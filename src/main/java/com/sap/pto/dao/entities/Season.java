package com.sap.pto.dao.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

@Table(name = "Seasons")
@Entity
public class Season extends BasicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Index
    private String extId;
    private String name;

    public Season() {
        // just needed for JPA
    }

    public Season(String extId) {
        this.extId = extId;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Season [extId=" + extId + ", name=" + name + "]";
    }

}
