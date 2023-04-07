package com.arek.legicrawler.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.arek.legicrawler.IntegrationTest;
import com.arek.legicrawler.domain.Collection;
import com.arek.legicrawler.repository.CollectionRepository;
import com.arek.legicrawler.service.CollectionService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CollectionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CollectionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/collections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CollectionRepository collectionRepository;

    @Mock
    private CollectionRepository collectionRepositoryMock;

    @Mock
    private CollectionService collectionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCollectionMockMvc;

    private Collection collection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collection createEntity(EntityManager em) {
        Collection collection = new Collection().name(DEFAULT_NAME).url(DEFAULT_URL);
        return collection;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collection createUpdatedEntity(EntityManager em) {
        Collection collection = new Collection().name(UPDATED_NAME).url(UPDATED_URL);
        return collection;
    }

    @BeforeEach
    public void initTest() {
        collection = createEntity(em);
    }

    @Test
    @Transactional
    void createCollection() throws Exception {
        int databaseSizeBeforeCreate = collectionRepository.findAll().size();
        // Create the Collection
        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isCreated());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeCreate + 1);
        Collection testCollection = collectionList.get(collectionList.size() - 1);
        assertThat(testCollection.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCollection.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void createCollectionWithExistingId() throws Exception {
        // Create the Collection with an existing ID
        collection.setId("existing_id");

        int databaseSizeBeforeCreate = collectionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCollections() throws Exception {
        // Initialize the database
        collection.setId(UUID.randomUUID().toString());
        collectionRepository.saveAndFlush(collection);

        // Get all the collectionList
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collection.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCollectionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(collectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCollectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(collectionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCollectionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(collectionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCollectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(collectionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCollection() throws Exception {
        // Initialize the database
        collection.setId(UUID.randomUUID().toString());
        collectionRepository.saveAndFlush(collection);

        // Get the collection
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL_ID, collection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(collection.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }

    @Test
    @Transactional
    void getNonExistingCollection() throws Exception {
        // Get the collection
        restCollectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCollection() throws Exception {
        // Initialize the database
        collection.setId(UUID.randomUUID().toString());
        collectionRepository.saveAndFlush(collection);

        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();

        // Update the collection
        Collection updatedCollection = collectionRepository.findById(collection.getId()).get();
        // Disconnect from session so that the updates on updatedCollection are not directly saved in db
        em.detach(updatedCollection);
        updatedCollection.name(UPDATED_NAME).url(UPDATED_URL);

        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCollection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
        Collection testCollection = collectionList.get(collectionList.size() - 1);
        assertThat(testCollection.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCollection.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void putNonExistingCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, collection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        collection.setId(UUID.randomUUID().toString());
        collectionRepository.saveAndFlush(collection);

        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();

        // Update the collection using partial update
        Collection partialUpdatedCollection = new Collection();
        partialUpdatedCollection.setId(collection.getId());

        partialUpdatedCollection.name(UPDATED_NAME);

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
        Collection testCollection = collectionList.get(collectionList.size() - 1);
        assertThat(testCollection.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCollection.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    void fullUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        collection.setId(UUID.randomUUID().toString());
        collectionRepository.saveAndFlush(collection);

        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();

        // Update the collection using partial update
        Collection partialUpdatedCollection = new Collection();
        partialUpdatedCollection.setId(collection.getId());

        partialUpdatedCollection.name(UPDATED_NAME).url(UPDATED_URL);

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
        Collection testCollection = collectionList.get(collectionList.size() - 1);
        assertThat(testCollection.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCollection.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    void patchNonExistingCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, collection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCollection() throws Exception {
        // Initialize the database
        collection.setId(UUID.randomUUID().toString());
        collectionRepository.saveAndFlush(collection);

        int databaseSizeBeforeDelete = collectionRepository.findAll().size();

        // Delete the collection
        restCollectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, collection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
