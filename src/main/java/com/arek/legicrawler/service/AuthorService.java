package com.arek.legicrawler.service;

import com.arek.legicrawler.domain.Author;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Author}.
 */
public interface AuthorService {
    /**
     * Save a author.
     *
     * @param author the entity to save.
     * @return the persisted entity.
     */
    Author save(Author author);
    List<Author> saveAll(List<Author> authors);

    /**
     * Updates a author.
     *
     * @param author the entity to update.
     * @return the persisted entity.
     */
    Author update(Author author);

    /**
     * Partially updates a author.
     *
     * @param author the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Author> partialUpdate(Author author);

    /**
     * Get all the authors.
     *
     * @param pageable the pagination information.
     * @param query
     * @return the list of entities.
     */
    Page<Author> findAll(Pageable pageable, String query);
    List<Author> findAll(String query);
    List<String> findIdList(String query);

    /**
     * Get all the authors with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @param query
     * @return the list of entities.
     */
    Page<Author> findAllWithEagerRelationships(Pageable pageable, String query);

    /**
     * Get the "id" author.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Author> findOne(String id);

    /**
     * Delete the "id" author.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    List<String> findAllIds();
}
