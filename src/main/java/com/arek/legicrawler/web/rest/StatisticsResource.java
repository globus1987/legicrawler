package com.arek.legicrawler.web.rest;

import com.arek.legicrawler.domain.Statistics;
import com.arek.legicrawler.repository.BookRepository;
import com.google.gson.Gson;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final BookRepository bookRepository;

    public StatisticsResource(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * {@code POST  /statistics} : Create a new statistics.
     *
     * @param statistics the statistics to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statistics, or with status {@code 400 (Bad Request)} if the statistics has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */

    /**
     * {@code GET  /statistics} : get all the statistics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statistics in body.
     */
    @GetMapping("/statistics")
    public List<Statistics> getAllStatistics() {
        log.debug("REST request to get all Statistics");
        return bookRepository.countBooksByDay().stream().filter(e -> e.getCount() < 50000).collect(Collectors.toList());
    }
}
