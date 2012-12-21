package com.sap.pto.importers;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.pto.dao.PlayerDAO;
import com.sap.pto.dao.PlayerStatDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.TeamStatDAO;
import com.sap.pto.dao.entities.Competition;
import com.sap.pto.dao.entities.Player;
import com.sap.pto.dao.entities.PlayerStat;
import com.sap.pto.dao.entities.Season;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.TeamStat;
import com.sap.pto.util.XmlUtilsExt;

public class OptaStatsImporter extends BasicImporter {
    @Override
    public void importData(Document doc) {
        Competition competition = extractCompetition(doc, "SeasonStatistics");
        Season season = extractSeason(doc, "SeasonStatistics");

        NodeList teamList = XmlUtilsExt.getXPathResultSet(doc, "//SeasonStatistics/Team");
        for (int i = 0; i < teamList.getLength(); i++) {
            Node node = teamList.item(i);

            Team team = extractTeam(node, competition, season);
            extractTeamStats(node, team);
            extractPlayerStats(node, team);
        }
    }

    private void extractTeamStats(Node node, Team team) {
        NodeList statsList = XmlUtilsExt.getXPathResultSet(node, "./Stat");
        for (int i = 0; i < statsList.getLength(); i++) {
            Node reportNode = statsList.item(i);
            extractTeamStat(reportNode, team);
        }
    }

    private TeamStat extractTeamStat(Node node, Team team) {
        String key = getAttribute(node.getAttributes(), "name");
        String value = node.getTextContent();

        TeamStat teamStat = TeamStatDAO.getForTeam(team, key);
        if (teamStat == null) {
            // create new
            teamStat = new TeamStat();
        }

        teamStat.setTeam(team);
        teamStat.setStatKey(key);
        teamStat.setStatValue(value);
        teamStat = TeamStatDAO.save(teamStat);

        return teamStat;
    }

    private void extractPlayerStats(Node node, Team team) {
        NodeList statsList = XmlUtilsExt.getXPathResultSet(node, "./Player/Stat");
        for (int i = 0; i < statsList.getLength(); i++) {
            Node reportNode = statsList.item(i);
            extractPlayerStat(reportNode, team);
        }
    }

    private PlayerStat extractPlayerStat(Node node, Team team) {
        String extId = stripId(getAttribute(node.getParentNode().getAttributes(), "player_id"));
        String position = getAttribute(node.getParentNode().getAttributes(), "position");
        String jerseyNumber = getAttribute(node.getParentNode().getAttributes(), "shirtNumber");
        String firstName = getAttribute(node.getParentNode().getAttributes(), "first_name");
        String lastName = getAttribute(node.getParentNode().getAttributes(), "last_name");
        String nickName = getAttribute(node.getParentNode().getAttributes(), "known_name");
        String key = getAttribute(node.getAttributes(), "name");
        String value = node.getTextContent();

        Player player = new PlayerDAO().getByExtId(extId);
        if (player == null) {
            player = new Player(extId);
            player.setTeam(team);
            player.setPosition(position);
            player.setFirstName(firstName);
            player.setLastName(lastName);
            player.setNickName(nickName);
            if (StringUtils.isNumeric(jerseyNumber)) {
                player.setJerseyNumber(Integer.valueOf(jerseyNumber));
            }
            PlayerDAO.saveNew(player);
        }

        PlayerStat playerStat = PlayerStatDAO.getForPlayer(player, key);
        if (playerStat == null) {
            // create new
            playerStat = new PlayerStat();
        }

        playerStat.setPlayer(player);
        playerStat.setStatKey(key);
        playerStat.setStatValue(value);
        playerStat = TeamStatDAO.save(playerStat);

        return playerStat;
    }

    private Team extractTeam(Node node, Competition competition, Season season) {
        String extId = stripId(XmlUtilsExt.getXPathResultValue(node, "@id"));
        String name = XmlUtilsExt.getXPathResultValue(node, "@name");

        Team team = new TeamDAO().getByExtId(extId);
        if (team == null) {
            // create new
            team = new Team(extId);
        }

        team.setCompetition(competition);
        team.setSeason(season);
        team.setName(name);
        team = TeamDAO.save(team);

        return team;
    }
}
