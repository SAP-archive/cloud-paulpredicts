package com.sap.pto.importers;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.GoalDAO;
import com.sap.pto.dao.PlayerDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.entities.Competition;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Goal;
import com.sap.pto.dao.entities.Player;
import com.sap.pto.dao.entities.Season;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.util.XmlUtilsExt;

public class OptaMatchImporter extends BasicImporter {
    private static final Logger logger = LoggerFactory.getLogger(OptaMatchImporter.class);
    private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void importData(Document doc) {
        Competition competition = extractCompetition(doc);
        Season season = extractSeason(doc);

        NodeList matchList = XmlUtilsExt.getXPathResultSet(doc, "//SoccerDocument/MatchData");
        for (int i = 0; i < matchList.getLength(); i++) {
            Node node = matchList.item(i);

            Fixture fixture = extractFixture(node, competition, season);
            extractGoals(node, fixture);
        }
    }

    private Fixture extractFixture(Node node, Competition competition, Season season) {
        String extId = stripId(getAttribute(node.getAttributes(), "uID"));
        String groupName = "Group " + XmlUtilsExt.getXPathResultValue(node, "MatchInfo/@GroupName");
        String period = XmlUtilsExt.getXPathResultValue(node, "MatchInfo/@Period");
        String venue = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"Venue\"]");
        String city = XmlUtilsExt.getXPathResultValue(node, "Stat[@Type=\"City\"]");
        String homeTeamId = XmlUtilsExt.getXPathResultValue(node, "TeamData[@Side=\"Home\"]/@TeamRef");
        String awayTeamId = XmlUtilsExt.getXPathResultValue(node, "TeamData[@Side=\"Away\"]/@TeamRef");
        String homeScore = XmlUtilsExt.getXPathResultValue(node, "TeamData[@Side=\"Home\"]/@Score");
        String awayScore = XmlUtilsExt.getXPathResultValue(node, "TeamData[@Side=\"Away\"]/@Score");
        String date = XmlUtilsExt.getXPathResultValue(node, "MatchInfo/Date");
        String tzId = XmlUtilsExt.getXPathResultValue(node, "MatchInfo/TZ");

        Team homeTeam = new TeamDAO().getByExtId(stripId(homeTeamId));
        Team awayTeam = new TeamDAO().getByExtId(stripId(awayTeamId));

        // convert BST to Europe/London, see Java Bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4257424
        if ("BST".equals(tzId)) {
            tzId = "Europe/London";
        }
        TimeZone tz = TimeZone.getTimeZone(tzId);
        DateTime matchDate = fmt.parseDateTime(date).withZoneRetainFields(DateTimeZone.forTimeZone(tz));

        if (homeTeam == null) {
            homeTeam = extractTeam(node, homeTeamId, competition, season);
        }
        if (awayTeam == null) {
            awayTeam = extractTeam(node, awayTeamId, competition, season);
        }

        Fixture fixture = new FixtureDAO().getByExtId(extId);
        if (fixture == null) {
            fixture = FixtureDAO.getForDayAndTeams(matchDate, homeTeam, awayTeam);
        }
        if (fixture == null) {
            // create new
            fixture = new Fixture(matchDate.toDate(), homeTeam, awayTeam);

            logger.info("Creating new fixture: " + fixture);
        } else {
            // update
            fixture.setMatchDate(matchDate.toDate());
            fixture.setHomeTeam(homeTeam);
            fixture.setAwayTeam(awayTeam);
        }

        if ("fulltime".equalsIgnoreCase(period)) {
            if (homeScore.equals(awayScore)) {
                fixture.setResult(Result.DRAW);
            } else {
                fixture.setResult(Integer.valueOf(homeScore) > Integer.valueOf(awayScore) ? Result.HOMEWIN : Result.AWAYWIN);
            }
            fixture.setScore(homeScore + ":" + awayScore);
        }
        fixture.setVenue(venue);
        fixture.setCity(city);
        fixture.setExtId(extId);
        fixture.setGroupName(groupName);
        fixture.setCompetition(competition);
        fixture.setSeason(season);
        fixture = FixtureDAO.save(fixture);

        return fixture;
    }

    private Team extractTeam(Node node, String extId, Competition competition, Season season) {
        String name = XmlUtilsExt.getXPathResultValue(node, "//Team[@uID=\"" + extId + "\"]/Name");

        Team team = new Team(stripId(extId));
        team.setName(name);
        team.setCompetition(competition);
        team.setSeason(season);

        team = TeamDAO.save(team);

        return team;
    }

    private void extractGoals(Node node, Fixture fixture) {
        GoalDAO.deleteForFixture(fixture);

        NodeList goalList = XmlUtilsExt.getXPathResultSet(node, ".//Goal");
        for (int i = 0; i < goalList.getLength(); i++) {
            Node goalNode = goalList.item(i);
            extractGoal(goalNode, fixture);
        }

    }

    private Goal extractGoal(Node node, Fixture fixture) {
        String teamId = stripId(getAttribute(node.getParentNode().getAttributes(), "TeamRef"));
        String playerId = stripId(getAttribute(node.getAttributes(), "PlayerRef"));
        String period = getAttribute(node.getAttributes(), "Period");
        String goalType = getAttribute(node.getAttributes(), "Type");

        Team team = new TeamDAO().getByExtId(teamId);
        Player player = new PlayerDAO().getByExtId(playerId);
        if (player == null) {
            player = new Player(playerId);
            player.setTeam(team);
            PlayerDAO.saveNew(player);
        }

        Goal goal = new Goal();
        goal.setFixture(fixture);
        goal.setPeriod(period);
        goal.setGoalType(goalType);
        goal.setPlayer(player);
        GoalDAO.saveNew(goal);

        return goal;
    }

}
