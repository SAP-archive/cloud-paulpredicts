package com.sap.pto.importers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.CompetitionDAO;
import com.sap.pto.dao.PlayerDAO;
import com.sap.pto.dao.SeasonDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.TeamOfficialDAO;
import com.sap.pto.dao.entities.Competition;
import com.sap.pto.dao.entities.Player;
import com.sap.pto.dao.entities.Season;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.TeamOfficial;
import com.sap.pto.testutil.PTOTest;

@SuppressWarnings("nls")
public class OptaTeamImporterTest extends PTOTest {
    @Before
    public void setup() throws Exception {
        prepareTest();
    }

    @Test
    public void testImportTeams() throws Exception {
        importTeams();

        List<Competition> competitions = new CompetitionDAO().getAll();
        assertEquals(1, competitions.size());
        assertEquals("5", competitions.get(0).getExtId());
        assertEquals("Test League", competitions.get(0).getName());

        List<Season> seasons = new SeasonDAO().getAll();
        assertEquals(1, seasons.size());
        assertEquals("2012", seasons.get(0).getExtId());
        assertEquals("Season 2012/2013", seasons.get(0).getName());

        assertEquals(4, new TeamDAO().getAll().size());
        assertEquals(4, new PlayerDAO().getAll().size());
        assertEquals(7, new TeamOfficialDAO().getAll().size());

        Team team = new TeamDAO().getByExtId("120");
        assertEquals("Team 120", team.getName());
        assertEquals("Team 120", team.getLongName());
        assertEquals("Italy", team.getCountry());
        assertEquals("1999", team.getFoundingDate());
        assertEquals("Europe", team.getRegion());
        assertEquals("ACM", team.getShortName());
        assertEquals("Venue 2", team.getStadiumName());
        assertEquals(8004, team.getStadiumCapacity());
        assertEquals("5", team.getCompetition().getExtId());
        assertEquals("2012", team.getSeason().getExtId());

        Player player = new PlayerDAO().getByExtId("4242");
        assertEquals("F6", player.getFirstName());
        assertEquals("L6", player.getLastName());
        assertEquals("120", player.getTeam().getExtId());

        List<Player> players = new PlayerDAO().getByTeam(team);
        assertEquals(2, players.size());

        List<TeamOfficial> officials = new TeamOfficialDAO().getByTeam(team);
        assertEquals(5, officials.size());
    }

    @Test
    public void testImportTeamsTwice() throws Exception {
        importTeams();
        importTeams();

        assertEquals(4, new TeamDAO().getAll().size());
        assertEquals(4, new PlayerDAO().getAll().size());
        assertEquals(7, new TeamOfficialDAO().getAll().size());
    }
}
