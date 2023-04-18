package com.arek.legicrawler.web.rest;

import com.arek.legicrawler.domain.History;
import com.arek.legicrawler.repository.HistoryRepository;
import com.arek.legicrawler.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.arek.legicrawler.domain.History}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HistoryResource {

    private final Logger log = LoggerFactory.getLogger(HistoryResource.class);

    private static final String ENTITY_NAME = "history";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HistoryRepository historyRepository;

    public HistoryResource(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * {@code POST  /histories} : Create a new history.
     *
     * @param history the history to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new history, or with status {@code 400 (Bad Request)} if the history has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/histories")
    public ResponseEntity<History> createHistory(@RequestBody History history) throws URISyntaxException {
        log.debug("REST request to save History : {}", history);
        if (history.getId() != null) {
            throw new BadRequestAlertException("A new history cannot already have an ID", ENTITY_NAME, "idexists");
        }
        History result = historyRepository.save(history);
        return ResponseEntity
            .created(new URI("/api/histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /histories/:id} : Updates an existing history.
     *
     * @param id the id of the history to save.
     * @param history the history to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated history,
     * or with status {@code 400 (Bad Request)} if the history is not valid,
     * or with status {@code 500 (Internal Server Error)} if the history couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/histories/{id}")
    public ResponseEntity<History> updateHistory(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody History history
    ) throws URISyntaxException {
        log.debug("REST request to update History : {}, {}", id, history);
        if (history.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, history.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        history.setIsPersisted();
        History result = historyRepository.save(history);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, history.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /histories/:id} : Partial updates given fields of an existing history, field will ignore if it is null
     *
     * @param id the id of the history to save.
     * @param history the history to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated history,
     * or with status {@code 400 (Bad Request)} if the history is not valid,
     * or with status {@code 404 (Not Found)} if the history is not found,
     * or with status {@code 500 (Internal Server Error)} if the history couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<History> partialUpdateHistory(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody History history
    ) throws URISyntaxException {
        log.debug("REST request to partial update History partially : {}, {}", id, history);
        if (history.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, history.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<History> result = historyRepository
            .findById(history.getId())
            .map(existingHistory -> {
                if (history.getTimeStamp() != null) {
                    existingHistory.setTimeStamp(history.getTimeStamp());
                }

                return existingHistory;
            })
            .map(historyRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, history.getId())
        );
    }

    /**
     * {@code GET  /histories} : get all the histories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of histories in body.
     */
    @GetMapping("/histories")
    public List<History> getAllHistories() {
        log.debug("REST request to get all Histories");
        return historyRepository
            .findAll()
            .stream()
            .sorted(Comparator.comparing(History::getTimeStamp).reversed())
            .collect(Collectors.toList());
    }

    /**
     * {@code GET  /histories/:id} : get the "id" history.
     *
     * @param id the id of the history to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the history, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/histories/{id}")
    public ResponseEntity<History> getHistory(@PathVariable String id) {
        log.debug("REST request to get History : {}", id);
        Optional<History> history = historyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(history);
    }

    /**
     * {@code DELETE  /histories/:id} : delete the "id" history.
     *
     * @param id the id of the history to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/histories/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable String id) {
        log.debug("REST request to delete History : {}", id);
        historyRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
