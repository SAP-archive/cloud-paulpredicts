package com.sap.pto.importers;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.pto.dao.PlayerDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.TeamOfficialDAO;
import com.sap.pto.dao.entities.Competition;
import com.sap.pto.dao.entities.Player;
import com.sap.pto.dao.entities.Season;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.TeamOfficial;
import com.sap.pto.util.XmlUtilsExt;

public class OptaTeamImporter extends BasicImporter {
    @Override
    public void importData(Document doc) {
        Competition competition = extractCompetition(doc);
        Season season = extractSeason(doc);

        NodeList teamList = XmlUtilsExt.getXPathResultSet(doc, "//SoccerDocument/Team");
        for (int i = 0; i < teamList.getLength(); i++) {
            Node node = teamList.item(i);

            Team team = extractTeam(node, competition, season);
            extractPlayers(node, team);
            extractTeamOfficials(node, team);
        }
    }

    private Team extractTeam(Node node, Competition competition, Season season) {
        String extId = stripId(XmlUtilsExt.getXPathResultValue(node, "@uID"));
        String name = XmlUtilsExt.getXPathResultValue(node, "Name");
        String shortName = XmlUtilsExt.getXPathResultValue(node, "SYMID");
        String country = XmlUtilsExt.getXPathResultValue(node, "Country");
        String region = XmlUtilsExt.getXPathResultValue(node, "Region");
        String foundingDate = XmlUtilsExt.getXPathResultValue(node, "Founded");
        String stadiumName = XmlUtilsExt.getXPathResultValue(node, "Stadium/Name");
        String stadiumCapacity = XmlUtilsExt.getXPathResultValue(node, "Stadium/Capacity");

        Team team = new TeamDAO().getByExtId(extId);
        if (team == null) {
            // create new
            team = new Team(extId);
        }

        team.setName(name);
        team.setShortName(shortName);
        team.setLongName(name);
        team.setCountry(country);
        team.setRegion(region);
        team.setFoundingDate(foundingDate);
        team.setStadiumName(stadiumName);
        team.setCompetition(competition);
        team.setSeason(season);
        if (StringUtils.isNumeric(stadiumCapacity)) {
            team.setStadiumCapacity(Integer.valueOf(stadiumCapacity));
        }
        team = TeamDAO.save(team);

        return team;
    }

    private void extractPlayers(Node node, Team team) {
        NodeList playerList = XmlUtilsExt.getXPathResultSet(node, ".//Player");
        for (int i = 0; i < playerList.getLength(); i++) {
            Node playerNode = playerList.item(i);
            extractPlayer(playerNode, team);
        }
    }

    private Player extractPlayer(Node node, Team team) {
        String extId = stripId(getAttribute(node.getAttributes(), "uID"));
        String firstName = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"first_name\"]");
        String lastName = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"last_name\"]");
        String nickName = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"known_name\"]");
        String birthday = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"birth_date\"]");
        String weight = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"weight\"]");
        String height = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"height\"]");
        String jerseyNum = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"jersey_num\"]");
        String position = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"real_position\"]");
        String positionSide = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"real_position_side\"]");
        String dateJoined = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"join_date\"]");
        String country = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"country\"]");

        Player player = new PlayerDAO().getByExtId(extId);
        if (player == null) {
            // create new
            player = new Player(extId);
        }

        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setNickName(nickName);
        player.setBirthday(birthday);
        if (StringUtils.isNumeric(weight)) {
            player.setWeight(Integer.valueOf(weight));
        }
        if (StringUtils.isNumeric(height)) {
            player.setHeight(Integer.valueOf(height));
        }
        if (StringUtils.isNumeric(jerseyNum)) {
            player.setJerseyNumber(Integer.valueOf(jerseyNum));
        }
        player.setPosition(position);
        player.setPositionSide(positionSide);
        player.setDateJoined(dateJoined);
        player.setCountry(country);
        player.setTeam(team);

        player = PlayerDAO.save(player);

        return player;
    }

    private void extractTeamOfficials(Node node, Team team) {
        NodeList playerList = XmlUtilsExt.getXPathResultSet(node, ".//TeamOfficial");
        for (int i = 0; i < playerList.getLength(); i++) {
            Node officialNode = playerList.item(i);
            extractTeamOfficial(officialNode, team);
        }

    }

    private TeamOfficial extractTeamOfficial(Node node, Team team) {
        String extId = stripId(XmlUtilsExt.getXPathResultValue(node, "@uID"));
        String position = XmlUtilsExt.getXPathResultValue(node, "@Type");
        String country = XmlUtilsExt.getXPathResultValue(node, "@Country");
        String firstName = XmlUtilsExt.getXPathResultValue(node, "PersonName/First");
        String lastName = XmlUtilsExt.getXPathResultValue(node, "PersonName/Last");
        String dateJoined = XmlUtilsExt.getXPathResultValue(node, "PersonName/join_date");

        TeamOfficial official = new TeamOfficialDAO().getByExtId(extId);
        if (official == null) {
            // create new
            official = new TeamOfficial(extId);
        }

        official.setFirstName(firstName);
        official.setLastName(lastName);
        official.setPosition(position);
        official.setDateJoined(dateJoined);
        official.setCountry(country);
        official.setTeam(team);

        official = TeamOfficialDAO.save(official);

        return official;
    }

}
