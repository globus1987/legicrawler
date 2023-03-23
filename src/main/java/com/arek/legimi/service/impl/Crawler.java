package com.arek.legimi.service.impl;

import com.arek.legimi.domain.Author;
import com.arek.legimi.domain.Book;
import com.arek.legimi.domain.Cycle;
import com.arek.legimi.repository.BookRepository;
import com.arek.legimi.service.AuthorService;
import com.arek.legimi.service.BookService;
import com.arek.legimi.service.CycleService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
    private List<Book> books = new ArrayList<>();
    private List<Book> databaseBooks = new ArrayList<>();
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

    private static final String legimiUrl = "https://www.legimi.pl";

    private Mono<String> getBooks(String url) {
        books.addAll(parseUrl(url));
        counter.getAndDecrement();
        logger.info(counter.get() + " left to parse");
        return Mono.just("test");
    }

    private Collection<? extends Book> parseUrl(String url) {
        var bookList = new ArrayList<Book>();
        var client = WebClient.builder().build();
        var gson = new GsonBuilder().create();
        var response = client.get().uri(url).retrieve().bodyToMono(String.class).block();
        var gsonValue = gson.fromJson(response, JsonObject.class);
        var books = gsonValue.get("bookList").getAsJsonObject().get("books").getAsJsonArray();

        for (var book : books) {
            var bookObject = book.getAsJsonObject();
            if (existingIds.stream().anyMatch(e -> e.equals(bookObject.get("id").getAsString()))) {
                continue;
            }
            try {
                var bookDetails = gson
                    .fromJson(
                        WebClient
                            .builder()
                            .build()
                            .get()
                            .uri("https://www.legimi.pl/api/catalogue/book/" + bookObject.get("id").getAsString())
                            .retrieve()
                            .bodyToMono(String.class)
                            .block(),
                        JsonObject.class
                    )
                    .get("book")
                    .getAsJsonObject();
                boolean audiobookFormat = bookObject.get("audiobookFormat").getAsBoolean();
                boolean ebookFormat = bookObject.get("ebookFormat").getAsBoolean();
                if (!ebookFormat && !audiobookFormat) {
                    continue;
                }
                var newBook = new Book();
                newBook.setTitle(bookObject.get("title").getAsString());
                newBook.setUrl(legimiUrl + bookObject.get("url").getAsString());
                newBook.setId(bookObject.get("id").getAsString());
                newBook.setAdded(LocalDate.now());
                newBook.setAudiobook(audiobookFormat);
                newBook.setEbook(ebookFormat);
                if (!bookDetails.get("primaryCategory").isJsonNull()) newBook.setCategory(
                    bookDetails.get("primaryCategory").getAsJsonObject().get("name").getAsString()
                );

                if (audiobookFormat && !bookDetails.get("audiobook").isJsonNull()) {
                    newBook.setSubscription(bookDetails.get("audiobook").getAsJsonObject().get("isInSubscription").getAsBoolean());
                    newBook.setLibrarySubscription(
                        bookDetails.get("audiobook").getAsJsonObject().get("isInLibrarySubscription").getAsBoolean()
                    );
                    newBook.setLibraryPass(bookDetails.get("audiobook").getAsJsonObject().get("isInLibraryPass").getAsBoolean());
                    newBook.setKindleSubscription(
                        bookDetails.get("audiobook").getAsJsonObject().get("isInKindleSubscription").getAsBoolean()
                    );
                }
                if (ebookFormat && !bookDetails.get("ebook").isJsonNull()) {
                    newBook.setSubscription(bookDetails.get("ebook").getAsJsonObject().get("isInSubscription").getAsBoolean());
                    newBook.setLibrarySubscription(
                        bookDetails.get("ebook").getAsJsonObject().get("isInLibrarySubscription").getAsBoolean()
                    );
                    newBook.setLibraryPass(bookDetails.get("ebook").getAsJsonObject().get("isInLibraryPass").getAsBoolean());
                    newBook.setKindleSubscription(bookDetails.get("ebook").getAsJsonObject().get("isInKindleSubscription").getAsBoolean());
                }

                bookService.save(newBook);

                var authorList = new HashSet<Author>();
                if (!bookDetails.get("authors").getAsJsonArray().isEmpty()) {
                    for (var author : bookDetails
                        .get("authors")
                        .getAsJsonArray()
                        .asList()
                        .stream()
                        .map(JsonElement::getAsJsonObject)
                        .collect(Collectors.toList())) {
                        Author authorToBeSet = null;
                        var authorId = author.get("id").getAsString();
                        var authorDb = authorService.findOne(authorId);
                        if (authorDb.isPresent()) {
                            authorList.add(authorDb.get());
                        } else {
                            authorList.add(
                                new Author(authorId, author.get("name").getAsString(), legimiUrl + author.get("url").getAsString())
                            );
                        }
                    }
                }
                authorList.forEach(e -> authorService.save(e));

                newBook.setAuthors(authorList);
                authorList.forEach(e -> e.addBooks(newBook));
                bookService.save(newBook);
                authorList.forEach(e -> authorService.save(e));

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
                bookService.save(newBook);
                bookList.add(newBook);
            } catch (Exception e) {
                logger.error(
                    "Cannot parse book {} {} {}",
                    bookObject.get("id").getAsString(),
                    bookObject.get("title").getAsString(),
                    bookObject.get("url").getAsString()
                );
                logger.error(e.getMessage());
            }
        }

        return bookList;
    }

    public Crawler setBooks(List<Book> books) {
        this.books = books;
        return this;
    }

    public List<Book> getDatabaseBooks() {
        return databaseBooks;
    }

    public Crawler setDatabaseBooks(List<Book> databaseBooks) {
        this.databaseBooks = databaseBooks;
        this.existingIds = databaseBooks.stream().map(Book::getId).collect(Collectors.toList());
        this.counter = new AtomicInteger(0);
        return this;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public Crawler setCounter(AtomicInteger counter) {
        this.counter = counter;
        return this;
    }

    public List<String> getExistingIds() {
        return existingIds;
    }

    public Crawler setExistingIds(List<String> existingIds) {
        this.existingIds = existingIds;
        return this;
    }

    public void parse(int pageCount) {
        var hrefList = new ArrayList<String>();
        for (int i = 1; i <= pageCount; i++) {
            hrefList.add(
                "https://www.legimi.pl/api/catalogue?filters=[\"audiobooks\",\"ebooks\",\"epub\",\"mobi\",\"pdf\",\"synchrobooks\",\"unlimited\",\"unlimitedlegimi\"]&languages=[\"polish\"]&sort=latest&page=" +
                i +
                "&&&skip=0"
            );
        }
        var restTemplate = new RestTemplateBuilder().build();
        counter = new AtomicInteger(hrefList.size());
        Flux
            .fromIterable(hrefList)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .flatMap(this::getBooks)
            .ordered(String::compareTo)
            .toStream()
            .collect(Collectors.toList());
    }

    public void bookStats() {
        logger.info("Existing in database: " + existingIds.size());
        logger.info("Books parsed: " + books.size());
        var parsedIds = books.stream().map(Book::getId).collect(Collectors.toList());
        var deletedBooks = databaseBooks.stream().filter(e -> !parsedIds.contains(e.getId())).collect(Collectors.toList());
        var newBooks = books.stream().filter(e -> !existingIds.contains(e.getId())).collect(Collectors.toList());
        var gson = new GsonBuilder().setPrettyPrinting().create();
        logger.info("New books: " + newBooks.size());
        logger.info("Deleted books: " + deletedBooks.size());
    }
}
