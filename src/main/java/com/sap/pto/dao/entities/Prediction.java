package com.sap.pto.dao.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

import com.sap.pto.dao.entities.Fixture.Result;

@Table(name = "Predictions")
@NamedQueries({
        @NamedQuery(name = Prediction.QUERY_BYUSER, query = "SELECT p FROM Prediction p WHERE p.user = :user order by p.fixture.matchDate asc"),
        @NamedQuery(name = Prediction.QUERY_BYFIXTURE, query = "SELECT p FROM Prediction p WHERE p.fixture = :fixture"),
        @NamedQuery(name = Prediction.QUERY_BYUSERANDFIXTURE, query = "SELECT p FROM Prediction p WHERE p.user.id = :userId and p.fixture.id = :fixtureId"),
        @NamedQuery(name = Prediction.QUERY_BYUSERSANDFIXTURES, query = "SELECT p FROM Prediction p WHERE p.user.id in :users and p.fixture.id in :fixtures order by p.fixture.matchDate asc"),
        @NamedQuery(name = Prediction.QUERY_PASTBYFIXTURE, query = "SELECT p FROM Prediction p WHERE p.fixture = :fixture and p.fixture.result <> com.sap.pto.dao.entities.Fixture.Result.NONE"),
        @NamedQuery(name = Prediction.QUERY_PASTBYUSER, query = "SELECT p FROM Prediction p WHERE p.user = :user and p.fixture.result <> com.sap.pto.dao.entities.Fixture.Result.NONE order by p.fixture.matchDate desc") })
@Entity
public class Prediction extends BasicEntity {
    public static final String QUERY_BYUSER = "findPredictionsByUser";
    public static final String QUERY_BYFIXTURE = "findPredictionsByFixture";
    public static final String QUERY_PASTBYUSER = "findPastPredictionsByUser";
    public static final String QUERY_PASTBYFIXTURE = "findPastPredictionsByFixture";
    public static final String QUERY_BYUSERANDFIXTURE = "findPredictionByUserAndFixture";
    public static final String QUERY_BYUSERSANDFIXTURES = "findPredictionsByUsersAndFixtures";
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Index
    private User user;
    @Index
    private Fixture fixture;
    @Index
    private Result result;

    public Prediction() {
        // just needed for JPA
    }

    public Prediction(User user, Fixture fixture, Result result) {
        this.user = user;
        this.fixture = fixture;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Prediction [result=" + result + ", fixture=" + fixture + ", user=" + user + "]";
    }

}
