package com.sap.pto.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sap.pto.dao.PlayerDAO;
import com.sap.pto.dao.PlayerStatDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.TeamOfficialDAO;
import com.sap.pto.dao.TeamStatDAO;
import com.sap.pto.dao.entities.Player;
import com.sap.pto.dao.entities.PlayerStat;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.TeamOfficial;
import com.sap.pto.dao.entities.TeamStat;
import com.sap.pto.dao.entities.TeamStatistics;

@Path("/teamservice")
public class TeamService extends BasicService {
    @GET
    @Path("/teams")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Team> getAllTeams() {
        List<Team> teams = new TeamDAO().getAll();

        return teams;
    }

    @GET
    @Path("/details/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public TeamStatistics getTeamDetails(@PathParam("id") int teamId) {
        Team team = new TeamDAO().getById(teamId);
        if (team == null) {
            throwNotFound();
        }

        TeamStatistics allStats = new TeamStatistics(team);

        // team
        List<TeamStat> teamStats = TeamStatDAO.getForTeam(team);
        for (TeamStat teamStat : teamStats) {
            teamStat.setTeam(null);
        }
        allStats.setTeamStatistics(teamStats);

        // players
        List<Player> players = new PlayerDAO().getByTeam(team);
        for (Player player : players) {
            List<PlayerStat> playerStats = PlayerStatDAO.getForPlayer(player);
            player.setTeam(null);
            player.setStatistics(playerStats);
        }
        allStats.setPlayers(players);

        // officials
        List<TeamOfficial> officials = new TeamOfficialDAO().getByTeam(team);
        for (TeamOfficial teamOfficial : officials) {
            teamOfficial.setTeam(null);
        }
        allStats.setOfficials(officials);

        return allStats;
    }
}
