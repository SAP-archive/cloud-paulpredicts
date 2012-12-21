package com.sap.pto.dao.entities;

import java.util.List;

public class TeamStatistics {
    private Team team;
    private List<TeamStat> teamStatistics;
    private List<Player> players;
    private List<TeamOfficial> officials;

    public TeamStatistics() {
    }

    public TeamStatistics(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<TeamStat> getTeamStatistics() {
        return teamStatistics;
    }

    public void setTeamStatistics(List<TeamStat> teamStatistics) {
        this.teamStatistics = teamStatistics;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<TeamOfficial> getOfficials() {
        return officials;
    }

    public void setOfficials(List<TeamOfficial> officials) {
        this.officials = officials;
    }

}
