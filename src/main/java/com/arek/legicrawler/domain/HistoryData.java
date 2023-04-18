package com.arek.legicrawler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.springframework.data.domain.Persistable;

/**
 * A HistoryData.
 */
@JsonIgnoreProperties(value = { "new" })
@Entity
@Table(name = "history_data")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoryData implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "key")
    private String key;

    @Column(name = "value_string")
    private String valueString;

    @Column(name = "value_int")
    private Integer valueInt;

    @Transient
    private boolean isPersisted;

    @ManyToOne
    @JsonIgnoreProperties(value = { "data" }, allowSetters = true)
    private History history;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public HistoryData id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public HistoryData key(String key) {
        this.setKey(key);
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValueString() {
        return this.valueString;
    }

    public HistoryData valueString(String valueString) {
        this.setValueString(valueString);
        return this;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public Integer getValueInt() {
        return this.valueInt;
    }

    public HistoryData valueInt(Integer valueInt) {
        this.setValueInt(valueInt);
        return this;
    }

    public void setValueInt(Integer valueInt) {
        this.valueInt = valueInt;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public HistoryData setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    public History getHistory() {
        return this.history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public HistoryData history(History history) {
        this.setHistory(history);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistoryData)) {
            return false;
        }
        return id != null && id.equals(((HistoryData) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoryData{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", valueString='" + getValueString() + "'" +
            ", valueInt=" + getValueInt() +
            "}";
    }
}
