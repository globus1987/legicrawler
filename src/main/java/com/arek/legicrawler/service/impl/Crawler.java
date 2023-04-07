package com.arek.legicrawler.service.impl;

import com.arek.legicrawler.domain.Author;
import com.arek.legicrawler.domain.Book;
import com.arek.legicrawler.domain.Cycle;
import com.arek.legicrawler.service.AuthorService;
import com.arek.legicrawler.service.BookService;
import com.arek.legicrawler.service.CycleService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

    public Crawler(BookService bookService, AuthorService authorService, CycleService cycleService) {
        this.cycleService = cycleService;
        this.bookService = bookService;
        this.authorService = authorService;
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

    public void parse(int pageCount) {
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
        var restTemplate = new RestTemplateBuilder().build();
        counter = new AtomicInteger(hrefList.size());
        Flux
            .fromIterable(hrefList)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .flatMap(this::fetchBooks)
            .ordered(String::compareTo)
            .toStream()
            .collect(Collectors.toList());
    }

    private Mono<String> fetchBooks(String url) {
        books.addAll(parseUrl(url));
        counter.getAndDecrement();
        if (counter.get() % 100 == 0) logger.info(counter.get() + " left to parse");
        return Mono.just("test");
    }

    private Collection<? extends Book> parseUrl(String url) {
        var client = WebClient.builder().build();
        var gson = new GsonBuilder().create();
        var response = client.get().uri(url).retrieve().bodyToMono(String.class).block();
        var gsonValue = gson.fromJson(response, JsonObject.class);
        var books = gsonValue.get("bookList").getAsJsonObject().get("books").getAsJsonArray();

        var bookList = new ArrayList<Book>();
        for (var bookElement : books) {
            var bookObject = bookElement.getAsJsonObject();
            var bookElementId = bookObject.get("id").getAsString();
            if (existingIds.contains(bookElementId)) {
                continue;
            }
            try {
                var bookDetails = getBookDetails(client, bookElementId);
                var audiobookFormat = bookObject.get("audiobookFormat").getAsBoolean();
                var ebookFormat = bookObject.get("ebookFormat").getAsBoolean();
                if (!ebookFormat && !audiobookFormat) {
                    continue;
                }
                var newBook = createBookDetails(bookObject, bookElementId, bookDetails, audiobookFormat, ebookFormat);
                setAudiobookSubscriptions(bookDetails, audiobookFormat, newBook);
                setEbookSubscriptions(bookDetails, ebookFormat, newBook);
                saveBook(newBook);
                setAuthors(bookDetails, newBook);
                setCycle(bookList, bookDetails, newBook);
            } catch (Exception e) {
                logger.error(
                    "Cannot parse book {} {} {}",
                    bookElementId,
                    bookObject.get("title").getAsString(),
                    bookObject.get("url").getAsString()
                );
                logger.error(e.getMessage());
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }

        return bookList;
    }

    private static JsonObject getBookDetails(WebClient client, String bookElementId) {
        return new GsonBuilder()
            .create()
            .fromJson(
                client.get().uri("https://www.legimi.pl/api/catalogue/book/" + bookElementId).retrieve().bodyToMono(String.class).block(),
                JsonObject.class
            )
            .getAsJsonObject("book");
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
        newBook.setUrl(legimiUrl + bookObject.get("url").getAsString());
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
        var authorList = new ArrayList<Author>();
        var authors = bookDetails.getAsJsonArray("authors");
        if (authors != null && !authors.isJsonNull()) {
            for (var authorElement : authors) {
                var authorObject = authorElement.getAsJsonObject();
                var authorId = authorObject.get("id").getAsString();
                var author = authorService
                    .findOne(authorId)
                    .orElseGet(() ->
                        new Author(authorId, authorObject.get("name").getAsString(), legimiUrl + authorObject.get("url").getAsString())
                    );
                authorList.add(author);
            }
        }

        newBook.setAuthors(new HashSet<>(authorList));
        authorList.forEach(e -> e.addBooks(newBook));
        authorService.saveAll(authorList);
        saveBook(newBook);
    }

    private void setCycle(ArrayList<Book> bookList, JsonObject bookDetails, Book newBook) {
        Cycle cycle = null;
        if (!bookDetails.get("cycle").isJsonNull()) {
            var cycleId = bookDetails.get("cycle").getAsJsonObject().get("id").getAsString();
            var cycleDb = cycleService.findOne(cycleId);
            if (cycleDb.isPresent()) {
                cycle = cycleDb.get();
            } else {
                cycle = new Cycle();
                cycle.setName(bookDetails.get("cycle").getAsJsonObject().get("name").getAsString());
                cycle.setUrl(legimiUrl + bookDetails.get("cycle").getAsJsonObject().get("url").getAsString());
                cycle.setId(cycleId);
            }
            cycle.addBooks(newBook);
        }
        saveBook(newBook);
        bookList.add(newBook);
    }

    public void bookStats() {
        logger.info("Existing in database: " + existingIds.size());
        logger.info("Books parsed: " + books.size());
        var parsedIds = books.stream().map(Book::getId).collect(Collectors.toList());
        var deletedBooks = existingIds.stream().filter(e -> !parsedIds.contains(e)).collect(Collectors.toList());
        var newBooks = books.stream().filter(e -> !existingIds.contains(e.getId())).collect(Collectors.toList());
        var gson = new GsonBuilder().setPrettyPrinting().create();
        logger.info("New books: " + newBooks.size());
        logger.info("Deleted books: " + deletedBooks.size());
    }
}
