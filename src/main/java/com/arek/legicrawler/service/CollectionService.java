package com.arek.legicrawler.service;

import com.arek.legicrawler.domain.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Collection}.
 */
public interface CollectionService {
    /**
     * Save a collection.
     *
     * @param collection the entity to save.
     * @return the persisted entity.
     */
    Collection save(Collection collection);
    List<Collection> saveAll(List<Collection> collections);

    /**
     * Updates a collection.
     *
     * @param collection the entity to update.
     * @return the persisted entity.
     */
    Collection update(Collection collection);

    /**
     * Partially updates a collection.
     *
     * @param collection the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Collection> partialUpdate(Collection collection);

    /**
     * Get all the collections.
     *
     * @return the list of entities.
     */
    List<Collection> findAll();

    /**
     * Get all the collections with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Collection> findAllWithEagerRelationships(Pageable pageable);
    List<String> findIdList(String query);

    /**
     * Get the "id" collection.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Collection> findOne(String id);

    /**
     * Delete the "id" collection.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
    List<String> findAllIds();
}
