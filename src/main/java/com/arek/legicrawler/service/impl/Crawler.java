package com.arek.legicrawler.service.impl;

import com.arek.legicrawler.domain.Author;
import com.arek.legicrawler.domain.Book;
import com.arek.legicrawler.domain.Cycle;
import com.arek.legicrawler.service.AuthorService;
import com.arek.legicrawler.service.BookService;
import com.arek.legicrawler.service.CollectionService;
import com.arek.legicrawler.service.CycleService;
import com.google.gson.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class Crawler {

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);
    private static final String LEGIMI_CATALOGUE = "https://www.legimi.pl/api/catalogue";
    private static final List<String> LANGUAGES = List.of("\"polish\"");
    private static final List<String> FILTERS = List.of(
        "\"audiobooks\"",
        "\"ebooks\"",
        "\"epub\"",
        "\"mobi\"",
        "\"pdf\"",
        "\"synchrobooks\"",
        "\"unlimited\"",
        "\"unlimitedlegimi\""
    );
    private static final String legimiUrl = "https://www.legimi.pl";
    private List<Book> books = new ArrayList<>();
    private AtomicInteger counter;
    private List<String> existingIds;
    private CycleService cycleService;
    private BookService bookService;
    private AuthorService authorService;
    private CollectionService collectionService;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private WebClient webClient = WebClient.builder().build();
    private Map<String, com.arek.legicrawler.domain.Collection> collectionList = new HashMap<>();
    private Map<String, com.arek.legicrawler.domain.Author> authorList = new HashMap<>();
    private Map<String, com.arek.legicrawler.domain.Cycle> cycleList = new HashMap<>();
    private List<Book> bookList;
    private Set<String> idList = new HashSet<>();
    private List<String> existingAuthors;

    public Crawler(BookService bookService, AuthorService authorService, CycleService cycleService, CollectionService collectionService) {
        this.cycleService = cycleService;
        this.bookService = bookService;
        this.authorService = authorService;
        this.collectionService = collectionService;
    }

    public Crawler setBooks(List<Book> books) {
        this.books = books;
        return this;
    }

    public Crawler setExistingIds(List<String> existingIds) {
        this.existingIds = existingIds;
        this.counter = new AtomicInteger(0);
        return this;
    }

    public Crawler setExistingAuthors(List<String> allIds) {
        this.existingAuthors = allIds;
        return this;
    }

    public void parse(Set<String> ids, List<Book> bookList) {
        this.bookList = bookList;
        counter = new AtomicInteger(ids.size());
        Flux
            .fromIterable(ids)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .flatMap(this::fetchBooks)
            .ordered(String::compareTo)
            .toStream()
            .collect(Collectors.toList());
    }

    private Mono<String> fetchBooks(String id) {
        parseUrl(id);
        counter.getAndDecrement();
        if (counter.get() % 100 == 0) logger.info(counter.get() + " pages left to parse");
        return Mono.just("test");
    }

    private Mono<String> fetchIds(String url) {
        parseUrlForId(url);
        counter.getAndDecrement();
        if (counter.get() % 1000 == 0) logger.info(counter.get() + " ids left to parse");
        return Mono.just("test");
    }

    @Transactional
    public void parseUrl(String id) {
        var client = WebClient.builder().build();
        var gson = new GsonBuilder().create();
        if (existingIds.contains(id)) {
            return;
        }
        try {
            var bookDetailsJson = getBookDetails(client, id);
            if (bookDetailsJson.isJsonNull()) return;
            if (!bookDetailsJson.has("book")) return;
            var bookDetails = bookDetailsJson.getAsJsonObject("book");
            if (!bookDetails.has("audiobook") && bookDetails.has("ebook")) return;
            var audiobookFormat = bookDetails.get("audiobook").isJsonObject();
            var ebookFormat = bookDetails.get("ebook").isJsonObject();
            if (!ebookFormat && !audiobookFormat) {
                return;
            }
            var newBook = createBookDetails(bookDetails, id, bookDetails, audiobookFormat, ebookFormat);
            setAudiobookSubscriptions(bookDetails, audiobookFormat, newBook);
            setEbookSubscriptions(bookDetails, ebookFormat, newBook);
            bookService.save(newBook);
            try {
                setCycle(bookDetails, newBook);
            } catch (Exception exception) {
                logger.error("Cannot set cycle for " + id);
                exception.printStackTrace();
            }
            try {
                setAuthors(bookDetails, newBook);
            } catch (Exception exception) {
                logger.error("Cannot set authors for " + id);
                exception.printStackTrace();
            }
            //                try {
            //                    setCollections(bookDetailsJson.getAsJsonArray("bookCollections"), newBook);
            //                } catch (Exception exception) {
            //                    logger.error("Cannot set collections for " + bookElementId);
            //                    exception.printStackTrace();
            //                }
            bookList.add(newBook);
        } catch (Exception e) {
            logger.error("Cannot parse book {}", id);
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void parseUrlForId(String url) {
        var client = WebClient.builder().build();
        var gson = new GsonBuilder().create();
        var response = client.get().uri(url).retrieve().bodyToMono(String.class).block();
        var gsonValue = gson.fromJson(response, JsonObject.class);
        var books = gsonValue.get("bookList").getAsJsonObject().get("books").getAsJsonArray();
        idList.addAll(
            books.asList().stream().map(JsonElement::getAsJsonObject).map(e -> e.get("id").getAsString()).collect(Collectors.toList())
        );
    }

    private static JsonObject getBookDetails(WebClient client, String bookElementId) {
        return new GsonBuilder()
            .create()
            .fromJson(
                client.get().uri("https://www.legimi.pl/api/catalogue/book/" + bookElementId).retrieve().bodyToMono(String.class).block(),
                JsonObject.class
            );
    }

    private static Book createBookDetails(
        JsonObject bookObject,
        String bookElementId,
        JsonObject bookDetails,
        boolean audiobookFormat,
        boolean ebookFormat
    ) {
        var newBook = new Book();
        newBook.setTitle(bookObject.get("title").getAsString());
        newBook.setUrl(bookObject.get("url").getAsString());
        newBook.setId(bookElementId);
        newBook.setAdded(LocalDate.now());
        newBook.setAudiobook(audiobookFormat);
        newBook.setEbook(ebookFormat);

        if (!bookDetails.get("primaryCategory").isJsonNull()) {
            newBook.setCategory(bookDetails.get("primaryCategory").getAsJsonObject().get("name").getAsString());
        }
        return newBook;
    }

    private static void setAudiobookSubscriptions(JsonObject bookDetails, boolean audiobookFormat, Book newBook) {
        if (audiobookFormat && !bookDetails.get("audiobook").isJsonNull()) {
            newBook.setSubscription(bookDetails.get("audiobook").getAsJsonObject().get("isInSubscription").getAsBoolean());
            newBook.setLibrarySubscription(bookDetails.get("audiobook").getAsJsonObject().get("isInLibrarySubscription").getAsBoolean());
            newBook.setLibraryPass(bookDetails.get("audiobook").getAsJsonObject().get("isInLibraryPass").getAsBoolean());
            newBook.setKindleSubscription(bookDetails.get("audiobook").getAsJsonObject().get("isInKindleSubscription").getAsBoolean());
        }
    }

    private static void setEbookSubscriptions(JsonObject bookDetails, boolean ebookFormat, Book newBook) {
        if (ebookFormat && !bookDetails.get("ebook").isJsonNull()) {
            newBook.setSubscription(bookDetails.get("ebook").getAsJsonObject().get("isInSubscription").getAsBoolean());
            newBook.setLibrarySubscription(bookDetails.get("ebook").getAsJsonObject().get("isInLibrarySubscription").getAsBoolean());
            newBook.setLibraryPass(bookDetails.get("ebook").getAsJsonObject().get("isInLibraryPass").getAsBoolean());
            newBook.setKindleSubscription(bookDetails.get("ebook").getAsJsonObject().get("isInKindleSubscription").getAsBoolean());
        }
    }

    private Book saveBook(Book newBook) {
        return bookService.save(newBook);
    }

    private void setAuthors(JsonObject bookDetails, Book newBook) {
        var authors = bookDetails.getAsJsonArray("authors");
        if (authors != null && !authors.isJsonNull()) {
            for (var authorElement : authors) {
                var authorObject = authorElement.getAsJsonObject();
                var authorId = authorObject.get("id").getAsString();
                var author = existingAuthors.contains(authorId)
                    ? authorService.findOne(authorId).get()
                    : authorList.containsKey(authorId)
                        ? authorList.get(authorId)
                        : new Author(authorId, authorObject.get("name").getAsString(), legimiUrl + authorObject.get("url").getAsString());
                if (!authorList.containsKey(authorId)) {
                    authorList.put(authorId, author);
                    authorService.save(author);
                }
                author.addBooks(newBook);
            }
        }
    }

    private void setCollections(JsonArray collections, Book newBook) {
        if (collections.isEmpty()) return;
        for (var collectionElement : collections) {
            var collectionObject = collectionElement.getAsJsonObject();
            var collectionId = collectionObject.get("id").getAsString();
            var collection = collectionList.containsKey(collectionId)
                ? collectionList.get(collectionId)
                : collectionService
                    .findOne(collectionId)
                    .orElseGet(() ->
                        new com.arek.legicrawler.domain.Collection(
                            collectionId,
                            collectionObject.get("name").getAsString(),
                            legimiUrl + collectionObject.get("url").getAsString()
                        )
                    );
            collection.addBooks(newBook);
            if (!collectionList.containsKey(collectionId)) collectionList.put(collectionId, collection);
            newBook.addCollections(collection);
        }
    }

    private void setCycle(JsonObject bookDetails, Book newBook) {
        if (!bookDetails.get("cycle").isJsonNull()) {
            Cycle cycle = null;
            var cycleId = bookDetails.get("cycle").getAsJsonObject().get("id").getAsString();
            var cycleDb = cycleService.findOne(cycleId);
            if (cycleDb.isPresent()) {
                cycle = cycleDb.get();
            } else {
                cycle = new Cycle();
                cycle.setName(bookDetails.get("cycle").getAsJsonObject().get("name").getAsString());
                cycle.setUrl(legimiUrl + bookDetails.get("cycle").getAsJsonObject().get("url").getAsString());
                cycle.setId(cycleId);
                cycleService.save(cycle);
            }
            cycle.addBooks(newBook);
            saveBook(newBook);
        }
    }

    public void bookStats() {
        logger.info("Existing in database: " + existingIds.size());
        logger.info("Books parsed: " + books.size());
        var parsedIds = books.stream().map(Book::getId).collect(Collectors.toList());
        var newBooks = books.stream().filter(e -> !existingIds.contains(e.getId())).collect(Collectors.toList());
        logger.info("New books: " + newBooks.size());
    }

    public void reloadCycles(List<String> bookIds) {
        this.counter = new AtomicInteger(bookIds.size());
        Flux
            .fromIterable(bookIds)
            //            .parallel()
            //            .runOn(Schedulers.boundedElastic())
            .flatMap(this::fetchBooksById)
            //            .ordered(String::compareTo)
            .toStream()
            .collect(Collectors.toList());
    }

    private Mono<String> fetchBooksById(String id) {
        parseId(id);
        counter.getAndDecrement();
        if (counter.get() % 1000 == 0) logger.info(counter.get() + " left to parse");
        return Mono.just("test");
    }

    private boolean parseId(String id) {
        //        webClient = WebClient.builder().build();
        var client = webClient;

        var bookDetails = getBookDetails(client, id);

        if (bookDetails.get("cycle").isJsonNull()) return true;
        logger.info("2");
        var book = bookService.findOne(id);
        logger.info("3");
        if (book.isEmpty()) return false;
        var bookDb = book.get();
        logger.info("4");
        setCycle(bookDetails, bookDb);
        return true;
    }

    public Set<String> retrieveIdList(int pageCount) {
        var hrefList = new ArrayList<String>();
        for (int i = 1; i <= pageCount; i++) {
            var url = String.format(
                "%s?filters=[%s]&languages=[%s]&sort=latest&page=%d&&&skip=0",
                LEGIMI_CATALOGUE,
                String.join(",", FILTERS),
                String.join(",", LANGUAGES),
                i
            );
            hrefList.add(url);
        }
        counter = new AtomicInteger(hrefList.size());
        Flux
            .fromIterable(hrefList)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .flatMap(this::fetchIds)
            .ordered(String::compareTo)
            .toStream()
            .collect(Collectors.toList());
        return idList;
    }
}
