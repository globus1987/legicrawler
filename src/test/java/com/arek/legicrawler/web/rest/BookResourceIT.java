package com.arek.legicrawler.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.arek.legicrawler.IntegrationTest;
import com.arek.legicrawler.domain.Book;
import com.arek.legicrawler.repository.BookRepository;
import com.arek.legicrawler.service.BookService;
import java.time.LocalDate;
import java.time.ZoneId;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_IMGSRC = "AAAAAAAAAA";
    private static final String UPDATED_IMGSRC = "BBBBBBBBBB";

    private static final Boolean DEFAULT_EBOOK = false;
    private static final Boolean UPDATED_EBOOK = true;

    private static final Boolean DEFAULT_AUDIOBOOK = false;
    private static final Boolean UPDATED_AUDIOBOOK = true;

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ADDED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ADDED = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_KINDLE_SUBSCRIPTION = false;
    private static final Boolean UPDATED_KINDLE_SUBSCRIPTION = true;

    private static final Boolean DEFAULT_LIBRARY_PASS = false;
    private static final Boolean UPDATED_LIBRARY_PASS = true;

    private static final Boolean DEFAULT_LIBRARY_SUBSCRIPTION = false;
    private static final Boolean UPDATED_LIBRARY_SUBSCRIPTION = true;

    private static final Boolean DEFAULT_SUBSCRIPTION = false;
    private static final Boolean UPDATED_SUBSCRIPTION = true;

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private BookRepository bookRepository;

    @Mock
    private BookRepository bookRepositoryMock;

    @Mock
    private BookService bookServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookMockMvc;

    private Book book;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity(EntityManager em) {
        Book book = new Book()
            .title(DEFAULT_TITLE)
            .url(DEFAULT_URL)
            .imgsrc(DEFAULT_IMGSRC)
            .ebook(DEFAULT_EBOOK)
            .audiobook(DEFAULT_AUDIOBOOK)
            .category(DEFAULT_CATEGORY)
            .added(DEFAULT_ADDED)
            .kindleSubscription(DEFAULT_KINDLE_SUBSCRIPTION)
            .libraryPass(DEFAULT_LIBRARY_PASS)
            .librarySubscription(DEFAULT_LIBRARY_SUBSCRIPTION)
            .subscription(DEFAULT_SUBSCRIPTION);
        return book;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity(EntityManager em) {
        Book book = new Book()
            .title(UPDATED_TITLE)
            .url(UPDATED_URL)
            .imgsrc(UPDATED_IMGSRC)
            .ebook(UPDATED_EBOOK)
            .audiobook(UPDATED_AUDIOBOOK)
            .category(UPDATED_CATEGORY)
            .added(UPDATED_ADDED)
            .kindleSubscription(UPDATED_KINDLE_SUBSCRIPTION)
            .libraryPass(UPDATED_LIBRARY_PASS)
            .librarySubscription(UPDATED_LIBRARY_SUBSCRIPTION)
            .subscription(UPDATED_SUBSCRIPTION);
        return book;
    }

    @BeforeEach
    public void initTest() {
        book = createEntity(em);
    }

    @Test
    @Transactional
    void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();
        // Create the Book
        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isCreated());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testBook.getImgsrc()).isEqualTo(DEFAULT_IMGSRC);
        assertThat(testBook.getEbook()).isEqualTo(DEFAULT_EBOOK);
        assertThat(testBook.getAudiobook()).isEqualTo(DEFAULT_AUDIOBOOK);
        assertThat(testBook.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testBook.getAdded()).isEqualTo(DEFAULT_ADDED);
        assertThat(testBook.getKindleSubscription()).isEqualTo(DEFAULT_KINDLE_SUBSCRIPTION);
        assertThat(testBook.getLibraryPass()).isEqualTo(DEFAULT_LIBRARY_PASS);
        assertThat(testBook.getLibrarySubscription()).isEqualTo(DEFAULT_LIBRARY_SUBSCRIPTION);
        assertThat(testBook.getSubscription()).isEqualTo(DEFAULT_SUBSCRIPTION);
    }

    @Test
    @Transactional
    void createBookWithExistingId() throws Exception {
        // Create the Book with an existing ID
        book.setId("existing_id");

        int databaseSizeBeforeCreate = bookRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBooks() throws Exception {
        // Initialize the database
        book.setId(UUID.randomUUID().toString());
        bookRepository.saveAndFlush(book);

        // Get all the bookList
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].imgsrc").value(hasItem(DEFAULT_IMGSRC)))
            .andExpect(jsonPath("$.[*].ebook").value(hasItem(DEFAULT_EBOOK.booleanValue())))
            .andExpect(jsonPath("$.[*].audiobook").value(hasItem(DEFAULT_AUDIOBOOK.booleanValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].added").value(hasItem(DEFAULT_ADDED.toString())))
            .andExpect(jsonPath("$.[*].kindleSubscription").value(hasItem(DEFAULT_KINDLE_SUBSCRIPTION.booleanValue())))
            .andExpect(jsonPath("$.[*].libraryPass").value(hasItem(DEFAULT_LIBRARY_PASS.booleanValue())))
            .andExpect(jsonPath("$.[*].librarySubscription").value(hasItem(DEFAULT_LIBRARY_SUBSCRIPTION.booleanValue())))
            .andExpect(jsonPath("$.[*].subscription").value(hasItem(DEFAULT_SUBSCRIPTION.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBook() throws Exception {
        // Initialize the database
        book.setId(UUID.randomUUID().toString());
        bookRepository.saveAndFlush(book);

        // Get the book
        restBookMockMvc
            .perform(get(ENTITY_API_URL_ID, book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(book.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.imgsrc").value(DEFAULT_IMGSRC))
            .andExpect(jsonPath("$.ebook").value(DEFAULT_EBOOK.booleanValue()))
            .andExpect(jsonPath("$.audiobook").value(DEFAULT_AUDIOBOOK.booleanValue()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY))
            .andExpect(jsonPath("$.added").value(DEFAULT_ADDED.toString()))
            .andExpect(jsonPath("$.kindleSubscription").value(DEFAULT_KINDLE_SUBSCRIPTION.booleanValue()))
            .andExpect(jsonPath("$.libraryPass").value(DEFAULT_LIBRARY_PASS.booleanValue()))
            .andExpect(jsonPath("$.librarySubscription").value(DEFAULT_LIBRARY_SUBSCRIPTION.booleanValue()))
            .andExpect(jsonPath("$.subscription").value(DEFAULT_SUBSCRIPTION.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBook() throws Exception {
        // Initialize the database
        book.setId(UUID.randomUUID().toString());
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).get();
        // Disconnect from session so that the updates on updatedBook are not directly saved in db
        em.detach(updatedBook);
        updatedBook
            .title(UPDATED_TITLE)
            .url(UPDATED_URL)
            .imgsrc(UPDATED_IMGSRC)
            .ebook(UPDATED_EBOOK)
            .audiobook(UPDATED_AUDIOBOOK)
            .category(UPDATED_CATEGORY)
            .added(UPDATED_ADDED)
            .kindleSubscription(UPDATED_KINDLE_SUBSCRIPTION)
            .libraryPass(UPDATED_LIBRARY_PASS)
            .librarySubscription(UPDATED_LIBRARY_SUBSCRIPTION)
            .subscription(UPDATED_SUBSCRIPTION);

        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testBook.getImgsrc()).isEqualTo(UPDATED_IMGSRC);
        assertThat(testBook.getEbook()).isEqualTo(UPDATED_EBOOK);
        assertThat(testBook.getAudiobook()).isEqualTo(UPDATED_AUDIOBOOK);
        assertThat(testBook.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testBook.getAdded()).isEqualTo(UPDATED_ADDED);
        assertThat(testBook.getKindleSubscription()).isEqualTo(UPDATED_KINDLE_SUBSCRIPTION);
        assertThat(testBook.getLibraryPass()).isEqualTo(UPDATED_LIBRARY_PASS);
        assertThat(testBook.getLibrarySubscription()).isEqualTo(UPDATED_LIBRARY_SUBSCRIPTION);
        assertThat(testBook.getSubscription()).isEqualTo(UPDATED_SUBSCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, book.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookWithPatch() throws Exception {
        // Initialize the database
        book.setId(UUID.randomUUID().toString());
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook.audiobook(UPDATED_AUDIOBOOK).kindleSubscription(UPDATED_KINDLE_SUBSCRIPTION).subscription(UPDATED_SUBSCRIPTION);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testBook.getImgsrc()).isEqualTo(DEFAULT_IMGSRC);
        assertThat(testBook.getEbook()).isEqualTo(DEFAULT_EBOOK);
        assertThat(testBook.getAudiobook()).isEqualTo(UPDATED_AUDIOBOOK);
        assertThat(testBook.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testBook.getAdded()).isEqualTo(DEFAULT_ADDED);
        assertThat(testBook.getKindleSubscription()).isEqualTo(UPDATED_KINDLE_SUBSCRIPTION);
        assertThat(testBook.getLibraryPass()).isEqualTo(DEFAULT_LIBRARY_PASS);
        assertThat(testBook.getLibrarySubscription()).isEqualTo(DEFAULT_LIBRARY_SUBSCRIPTION);
        assertThat(testBook.getSubscription()).isEqualTo(UPDATED_SUBSCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateBookWithPatch() throws Exception {
        // Initialize the database
        book.setId(UUID.randomUUID().toString());
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook
            .title(UPDATED_TITLE)
            .url(UPDATED_URL)
            .imgsrc(UPDATED_IMGSRC)
            .ebook(UPDATED_EBOOK)
            .audiobook(UPDATED_AUDIOBOOK)
            .category(UPDATED_CATEGORY)
            .added(UPDATED_ADDED)
            .kindleSubscription(UPDATED_KINDLE_SUBSCRIPTION)
            .libraryPass(UPDATED_LIBRARY_PASS)
            .librarySubscription(UPDATED_LIBRARY_SUBSCRIPTION)
            .subscription(UPDATED_SUBSCRIPTION);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testBook.getImgsrc()).isEqualTo(UPDATED_IMGSRC);
        assertThat(testBook.getEbook()).isEqualTo(UPDATED_EBOOK);
        assertThat(testBook.getAudiobook()).isEqualTo(UPDATED_AUDIOBOOK);
        assertThat(testBook.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testBook.getAdded()).isEqualTo(UPDATED_ADDED);
        assertThat(testBook.getKindleSubscription()).isEqualTo(UPDATED_KINDLE_SUBSCRIPTION);
        assertThat(testBook.getLibraryPass()).isEqualTo(UPDATED_LIBRARY_PASS);
        assertThat(testBook.getLibrarySubscription()).isEqualTo(UPDATED_LIBRARY_SUBSCRIPTION);
        assertThat(testBook.getSubscription()).isEqualTo(UPDATED_SUBSCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, book.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBook() throws Exception {
        // Initialize the database
        book.setId(UUID.randomUUID().toString());
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeDelete = bookRepository.findAll().size();

        // Delete the book
        restBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, book.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
