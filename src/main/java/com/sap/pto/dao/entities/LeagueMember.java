package com.sap.pto.dao.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "LeagueMembers")
@Entity
public class LeagueMember extends BasicEntity {
    public enum MemberState {
        INVITED, APPLIED, ACCEPTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    private User user;
    private MemberState state;
    @Transient
    private Prediction prediction;
    @Transient
    private UserStatistics statistics;

    public LeagueMember() {
        // just needed for JPA
    }

    public LeagueMember(User user) {
        this.user = user;
        this.state = MemberState.ACCEPTED;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MemberState getState() {
        return state;
    }

    public void setState(MemberState state) {
        this.state = state;
    }

    public Prediction getPrediction() {
        return prediction;
    }

    public void setPrediction(Prediction prediction) {
        this.prediction = prediction;
    }

    public UserStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(UserStatistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String toString() {
        return "LeagueMember [user=" + user + "]";
    }

}
