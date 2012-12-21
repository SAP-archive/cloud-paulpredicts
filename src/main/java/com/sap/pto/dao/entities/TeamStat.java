package com.sap.pto.dao.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

@Table(name = "TeamStatistics")
@NamedQueries({
        @NamedQuery(name = TeamStat.QUERY_BYTEAMANDKEY, query = "SELECT ts FROM TeamStat ts WHERE ts.team = :team and ts.statKey = :key"),
        @NamedQuery(name = TeamStat.QUERY_BYTEAM, query = "SELECT ts FROM TeamStat ts WHERE ts.team = :team") })
@Entity
public class TeamStat extends BasicEntity {
    public static final String QUERY_BYTEAMANDKEY = "findTeamStatByTeamAndKey";
    public static final String QUERY_BYTEAM = "findTeamStatByTeam";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Index
    private Team team;
    @Index
    private String statKey;
    private String statValue;

    public TeamStat() {
        // just needed for JPA
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getStatKey() {
        return statKey;
    }

    public void setStatKey(String statKey) {
        this.statKey = statKey;
    }

    public String getStatValue() {
        return statValue;
    }

    public void setStatValue(String statValue) {
        this.statValue = statValue;
    }

    public long getId() {
        return id;
    }

}
