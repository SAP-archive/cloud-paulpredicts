package com.sap.pto.importers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.PlayerDAO;
import com.sap.pto.dao.PlayerStatDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.TeamStatDAO;
import com.sap.pto.dao.entities.Player;
import com.sap.pto.dao.entities.PlayerStat;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.TeamStat;
import com.sap.pto.testutil.PTOTest;
import com.sap.pto.util.MiscUtils;

@SuppressWarnings("nls")
public class OptaStatsImporterTest extends PTOTest {
    @Before
    public void setup() throws Exception {
        prepareTest();
    }

    @Test
    public void testImportStats() throws Exception {
        importStats();

        assertEquals(1, new TeamDAO().getCount());
        assertEquals(76, new TeamStatDAO().getCount());
        assertEquals(4, new PlayerDAO().getCount());
        assertEquals(139, new PlayerStatDAO().getCount());

        Team team = new TeamDAO().getByExtId("156");
        assertEquals("Team 156", team.getName());
        assertEquals("5", team.getCompetition().getExtId());
        assertEquals("2012", team.getSeason().getExtId());
        assertNotNull(team.getCompetition());
        assertNotNull(team.getSeason());
        assertEquals(76, TeamStatDAO.getForTeam(team).size());

        TeamStat teamStat = TeamStatDAO.getForTeam(team, "Duels won");
        assertEquals("134", teamStat.getStatValue());

        Player player = new PlayerDAO().getByExtId("26894");
        assertEquals("Defender", player.getPosition());
        assertEquals("F4", player.getFirstName());
        assertEquals("L4", player.getLastName());
        assertEquals("K4", player.getNickName());
        assertNotNull(player.getTeam());
        assertEquals(45, PlayerStatDAO.getForPlayer(player).size());

        PlayerStat playerStat = PlayerStatDAO.getForPlayer(player, "Total Passes");
        assertEquals("123", playerStat.getStatValue());
    }

    private void importStats() throws URISyntaxException, IOException {
        URI results = MiscUtils.getResource("opta/seasonstats-5-2012-156.xml").toURI();
        new OptaStatsImporter().importData(results);
    }
}
