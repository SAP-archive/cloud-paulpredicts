package com.sap.pto.importers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.GoalDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Goal;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.testutil.PTOTest;

@SuppressWarnings("nls")
public class OptaMatchImporterTest extends PTOTest {
    @Before
    public void setup() throws Exception {
        prepareTest();
    }

    @Test
    public void testImportMatches() throws Exception {
        importMatches();

        List<Fixture> fixtures = new FixtureDAO().getAll();
        assertEquals(2, fixtures.size());

        Team team = new TeamDAO().getByExtId("202");
        fixtures = FixtureDAO.getForTeam(team);
        assertEquals(1, fixtures.size());

        Fixture fixture = new FixtureDAO().getByExtId("476841");
        assertEquals("Venue 2", fixture.getVenue());
        assertEquals("City 2", fixture.getCity());

        List<Goal> goals = GoalDAO.getForFixture(fixture);
        assertEquals(3, goals.size());
        assertEquals("FirstHalf", goals.get(0).getPeriod());
        assertEquals("182", goals.get(0).getPlayer().getTeam().getExtId());
        assertEquals("80209", goals.get(0).getPlayer().getExtId());
    }

    @Test
    public void testImportMatchesTwice() throws Exception {
        importMatches();
        importMatches();

        List<Fixture> fixtures = new FixtureDAO().getAll();
        assertEquals(2, fixtures.size());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testImportMatchesOverExisting() throws Exception {
        importTeams();

        Team team1 = new TeamDAO().getByExtId("182");
        Team team2 = new TeamDAO().getByExtId("1341");
        Fixture preFixture = new Fixture(new DateTime(2012, 9, 12, 10, 0).toDate(), team1, team2); // with incorrect time to check if it will be corrected
        FixtureDAO.saveNew(preFixture);

        importMatches();

        List<Fixture> fixtures = new FixtureDAO().getAll();
        assertEquals(2, fixtures.size());

        fixtures = FixtureDAO.getForTeam(team1);
        for (Fixture fixture : fixtures) {
            if (fixture.getAwayTeam().getExtId().equals(team2.getExtId())) {
                assertEquals(18, fixture.getMatchDate().getHours());
                assertEquals("476841", fixture.getExtId());
                assertEquals("Group C", fixture.getGroupName());
                assertEquals("3:0", fixture.getScore());
                assertEquals(Result.HOMEWIN, fixture.getResult());
                return;
            }
        }
        fail("Fixture to check could not be found.");
    }
}
