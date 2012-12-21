package com.sap.pto.dao.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

import com.sap.pto.services.util.JsonIgnore;

@Table(name = "Editorials")
@NamedQueries({ @NamedQuery(name = Editorial.DELETE_BYFIXTURE, query = "DELETE FROM Editorial e WHERE e.fixture = :fixture") })
@Entity
public class Editorial extends BasicEntity {
    public static final String DELETE_BYFIXTURE = "deleteEditorialsByFixture";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    private String extId;
    @Index
    @JsonIgnore
    private Fixture fixture;
    @Column(length = 4000)
    private String text;
    private String language;

    public Editorial() {
        // just needed for JPA
    }

    public Editorial(String extId) {
        this.extId = extId;
    }

    public long getId() {
        return id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "Editorial [fixture=" + fixture + ", text=" + text + "]";
    }

}
