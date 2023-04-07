package com.arek.legicrawler.service;

import com.arek.legicrawler.domain.Author;
import com.arek.legicrawler.domain.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Book}.
 */
public interface BookService {
    /**
     * Save a book.
     *
     * @param book the entity to save.
     * @return the persisted entity.
     */
    Book save(Book book);

    /**
     * Updates a book.
     *
     * @param book the entity to update.
     * @return the persisted entity.
     */
    Book update(Book book);

    /**
     * Partially updates a book.
     *
     * @param book the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Book> partialUpdate(Book book);

    /**
     * Get all the books.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Book> findAll(Pageable pageable);

    Page<Book> findAllByAuthor(Pageable pageable, List<Author> authors);
    Page<Book> findAllByTitle(Pageable pageable, String title);
    Page<Book> findAllByAuthorAndTitle(Pageable pageable, List<Author> authors, String title);
    Page<Book> findAll(Pageable pageable, List<String> authors, List<String> cycles, String title, String added);
    /**
     * Get the "id" book.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Book> findOne(String id);

    /**
     * Delete the "id" book.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    void reload(CycleService cycleService, AuthorService authorService);
}
