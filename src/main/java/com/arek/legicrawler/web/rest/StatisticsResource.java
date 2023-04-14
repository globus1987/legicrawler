package com.arek.legicrawler.web.rest;

import com.arek.legicrawler.domain.Statistics;
import com.arek.legicrawler.repository.StatisticsRepository;
import com.arek.legicrawler.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.arek.legicrawler.domain.Statistics}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class StatisticsResource {

    private final Logger log = LoggerFactory.getLogger(StatisticsResource.class);

    private static final String ENTITY_NAME = "statistics";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatisticsRepository statisticsRepository;

    public StatisticsResource(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    /**
     * {@code POST  /statistics} : Create a new statistics.
     *
     * @param statistics the statistics to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statistics, or with status {@code 400 (Bad Request)} if the statistics has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/statistics")
    public ResponseEntity<Statistics> createStatistics(@RequestBody Statistics statistics) throws URISyntaxException {
        log.debug("REST request to save Statistics : {}", statistics);
        if (statistics.getId() != null) {
            throw new BadRequestAlertException("A new statistics cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Statistics result = statisticsRepository.save(statistics);
        return ResponseEntity
            .created(new URI("/api/statistics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /statistics/:id} : Updates an existing statistics.
     *
     * @param id the id of the statistics to save.
     * @param statistics the statistics to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statistics,
     * or with status {@code 400 (Bad Request)} if the statistics is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statistics couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/statistics/{id}")
    public ResponseEntity<Statistics> updateStatistics(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Statistics statistics
    ) throws URISyntaxException {
        log.debug("REST request to update Statistics : {}, {}", id, statistics);
        if (statistics.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statistics.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statisticsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Statistics result = statisticsRepository.save(statistics);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, statistics.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /statistics/:id} : Partial updates given fields of an existing statistics, field will ignore if it is null
     *
     * @param id the id of the statistics to save.
     * @param statistics the statistics to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statistics,
     * or with status {@code 400 (Bad Request)} if the statistics is not valid,
     * or with status {@code 404 (Not Found)} if the statistics is not found,
     * or with status {@code 500 (Internal Server Error)} if the statistics couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/statistics/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Statistics> partialUpdateStatistics(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Statistics statistics
    ) throws URISyntaxException {
        log.debug("REST request to partial update Statistics partially : {}, {}", id, statistics);
        if (statistics.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statistics.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statisticsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Statistics> result = statisticsRepository
            .findById(statistics.getId())
            .map(existingStatistics -> {
                if (statistics.getAdded() != null) {
                    existingStatistics.setAdded(statistics.getAdded());
                }
                if (statistics.getCount() != null) {
                    existingStatistics.setCount(statistics.getCount());
                }

                return existingStatistics;
            })
            .map(statisticsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, statistics.getId().toString())
        );
    }

    /**
     * {@code GET  /statistics} : get all the statistics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statistics in body.
     */
    @GetMapping("/statistics")
    public List<Statistics> getAllStatistics() {
        log.debug("REST request to get all Statistics");
        return statisticsRepository.findAll();
    }

    /**
     * {@code GET  /statistics/:id} : get the "id" statistics.
     *
     * @param id the id of the statistics to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statistics, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/statistics/{id}")
    public ResponseEntity<Statistics> getStatistics(@PathVariable Long id) {
        log.debug("REST request to get Statistics : {}", id);
        Optional<Statistics> statistics = statisticsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(statistics);
    }

    /**
     * {@code DELETE  /statistics/:id} : delete the "id" statistics.
     *
     * @param id the id of the statistics to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/statistics/{id}")
    public ResponseEntity<Void> deleteStatistics(@PathVariable Long id) {
        log.debug("REST request to delete Statistics : {}", id);
        statisticsRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
