package com.sap.pto.dao.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Index;

@Table(name = "Fixtures")
@NamedQueries({
        @NamedQuery(name = Fixture.QUERY_BYTEAM, query = "SELECT f FROM Fixture f WHERE f.awayTeam = :team or f.homeTeam = :team order by f.matchDate desc"),
        @NamedQuery(name = Fixture.QUERY_PAST, query = "SELECT f FROM Fixture f WHERE f.matchDate < :date order by f.matchDate desc"),
        @NamedQuery(name = Fixture.QUERY_FUTURE, query = "SELECT f FROM Fixture f WHERE f.matchDate >= :date order by f.matchDate asc"),
        @NamedQuery(name = Fixture.QUERY_BYDATEANDTEAMS, query = "SELECT f FROM Fixture f WHERE f.extId is null and f.homeTeam = :homeTeam and f.awayTeam = :awayTeam and f.matchDate >= :minDate and f.matchDate <= :maxDate order by f.matchDate") })
@Entity
public class Fixture extends BasicEntity {
    public static final String QUERY_BYTEAM = "findFixturesByTeam";
    public static final String QUERY_PAST = "findPastFixtures";
    public static final String QUERY_FUTURE = "findFutureFixtures";
    public static final String QUERY_BYDATEANDTEAMS = "findFixtureByDateAndTeams";

    public enum Result {
        NONE, HOMEWIN, AWAYWIN, DRAW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Index
    private String extId;
    @Index
    private Competition competition;
    @Index
    private Season season;
    @Temporal(TemporalType.TIMESTAMP)
    @Index
    private Date matchDate;
    @Index
    private Team awayTeam;
    @Index
    private Team homeTeam;
    @Index
    private Result result = Result.NONE;
    private String score;
    private String groupName;
    private String venue;
    private String city;
    @Transient
    private List<Prediction> predictions = new ArrayList<Prediction>();
    @Transient
    private Prediction userPrediction;
    @Transient
    private List<Editorial> editorials = new ArrayList<Editorial>();
    @Transient
    private List<Goal> goals = new ArrayList<Goal>();

    public Fixture() {
        // just needed for JPA
    }

    public Fixture(String extId) {
        this.extId = extId;
    }

    public Fixture(Date matchDate, Team homeTeam, Team awayTeam) {
        this.matchDate = matchDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
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

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    public Prediction getUserPrediction() {
        return userPrediction;
    }

    public void setUserPrediction(Prediction userPrediction) {
        this.userPrediction = userPrediction;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Editorial> getEditorials() {
        return editorials;
    }

    public void setEditorials(List<Editorial> editorials) {
        this.editorials = editorials;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((awayTeam == null) ? 0 : awayTeam.hashCode());
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((competition == null) ? 0 : competition.hashCode());
        result = prime * result + ((extId == null) ? 0 : extId.hashCode());
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + ((homeTeam == null) ? 0 : homeTeam.hashCode());
        result = prime * result + ((matchDate == null) ? 0 : matchDate.hashCode());
        result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
        result = prime * result + ((score == null) ? 0 : score.hashCode());
        result = prime * result + ((venue == null) ? 0 : venue.hashCode());
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
        Fixture other = (Fixture) obj;
        if (awayTeam == null) {
            if (other.awayTeam != null)
                return false;
        } else if (!awayTeam.equals(other.awayTeam))
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (competition == null) {
            if (other.competition != null)
                return false;
        } else if (!competition.equals(other.competition))
            return false;
        if (extId == null) {
            if (other.extId != null)
                return false;
        } else if (!extId.equals(other.extId))
            return false;
        if (groupName == null) {
            if (other.groupName != null)
                return false;
        } else if (!groupName.equals(other.groupName))
            return false;
        if (homeTeam == null) {
            if (other.homeTeam != null)
                return false;
        } else if (!homeTeam.equals(other.homeTeam))
            return false;
        if (matchDate == null) {
            if (other.matchDate != null)
                return false;
        } else if (!matchDate.equals(other.matchDate))
            return false;
        if (result != other.result)
            return false;
        if (score == null) {
            if (other.score != null)
                return false;
        } else if (!score.equals(other.score))
            return false;
        if (venue == null) {
            if (other.venue != null)
                return false;
        } else if (!venue.equals(other.venue))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Fixture [matchDate=" + matchDate + ", awayTeam=" + awayTeam + ", homeTeam=" + homeTeam + ", result=" + result + ", score="
                + score + "]";
    }

}
