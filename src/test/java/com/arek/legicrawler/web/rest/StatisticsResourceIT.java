package com.arek.legicrawler.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.arek.legicrawler.IntegrationTest;
import com.arek.legicrawler.domain.Statistics;
import com.arek.legicrawler.repository.StatisticsRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StatisticsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StatisticsResourceIT {

    private static final LocalDate DEFAULT_ADDED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ADDED = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_COUNT = 1;
    private static final Integer UPDATED_COUNT = 2;

    private static final String ENTITY_API_URL = "/api/statistics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatisticsMockMvc;

    private Statistics statistics;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Statistics createEntity(EntityManager em) {
        Statistics statistics = new Statistics().added(DEFAULT_ADDED).count(DEFAULT_COUNT);
        return statistics;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Statistics createUpdatedEntity(EntityManager em) {
        Statistics statistics = new Statistics().added(UPDATED_ADDED).count(UPDATED_COUNT);
        return statistics;
    }

    @BeforeEach
    public void initTest() {
        statistics = createEntity(em);
    }

    @Test
    @Transactional
    void createStatistics() throws Exception {
        int databaseSizeBeforeCreate = statisticsRepository.findAll().size();
        // Create the Statistics
        restStatisticsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statistics)))
            .andExpect(status().isCreated());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeCreate + 1);
        Statistics testStatistics = statisticsList.get(statisticsList.size() - 1);
        assertThat(testStatistics.getAdded()).isEqualTo(DEFAULT_ADDED);
        assertThat(testStatistics.getCount()).isEqualTo(DEFAULT_COUNT);
    }

    @Test
    @Transactional
    void createStatisticsWithExistingId() throws Exception {
        // Create the Statistics with an existing ID
        statistics.setId(1L);

        int databaseSizeBeforeCreate = statisticsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatisticsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statistics)))
            .andExpect(status().isBadRequest());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStatistics() throws Exception {
        // Initialize the database
        statisticsRepository.saveAndFlush(statistics);

        // Get all the statisticsList
        restStatisticsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(statistics.getId().intValue())))
            .andExpect(jsonPath("$.[*].added").value(hasItem(DEFAULT_ADDED.toString())))
            .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)));
    }

    @Test
    @Transactional
    void getStatistics() throws Exception {
        // Initialize the database
        statisticsRepository.saveAndFlush(statistics);

        // Get the statistics
        restStatisticsMockMvc
            .perform(get(ENTITY_API_URL_ID, statistics.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(statistics.getId().intValue()))
            .andExpect(jsonPath("$.added").value(DEFAULT_ADDED.toString()))
            .andExpect(jsonPath("$.count").value(DEFAULT_COUNT));
    }

    @Test
    @Transactional
    void getNonExistingStatistics() throws Exception {
        // Get the statistics
        restStatisticsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStatistics() throws Exception {
        // Initialize the database
        statisticsRepository.saveAndFlush(statistics);

        int databaseSizeBeforeUpdate = statisticsRepository.findAll().size();

        // Update the statistics
        Statistics updatedStatistics = statisticsRepository.findById(statistics.getId()).get();
        // Disconnect from session so that the updates on updatedStatistics are not directly saved in db
        em.detach(updatedStatistics);
        updatedStatistics.added(UPDATED_ADDED).count(UPDATED_COUNT);

        restStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStatistics.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStatistics))
            )
            .andExpect(status().isOk());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeUpdate);
        Statistics testStatistics = statisticsList.get(statisticsList.size() - 1);
        assertThat(testStatistics.getAdded()).isEqualTo(UPDATED_ADDED);
        assertThat(testStatistics.getCount()).isEqualTo(UPDATED_COUNT);
    }

    @Test
    @Transactional
    void putNonExistingStatistics() throws Exception {
        int databaseSizeBeforeUpdate = statisticsRepository.findAll().size();
        statistics.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, statistics.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statistics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStatistics() throws Exception {
        int databaseSizeBeforeUpdate = statisticsRepository.findAll().size();
        statistics.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatisticsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(statistics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStatistics() throws Exception {
        int databaseSizeBeforeUpdate = statisticsRepository.findAll().size();
        statistics.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatisticsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statistics)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatisticsWithPatch() throws Exception {
        // Initialize the database
        statisticsRepository.saveAndFlush(statistics);

        int databaseSizeBeforeUpdate = statisticsRepository.findAll().size();

        // Update the statistics using partial update
        Statistics partialUpdatedStatistics = new Statistics();
        partialUpdatedStatistics.setId(statistics.getId());

        restStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatistics.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatistics))
            )
            .andExpect(status().isOk());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeUpdate);
        Statistics testStatistics = statisticsList.get(statisticsList.size() - 1);
        assertThat(testStatistics.getAdded()).isEqualTo(DEFAULT_ADDED);
        assertThat(testStatistics.getCount()).isEqualTo(DEFAULT_COUNT);
    }

    @Test
    @Transactional
    void fullUpdateStatisticsWithPatch() throws Exception {
        // Initialize the database
        statisticsRepository.saveAndFlush(statistics);

        int databaseSizeBeforeUpdate = statisticsRepository.findAll().size();

        // Update the statistics using partial update
        Statistics partialUpdatedStatistics = new Statistics();
        partialUpdatedStatistics.setId(statistics.getId());

        partialUpdatedStatistics.added(UPDATED_ADDED).count(UPDATED_COUNT);

        restStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStatistics.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStatistics))
            )
            .andExpect(status().isOk());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeUpdate);
        Statistics testStatistics = statisticsList.get(statisticsList.size() - 1);
        assertThat(testStatistics.getAdded()).isEqualTo(UPDATED_ADDED);
        assertThat(testStatistics.getCount()).isEqualTo(UPDATED_COUNT);
    }

    @Test
    @Transactional
    void patchNonExistingStatistics() throws Exception {
        int databaseSizeBeforeUpdate = statisticsRepository.findAll().size();
        statistics.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, statistics.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statistics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStatistics() throws Exception {
        int databaseSizeBeforeUpdate = statisticsRepository.findAll().size();
        statistics.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(statistics))
            )
            .andExpect(status().isBadRequest());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStatistics() throws Exception {
        int databaseSizeBeforeUpdate = statisticsRepository.findAll().size();
        statistics.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatisticsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(statistics))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Statistics in the database
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStatistics() throws Exception {
        // Initialize the database
        statisticsRepository.saveAndFlush(statistics);

        int databaseSizeBeforeDelete = statisticsRepository.findAll().size();

        // Delete the statistics
        restStatisticsMockMvc
            .perform(delete(ENTITY_API_URL_ID, statistics.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Statistics> statisticsList = statisticsRepository.findAll();
        assertThat(statisticsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
