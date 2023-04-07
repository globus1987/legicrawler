package com.arek.legicrawler.web.rest;

import com.arek.legicrawler.domain.Collection;
import com.arek.legicrawler.repository.CollectionRepository;
import com.arek.legicrawler.service.CollectionService;
import com.arek.legicrawler.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.arek.legicrawler.domain.Collection}.
 */
@RestController
@RequestMapping("/api")
public class CollectionResource {

    private final Logger log = LoggerFactory.getLogger(CollectionResource.class);

    private static final String ENTITY_NAME = "collection";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CollectionService collectionService;

    private final CollectionRepository collectionRepository;

    public CollectionResource(CollectionService collectionService, CollectionRepository collectionRepository) {
        this.collectionService = collectionService;
        this.collectionRepository = collectionRepository;
    }

    /**
     * {@code POST  /collections} : Create a new collection.
     *
     * @param collection the collection to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new collection, or with status {@code 400 (Bad Request)} if the collection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/collections")
    public ResponseEntity<Collection> createCollection(@Valid @RequestBody Collection collection) throws URISyntaxException {
        log.debug("REST request to save Collection : {}", collection);
        if (collection.getId() != null) {
            throw new BadRequestAlertException("A new collection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Collection result = collectionService.save(collection);
        return ResponseEntity
            .created(new URI("/api/collections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /collections/:id} : Updates an existing collection.
     *
     * @param id the id of the collection to save.
     * @param collection the collection to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collection,
     * or with status {@code 400 (Bad Request)} if the collection is not valid,
     * or with status {@code 500 (Internal Server Error)} if the collection couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/collections/{id}")
    public ResponseEntity<Collection> updateCollection(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Collection collection
    ) throws URISyntaxException {
        log.debug("REST request to update Collection : {}, {}", id, collection);
        if (collection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collection.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!collectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Collection result = collectionService.update(collection);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, collection.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /collections/:id} : Partial updates given fields of an existing collection, field will ignore if it is null
     *
     * @param id the id of the collection to save.
     * @param collection the collection to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collection,
     * or with status {@code 400 (Bad Request)} if the collection is not valid,
     * or with status {@code 404 (Not Found)} if the collection is not found,
     * or with status {@code 500 (Internal Server Error)} if the collection couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/collections/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Collection> partialUpdateCollection(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Collection collection
    ) throws URISyntaxException {
        log.debug("REST request to partial update Collection partially : {}, {}", id, collection);
        if (collection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collection.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!collectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Collection> result = collectionService.partialUpdate(collection);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, collection.getId())
        );
    }

    /**
     * {@code GET  /collections} : get all the collections.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of collections in body.
     */
    @GetMapping("/collections")
    public List<Collection> getAllCollections(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Collections");
        return collectionService.findAll();
    }

    /**
     * {@code GET  /collections/:id} : get the "id" collection.
     *
     * @param id the id of the collection to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the collection, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/collections/{id}")
    public ResponseEntity<Collection> getCollection(@PathVariable String id) {
        log.debug("REST request to get Collection : {}", id);
        Optional<Collection> collection = collectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(collection);
    }

    /**
     * {@code DELETE  /collections/:id} : delete the "id" collection.
     *
     * @param id the id of the collection to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/collections/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable String id) {
        log.debug("REST request to delete Collection : {}", id);
        collectionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
