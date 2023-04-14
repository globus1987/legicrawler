package com.arek.legicrawler.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * A Statistics.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Statistics {

    private static final long serialVersionUID = 1L;
    private LocalDate added;
    private Integer count;

    public LocalDate getAdded() {
        return this.added;
    }

    public Statistics(LocalDate added, Long count) {
        this.added = added;
        this.count = count.intValue();
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

}
