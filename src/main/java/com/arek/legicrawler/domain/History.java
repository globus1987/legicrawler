package com.arek.legicrawler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

/**
 * A History.
 */
@JsonIgnoreProperties(value = { "new" })
@Entity
@Table(name = "history")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class History implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "time_stamp")
    @CreationTimestamp
    private ZonedDateTime timeStamp;

    @Transient
    private boolean isPersisted;

    @OneToMany(mappedBy = "history", cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties(value = { "history" }, allowSetters = true)
    private Set<HistoryData> data = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public History id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ZonedDateTime getTimeStamp() {
        return this.timeStamp;
    }

    public History timeStamp(ZonedDateTime timeStamp) {
        this.setTimeStamp(timeStamp);
        return this;
    }

    public void setTimeStamp(ZonedDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public History setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    public Set<HistoryData> getData() {
        return this.data;
    }

    public void setData(Set<HistoryData> historyData) {
        if (this.data != null) {
            this.data.forEach(i -> i.setHistory(null));
        }
        if (historyData != null) {
            historyData.forEach(i -> i.setHistory(this));
        }
        this.data = historyData;
    }

    public History data(Set<HistoryData> historyData) {
        this.setData(historyData);
        return this;
    }

    public History addData(HistoryData historyData) {
        this.data.add(historyData);
        historyData.setHistory(this);
        return this;
    }

    public History removeData(HistoryData historyData) {
        this.data.remove(historyData);
        historyData.setHistory(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof History)) {
            return false;
        }
        return id != null && id.equals(((History) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "History{" +
            "id=" + getId() +
            ", timeStamp='" + getTimeStamp() + "'" +
            "}";
    }
}
