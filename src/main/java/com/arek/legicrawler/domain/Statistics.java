package com.arek.legicrawler.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * A Statistics.
 */
@Entity
@Table(name = "statistics")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Statistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "added")
    private LocalDate added;

    @Column(name = "count")
    private Integer count;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Statistics id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getAdded() {
        return this.added;
    }

    public Statistics added(LocalDate added) {
        this.setAdded(added);
        return this;
    }

    public void setAdded(LocalDate added) {
        this.added = added;
    }

    public Integer getCount() {
        return this.count;
    }

    public Statistics count(Integer count) {
        this.setCount(count);
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Statistics)) {
            return false;
        }
        return id != null && id.equals(((Statistics) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Statistics{" +
            "id=" + getId() +
            ", added='" + getAdded() + "'" +
            ", count=" + getCount() +
            "}";
    }
}
