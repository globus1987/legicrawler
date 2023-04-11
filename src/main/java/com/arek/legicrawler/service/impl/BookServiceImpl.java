package com.arek.legicrawler.service.impl;

import com.arek.legicrawler.domain.Author;
import com.arek.legicrawler.domain.Book;
import com.arek.legicrawler.repository.BookRepository;
import com.arek.legicrawler.service.AuthorService;
import com.arek.legicrawler.service.BookService;
import com.arek.legicrawler.service.CollectionService;
import com.arek.legicrawler.service.CycleService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Book}.
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        log.debug("Request to save Book : {}", book);
        return bookRepository.save(book);
    }

    @Override
    public Book update(Book book) {
        log.debug("Request to update Book : {}", book);
        book.setIsPersisted();
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> partialUpdate(Book book) {
        log.debug("Request to partially update Book : {}", book);

        return bookRepository
            .findById(book.getId())
            .map(existingBook -> {
                if (book.getTitle() != null) {
                    existingBook.setTitle(book.getTitle());
                }
                if (book.getUrl() != null) {
                    existingBook.setUrl(book.getUrl());
                }
                if (book.getEbook() != null) {
                    existingBook.setEbook(book.getEbook());
                }
                if (book.getAudiobook() != null) {
                    existingBook.setAudiobook(book.getAudiobook());
                }
                if (book.getCategory() != null) {
                    existingBook.setCategory(book.getCategory());
                }
                if (book.getAdded() != null) {
                    existingBook.setAdded(book.getAdded());
                }
                if (book.getKindleSubscription() != null) {
                    existingBook.setKindleSubscription(book.getKindleSubscription());
                }
                if (book.getLibraryPass() != null) {
                    existingBook.setLibraryPass(book.getLibraryPass());
                }
                if (book.getLibrarySubscription() != null) {
                    existingBook.setLibrarySubscription(book.getLibrarySubscription());
                }
                if (book.getSubscription() != null) {
                    existingBook.setSubscription(book.getSubscription());
                }

                return existingBook;
            })
            .map(bookRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAll(Pageable pageable) {
        log.debug("Request to get all Books");
        return bookRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAllByAuthor(Pageable pageable, List<Author> authors) {
        log.debug("Request to get all Books");
        return bookRepository.findAllByAuthors(pageable, authors);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAllByTitle(Pageable pageable, String filterTitle) {
        log.debug("Request to get all Books");
        return bookRepository.findAllByTitleContainingIgnoreCase(pageable, filterTitle);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAllByAuthorAndTitle(Pageable pageable, List<Author> authors, String filterTitle) {
        log.debug("Request to get all Books");
        return bookRepository.findAllByTitleContainingIgnoreCaseAndAuthorsContainingIgnoreCase(pageable, authors, filterTitle);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAll(Pageable pageable, List<String> authors, List<String> cycles, String filterTitle, String added) {
        log.debug("Request to get all Books");
        var bookspec = BookSpecification.filterByTitle(filterTitle); // filter by title
        if (!added.isEmpty()) bookspec =
            bookspec.and(BookSpecification.filterByAdded(LocalDate.parse(added, DateTimeFormatter.ofPattern("d/M/yyyy")))); // filter by added date

        if (!authors.isEmpty()) bookspec = bookspec.and(BookSpecification.filterByAuthors(authors));
        if (!cycles.isEmpty()) bookspec = bookspec.and(BookSpecification.filterByCycles(cycles));
        return bookRepository.findAll(
            bookspec, // filter by author name,
            pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findOne(String id) {
        log.debug("Request to get Book : {}", id);
        return bookRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete Book : {}", id);
        bookRepository.deleteById(id);
    }

    @Override
    public void reload(CycleService cycleService, AuthorService authorService, CollectionService collectionService) {
        var restTemplate = new RestTemplateBuilder().build();
        var response = restTemplate.getForObject(
            "https://www.legimi.pl/api/catalogue?filters=[\"audiobooks\",\"ebooks\",\"epub\",\"mobi\",\"pdf\",\"synchrobooks\",\"unlimited\",\"unlimitedlegimi\"]&languages=[\"polish\"]&sort=latest&&&skip=0",
            String.class
        );
        var gson = new GsonBuilder().create();
        var gsonValue = gson.fromJson(response, JsonObject.class);
        var pageCount = gsonValue.get("bookList").getAsJsonObject().get("pagination").getAsJsonObject().get("totalPages").getAsInt();
        var crawler = new Crawler(this, authorService, cycleService, collectionService);
        crawler.setExistingIds(bookRepository.findAllIds());
        crawler.parse(pageCount);
        crawler.bookStats();
    }

    @Override
    public void reloadCycles(CycleService cycleService, AuthorService authorService) {
        var bookIds = bookRepository.findAllIdsWithNullCycle();
        var crawler = new Crawler(this, null, cycleService, null);
        crawler.reloadCycles(bookIds);
    }

    @Override
    public void reloadCollections(CollectionService collectionService, AuthorService authorService) {}

    @Override
    public boolean existsByIdAndCycleIsNull(String id) {
        return bookRepository.existsByIdAndCycleIsNull(id);
    }

    @Override
    public boolean existsByIdAndCycleIsNotNull(String id) {
        return bookRepository.existsByIdAndCycleIsNotNull(id);
    }
}
