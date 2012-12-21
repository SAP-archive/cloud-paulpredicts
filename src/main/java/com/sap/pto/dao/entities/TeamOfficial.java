package com.sap.pto.dao.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

@Table(name = "TeamOfficials")
@Entity
public class TeamOfficial extends BasicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Index
    private String extId;
    @Index
    private Team team;
    private String firstName;
    private String lastName;
    private String country;
    private String position;
    private String dateJoined;

    public TeamOfficial() {
        // just needed for JPA
    }

    public TeamOfficial(String extId) {
        this.extId = extId;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TeamOfficial [position=" + position + ", team=" + team + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

}
