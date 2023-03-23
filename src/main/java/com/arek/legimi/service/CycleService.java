package com.arek.legimi.service;

import com.arek.legimi.domain.Cycle;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Cycle}.
 */
public interface CycleService {
    /**
     * Save a cycle.
     *
     * @param cycle the entity to save.
     * @return the persisted entity.
     */
    Cycle save(Cycle cycle);

    /**
     * Updates a cycle.
     *
     * @param cycle the entity to update.
     * @return the persisted entity.
     */
    Cycle update(Cycle cycle);

    /**
     * Partially updates a cycle.
     *
     * @param cycle the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Cycle> partialUpdate(Cycle cycle);

    /**
     * Get all the cycles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Cycle> findAll(Pageable pageable);
    Page<Cycle> findAll(Pageable pageable, String query);

    /**
     * Get the "id" cycle.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Cycle> findOne(String id);

    /**
     * Delete the "id" cycle.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
