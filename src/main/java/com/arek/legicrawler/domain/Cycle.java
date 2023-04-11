package com.arek.legicrawler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.springframework.data.domain.Persistable;

/**
 * A Cycle.
 */
@JsonIgnoreProperties(value = { "new" })
@Entity
@Table(name = "cycle")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cycle implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Size(max = 5000)
    @Column(name = "url", length = 5000)
    private String url;

    @Transient
    private boolean isPersisted;

    @OneToMany(mappedBy = "cycle", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "collections", "authors", "cycle" }, allowSetters = true)
    private Set<Book> books = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Cycle id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Cycle name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public Cycle url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Cycle setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    public Set<Book> getBooks() {
        return this.books;
    }

    public void setBooks(Set<Book> books) {
        if (this.books != null) {
            this.books.forEach(i -> i.setCycle(null));
        }
        if (books != null) {
            books.forEach(i -> i.setCycle(this));
        }
        this.books = books;
    }

    public Cycle books(Set<Book> books) {
        this.setBooks(books);
        return this;
    }

    public Cycle addBooks(Book book) {
        if (this.books.stream().map(Book::getId).collect(Collectors.toList()).contains(book.getId())) return this;
        this.books.add(book);
        book.setCycle(this);
        return this;
    }

    public Cycle removeBooks(Book book) {
        this.books.remove(book);
        book.setCycle(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cycle)) {
            return false;
        }
        return id != null && id.equals(((Cycle) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cycle{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", url='" + getUrl() + "'" +
            "}";
    }
}
