package com.sap.pto.dao.entities;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import com.sap.pto.services.util.JsonIgnore;

/**
 * Commonly needed attributes of all entities.
 * 
 */
@MappedSuperclass
public abstract class BasicEntity {
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date dateCreated;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date dateModified;

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDateCreated(DateTime dateCreated) {
        this.dateCreated = dateCreated.toDate();
    }

    public Date getDateModified() {
        return dateModified;
    }

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        if (dateCreated == null) {
            dateCreated = new Date();
        }
        dateModified = new Date();
    }

}
