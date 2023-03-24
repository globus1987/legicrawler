package com.arek.legimi.service.impl;

import com.arek.legimi.domain.Book;
import com.arek.legimi.repository.BookRepository;
import com.arek.legimi.service.AuthorService;
import com.arek.legimi.service.BookService;
import com.arek.legimi.service.CycleService;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void reload(CycleService cycleService, AuthorService authorService) {
        var restTemplate = new RestTemplateBuilder().build();
        var response = restTemplate.getForObject(
            "https://www.legimi.pl/api/catalogue?filters=[\"audiobooks\",\"ebooks\",\"epub\",\"mobi\",\"pdf\",\"synchrobooks\",\"unlimited\",\"unlimitedlegimi\"]&languages=[\"polish\"]&sort=latest&&&skip=0",
            String.class
        );
        var gson = new GsonBuilder().create();
        var gsonValue = gson.fromJson(response, JsonObject.class);
        var pageCount = gsonValue.get("bookList").getAsJsonObject().get("pagination").getAsJsonObject().get("totalPages").getAsInt();
        var crawler = new Crawler(this, authorService, cycleService);
        crawler.setExistingIds(bookRepository.findAllIds());
        crawler.parse(pageCount);
        crawler.bookStats();
    }
}
