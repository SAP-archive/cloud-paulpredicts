package com.sap.pto.dao.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

@Table(name = "Teams")
@Entity
public class Team extends BasicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Index
    private String extId;
    private Competition competition;
    private Season season;
    private String name;
    private String longName;
    private String shortName;
    private String country;
    private String region;
    private String previousGameStats;
    private String foundingDate;
    private String stadiumName;
    private int stadiumCapacity;

    public Team() {
        // just needed for JPA
    }

    public Team(String extId) {
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

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFoundingDate() {
        return foundingDate;
    }

    public void setFoundingDate(String foundingDate) {
        this.foundingDate = foundingDate;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPreviousGameStats() {
        return previousGameStats;
    }

    public void setPreviousGameStats(String previousGameStats) {
        this.previousGameStats = previousGameStats;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public int getStadiumCapacity() {
        return stadiumCapacity;
    }

    public void setStadiumCapacity(int stadiumCapacity) {
        this.stadiumCapacity = stadiumCapacity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((competition == null) ? 0 : competition.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((extId == null) ? 0 : extId.hashCode());
        result = prime * result + ((foundingDate == null) ? 0 : foundingDate.hashCode());
        result = prime * result + ((longName == null) ? 0 : longName.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((previousGameStats == null) ? 0 : previousGameStats.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime * result + ((season == null) ? 0 : season.hashCode());
        result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
        result = prime * result + stadiumCapacity;
        result = prime * result + ((stadiumName == null) ? 0 : stadiumName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Team other = (Team) obj;
        if (competition == null) {
            if (other.competition != null)
                return false;
        } else if (!competition.equals(other.competition))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (extId == null) {
            if (other.extId != null)
                return false;
        } else if (!extId.equals(other.extId))
            return false;
        if (foundingDate == null) {
            if (other.foundingDate != null)
                return false;
        } else if (!foundingDate.equals(other.foundingDate))
            return false;
        if (longName == null) {
            if (other.longName != null)
                return false;
        } else if (!longName.equals(other.longName))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (previousGameStats == null) {
            if (other.previousGameStats != null)
                return false;
        } else if (!previousGameStats.equals(other.previousGameStats))
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        } else if (!region.equals(other.region))
            return false;
        if (season == null) {
            if (other.season != null)
                return false;
        } else if (!season.equals(other.season))
            return false;
        if (shortName == null) {
            if (other.shortName != null)
                return false;
        } else if (!shortName.equals(other.shortName))
            return false;
        if (stadiumCapacity != other.stadiumCapacity)
            return false;
        if (stadiumName == null) {
            if (other.stadiumName != null)
                return false;
        } else if (!stadiumName.equals(other.stadiumName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Team [name=" + name + "]";
    }

}
