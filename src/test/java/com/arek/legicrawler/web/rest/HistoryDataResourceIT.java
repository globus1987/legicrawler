package com.arek.legicrawler.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.arek.legicrawler.IntegrationTest;
import com.arek.legicrawler.domain.HistoryData;
import com.arek.legicrawler.repository.HistoryDataRepository;
import java.util.List;
import java.util.UUID;
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
 * Integration tests for the {@link HistoryDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HistoryDataResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE_STRING = "AAAAAAAAAA";
    private static final String UPDATED_VALUE_STRING = "BBBBBBBBBB";

    private static final Integer DEFAULT_VALUE_INT = 1;
    private static final Integer UPDATED_VALUE_INT = 2;

    private static final String ENTITY_API_URL = "/api/history-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private HistoryDataRepository historyDataRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHistoryDataMockMvc;

    private HistoryData historyData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryData createEntity(EntityManager em) {
        HistoryData historyData = new HistoryData().key(DEFAULT_KEY).valueString(DEFAULT_VALUE_STRING).valueInt(DEFAULT_VALUE_INT);
        return historyData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryData createUpdatedEntity(EntityManager em) {
        HistoryData historyData = new HistoryData().key(UPDATED_KEY).valueString(UPDATED_VALUE_STRING).valueInt(UPDATED_VALUE_INT);
        return historyData;
    }

    @BeforeEach
    public void initTest() {
        historyData = createEntity(em);
    }

    @Test
    @Transactional
    void createHistoryData() throws Exception {
        int databaseSizeBeforeCreate = historyDataRepository.findAll().size();
        // Create the HistoryData
        restHistoryDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(historyData)))
            .andExpect(status().isCreated());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeCreate + 1);
        HistoryData testHistoryData = historyDataList.get(historyDataList.size() - 1);
        assertThat(testHistoryData.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testHistoryData.getValueString()).isEqualTo(DEFAULT_VALUE_STRING);
        assertThat(testHistoryData.getValueInt()).isEqualTo(DEFAULT_VALUE_INT);
    }

    @Test
    @Transactional
    void createHistoryDataWithExistingId() throws Exception {
        // Create the HistoryData with an existing ID
        historyData.setId("existing_id");

        int databaseSizeBeforeCreate = historyDataRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHistoryDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(historyData)))
            .andExpect(status().isBadRequest());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHistoryData() throws Exception {
        // Initialize the database
        historyData.setId(UUID.randomUUID().toString());
        historyDataRepository.saveAndFlush(historyData);

        // Get all the historyDataList
        restHistoryDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historyData.getId())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].valueString").value(hasItem(DEFAULT_VALUE_STRING)))
            .andExpect(jsonPath("$.[*].valueInt").value(hasItem(DEFAULT_VALUE_INT)));
    }

    @Test
    @Transactional
    void getHistoryData() throws Exception {
        // Initialize the database
        historyData.setId(UUID.randomUUID().toString());
        historyDataRepository.saveAndFlush(historyData);

        // Get the historyData
        restHistoryDataMockMvc
            .perform(get(ENTITY_API_URL_ID, historyData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(historyData.getId()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.valueString").value(DEFAULT_VALUE_STRING))
            .andExpect(jsonPath("$.valueInt").value(DEFAULT_VALUE_INT));
    }

    @Test
    @Transactional
    void getNonExistingHistoryData() throws Exception {
        // Get the historyData
        restHistoryDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHistoryData() throws Exception {
        // Initialize the database
        historyData.setId(UUID.randomUUID().toString());
        historyDataRepository.saveAndFlush(historyData);

        int databaseSizeBeforeUpdate = historyDataRepository.findAll().size();

        // Update the historyData
        HistoryData updatedHistoryData = historyDataRepository.findById(historyData.getId()).get();
        // Disconnect from session so that the updates on updatedHistoryData are not directly saved in db
        em.detach(updatedHistoryData);
        updatedHistoryData.key(UPDATED_KEY).valueString(UPDATED_VALUE_STRING).valueInt(UPDATED_VALUE_INT);

        restHistoryDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHistoryData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHistoryData))
            )
            .andExpect(status().isOk());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeUpdate);
        HistoryData testHistoryData = historyDataList.get(historyDataList.size() - 1);
        assertThat(testHistoryData.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testHistoryData.getValueString()).isEqualTo(UPDATED_VALUE_STRING);
        assertThat(testHistoryData.getValueInt()).isEqualTo(UPDATED_VALUE_INT);
    }

    @Test
    @Transactional
    void putNonExistingHistoryData() throws Exception {
        int databaseSizeBeforeUpdate = historyDataRepository.findAll().size();
        historyData.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoryDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, historyData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historyData))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHistoryData() throws Exception {
        int databaseSizeBeforeUpdate = historyDataRepository.findAll().size();
        historyData.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historyData))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHistoryData() throws Exception {
        int databaseSizeBeforeUpdate = historyDataRepository.findAll().size();
        historyData.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(historyData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHistoryDataWithPatch() throws Exception {
        // Initialize the database
        historyData.setId(UUID.randomUUID().toString());
        historyDataRepository.saveAndFlush(historyData);

        int databaseSizeBeforeUpdate = historyDataRepository.findAll().size();

        // Update the historyData using partial update
        HistoryData partialUpdatedHistoryData = new HistoryData();
        partialUpdatedHistoryData.setId(historyData.getId());

        partialUpdatedHistoryData.valueInt(UPDATED_VALUE_INT);

        restHistoryDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoryData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistoryData))
            )
            .andExpect(status().isOk());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeUpdate);
        HistoryData testHistoryData = historyDataList.get(historyDataList.size() - 1);
        assertThat(testHistoryData.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testHistoryData.getValueString()).isEqualTo(DEFAULT_VALUE_STRING);
        assertThat(testHistoryData.getValueInt()).isEqualTo(UPDATED_VALUE_INT);
    }

    @Test
    @Transactional
    void fullUpdateHistoryDataWithPatch() throws Exception {
        // Initialize the database
        historyData.setId(UUID.randomUUID().toString());
        historyDataRepository.saveAndFlush(historyData);

        int databaseSizeBeforeUpdate = historyDataRepository.findAll().size();

        // Update the historyData using partial update
        HistoryData partialUpdatedHistoryData = new HistoryData();
        partialUpdatedHistoryData.setId(historyData.getId());

        partialUpdatedHistoryData.key(UPDATED_KEY).valueString(UPDATED_VALUE_STRING).valueInt(UPDATED_VALUE_INT);

        restHistoryDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoryData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistoryData))
            )
            .andExpect(status().isOk());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeUpdate);
        HistoryData testHistoryData = historyDataList.get(historyDataList.size() - 1);
        assertThat(testHistoryData.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testHistoryData.getValueString()).isEqualTo(UPDATED_VALUE_STRING);
        assertThat(testHistoryData.getValueInt()).isEqualTo(UPDATED_VALUE_INT);
    }

    @Test
    @Transactional
    void patchNonExistingHistoryData() throws Exception {
        int databaseSizeBeforeUpdate = historyDataRepository.findAll().size();
        historyData.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoryDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, historyData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(historyData))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHistoryData() throws Exception {
        int databaseSizeBeforeUpdate = historyDataRepository.findAll().size();
        historyData.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(historyData))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHistoryData() throws Exception {
        int databaseSizeBeforeUpdate = historyDataRepository.findAll().size();
        historyData.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryDataMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(historyData))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoryData in the database
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHistoryData() throws Exception {
        // Initialize the database
        historyData.setId(UUID.randomUUID().toString());
        historyDataRepository.saveAndFlush(historyData);

        int databaseSizeBeforeDelete = historyDataRepository.findAll().size();

        // Delete the historyData
        restHistoryDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, historyData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HistoryData> historyDataList = historyDataRepository.findAll();
        assertThat(historyDataList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
