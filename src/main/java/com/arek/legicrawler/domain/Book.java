package com.arek.legicrawler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.springframework.data.domain.Persistable;

/**
 * A Book.
 */
@JsonIgnoreProperties(value = { "new" })
@Entity
@Table(name = "book")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Book implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Size(max = 5000)
    @Column(name = "url", length = 5000)
    private String url;

    @Column(name = "ebook")
    private Boolean ebook;

    @Column(name = "audiobook")
    private Boolean audiobook;

    @Column(name = "category")
    private String category;

    @Column(name = "added")
    private LocalDate added;

    @Column(name = "kindle_subscription")
    private Boolean kindleSubscription;

    @Column(name = "library_pass")
    private Boolean libraryPass;

    @Column(name = "library_subscription")
    private Boolean librarySubscription;

    @Column(name = "subscription")
    private Boolean subscription;

    @Transient
    private boolean isPersisted;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Cycle cycle;

    @ManyToMany
    @JoinTable(
        name = "rel_book__collections",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "collections_id")
    )
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Set<Collection> collections = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "rel_book__authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "authors_id")
    )
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Set<Author> authors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Book id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public Book url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getEbook() {
        return this.ebook;
    }

    public Book ebook(Boolean ebook) {
        this.setEbook(ebook);
        return this;
    }

    public void setEbook(Boolean ebook) {
        this.ebook = ebook;
    }

    public Boolean getAudiobook() {
        return this.audiobook;
    }

    public Book audiobook(Boolean audiobook) {
        this.setAudiobook(audiobook);
        return this;
    }

    public void setAudiobook(Boolean audiobook) {
        this.audiobook = audiobook;
    }

    public String getCategory() {
        return this.category;
    }

    public Book category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getAdded() {
        return this.added;
    }

    public Book added(LocalDate added) {
        this.setAdded(added);
        return this;
    }

    public void setAdded(LocalDate added) {
        this.added = added;
    }

    public Boolean getKindleSubscription() {
        return this.kindleSubscription;
    }

    public Book kindleSubscription(Boolean kindleSubscription) {
        this.setKindleSubscription(kindleSubscription);
        return this;
    }

    public void setKindleSubscription(Boolean kindleSubscription) {
        this.kindleSubscription = kindleSubscription;
    }

    public Boolean getLibraryPass() {
        return this.libraryPass;
    }

    public Book libraryPass(Boolean libraryPass) {
        this.setLibraryPass(libraryPass);
        return this;
    }

    public void setLibraryPass(Boolean libraryPass) {
        this.libraryPass = libraryPass;
    }

    public Boolean getLibrarySubscription() {
        return this.librarySubscription;
    }

    public Book librarySubscription(Boolean librarySubscription) {
        this.setLibrarySubscription(librarySubscription);
        return this;
    }

    public void setLibrarySubscription(Boolean librarySubscription) {
        this.librarySubscription = librarySubscription;
    }

    public Boolean getSubscription() {
        return this.subscription;
    }

    public Book subscription(Boolean subscription) {
        this.setSubscription(subscription);
        return this;
    }

    public void setSubscription(Boolean subscription) {
        this.subscription = subscription;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Book setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    public Cycle getCycle() {
        return this.cycle;
    }

    public void setCycle(Cycle cycle) {
        this.cycle = cycle;
    }

    public Book cycle(Cycle cycle) {
        this.setCycle(cycle);
        return this;
    }

    public Set<Collection> getCollections() {
        return this.collections;
    }

    public void setCollections(Set<Collection> collections) {
        this.collections = collections;
    }

    public Book collections(Set<Collection> collections) {
        this.setCollections(collections);
        return this;
    }

    public Book addCollections(Collection collection) {
        this.collections.add(collection);
        collection.getBooks().add(this);
        return this;
    }

    public Book removeCollections(Collection collection) {
        this.collections.remove(collection);
        collection.getBooks().remove(this);
        return this;
    }

    public Set<Author> getAuthors() {
        return this.authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Book authors(Set<Author> authors) {
        this.setAuthors(authors);
        return this;
    }

    public Book addAuthors(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
        return this;
    }

    public Book removeAuthors(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", url='" + getUrl() + "'" +
            ", ebook='" + getEbook() + "'" +
            ", audiobook='" + getAudiobook() + "'" +
            ", category='" + getCategory() + "'" +
            ", added='" + getAdded() + "'" +
            ", kindleSubscription='" + getKindleSubscription() + "'" +
            ", libraryPass='" + getLibraryPass() + "'" +
            ", librarySubscription='" + getLibrarySubscription() + "'" +
            ", subscription='" + getSubscription() + "'" +
            "}";
    }
}
