package com.sap.pto.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.LeagueDAO;
import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.League;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.dao.entities.User;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.services.LeagueService;
import com.sap.pto.testutil.HttpServletRequestMock;
import com.sap.pto.testutil.PTOTest;
import com.sap.pto.util.UserUtil;

@SuppressWarnings("nls")
public class LeagueServiceTest extends PTOTest {
    private LeagueService leagueService;

    @Before
    public void setup() throws Exception {
        prepareTest();

        leagueService = new LeagueService();
        leagueService.request = requestMock;
    }

    @Test
    public void testCreateLeague() throws Exception {
        League league = new League(simpleUser, "1");
        Response response = leagueService.createLeague(league);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        List<League> leagues = leagueService.getUserLeagues();
        assertEquals(1, leagues.size());
        assertEquals(2, leagues.get(0).getMembers().size());
    }

    @Test(expected = WebApplicationException.class)
    public void testCreateLeagueTwice() throws Exception {
        League league = new League(simpleUser, "1");
        Response response = leagueService.createLeague(league);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        leagueService.createLeague(league);
    }

    @Test(expected = WebApplicationException.class)
    public void testCreateUnnamedLeague() throws Exception {
        League league = new League(simpleUser, "");
        leagueService.createLeague(league);
    }

    @Test
    public void testCreateLeagueUnauthorized() throws Exception {
        League league = new League(UserUtil.getPaul(), "1");
        leagueService.createLeague(league);

        List<League> leagues = new LeagueDAO().getAll();
        assertEquals(1, leagues.size());
        assertEquals(simpleUser, leagues.get(0).getOwner());
    }

    @Test
    public void testJoinLeague() throws Exception {
        League league = new League(UserUtil.getPaul(), "1");
        LeagueDAO.saveNew(league);

        Response response = leagueService.joinLeague(league.getAccessKey());
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        League league2 = LeagueDAO.getLeagueByKey(league.getAccessKey());
        assertEquals(1, league2.getMembers().size());
        assertEquals(simpleUser.getUserName(), league2.getMembers().get(0).getUser().getUserName());
    }

    @Test(expected = WebApplicationException.class)
    public void testJoinLeagueTwice() throws Exception {
        League league = new League(UserUtil.getPaul(), "1");
        LeagueDAO.saveNew(league);

        Response response = leagueService.joinLeague(league.getAccessKey());
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        response = leagueService.joinLeague(league.getAccessKey());
    }

    @Test
    public void testLeaveLeague() throws Exception {
        League league = new League(UserUtil.getPaul(), "1");
        LeagueDAO.saveNew(league);

        leagueService.joinLeague(league.getAccessKey());
        league = LeagueDAO.getLeagueByKey(league.getAccessKey());
        assertEquals(1, league.getMembers().size());

        leagueService.leaveLeague(league.getId());
        league = LeagueDAO.getLeagueByKey(league.getAccessKey());
        assertNull(league);
    }

    @Test
    public void testLeaveOwnLeague() throws Exception {
        League league = new League(simpleUser, "1");
        leagueService.createLeague(league);
        List<League> leagues = leagueService.getUserLeagues();
        assertEquals(1, leagues.size());
        assertEquals(2, leagues.get(0).getMembers().size());

        leagueService.leaveLeague(leagues.get(0).getId());
        leagues = leagueService.getUserLeagues();
        assertEquals(0, leagues.size());

        assertEquals(0, new LeagueDAO().getCount());
    }

    @Test
    public void testDeleteLeagueIfLastToLeave() throws Exception {
        User someUser = new User("someuser", "someuser@test.com");
        UserDAO.saveNew(someUser);
        requestMock = new HttpServletRequestMock(someUser);
        leagueService.request = requestMock;

        League league = new League(someUser, "1");
        leagueService.createLeague(league);
        league = LeagueDAO.getForOwner(someUser).get(0);

        // switch to test user
        requestMock = new HttpServletRequestMock(simpleUser);
        leagueService.request = requestMock;

        leagueService.joinLeague(league.getAccessKey());
        List<League> leagues = leagueService.getUserLeagues();
        assertEquals(1, leagues.size());
        assertEquals(3, leagues.get(0).getMembers().size());

        // remove owner
        requestMock = new HttpServletRequestMock(someUser);
        leagueService.request = requestMock;
        leagueService.leaveLeague(league.getId());

        // switch to test user
        requestMock = new HttpServletRequestMock(simpleUser);
        leagueService.request = requestMock;

        leagues = leagueService.getUserLeagues();
        assertEquals(1, leagues.size());
        assertEquals(2, leagues.get(0).getMembers().size());

        leagueService.leaveLeague(leagues.get(0).getId());
        leagues = leagueService.getUserLeagues();
        assertEquals(0, leagues.size());
        assertEquals(0, new LeagueDAO().getCount());
    }

    @Test(expected = WebApplicationException.class)
    public void testJoinNonExistingLeague() throws Exception {
        leagueService.joinLeague("123");
    }

    @Test
    public void testGetLeagues() throws Exception {
        League pauls = new League(UserUtil.getPaul(), "2");
        League mine = new League(simpleUser, "1");
        LeagueDAO.saveNew(pauls);
        leagueService.createLeague(mine);

        List<League> leagues = leagueService.getUserLeagues();
        assertEquals(1, leagues.size());
        assertEquals("1", leagues.get(0).getName());

        // now join Paul's leage
        leagueService.joinLeague(pauls.getAccessKey());
        leagues = leagueService.getUserLeagues();
        assertEquals(2, leagues.size());
    }

    @Test
    public void testGetLeague() throws Exception {
        League league = new League(simpleUser, "1");
        LeagueDAO.saveNew(league);

        league = leagueService.getLeague(league.getId());
        assertEquals("1", league.getName());
    }

    @Test
    public void testGetLeagueAsMember() throws Exception {
        League league = new League(UserUtil.getPaul(), "1");
        LeagueDAO.saveNew(league);
        leagueService.joinLeague(league.getAccessKey());

        League league2 = leagueService.getLeague(league.getId());
        assertEquals("1", league2.getName());
    }

    @Test(expected = WebApplicationException.class)
    public void testNonExistingGetLeague() throws Exception {
        leagueService.getLeague(99);
    }

    @Test(expected = WebApplicationException.class)
    public void testUnauthorizedGetLeague() throws Exception {
        League league = new League(UserUtil.getPaul(), "1");
        LeagueDAO.saveNew(league);

        leagueService.getLeague(league.getId());
    }

    @Test
    public void testGetLeagueForFixture() throws Exception {
        setupSampleData();

        User user1 = new User("1", "1@1.com");
        UserDAO.saveNew(user1);

        League league = new League(user1, "1");
        LeagueDAO.saveNew(league);
        leagueService.joinLeague(league.getAccessKey());

        // initially no predictions must exist
        League league2 = leagueService.getLeague(league.getId(), fixture1_2.getId());
        assertEquals(1, league2.getMembers().size());
        assertNull(league2.getMembers().get(0).getPrediction());

        // now create predictions
        PredictionDAO.saveNew(new Prediction(user1, fixture1_2, Result.AWAYWIN));
        PredictionDAO.saveNew(new Prediction(simpleUser, fixture1_2, Result.DRAW));

        // must not be shown yet since match is not over
        league2 = leagueService.getLeague(league.getId(), fixture1_2.getId());
        assertNull(league2.getMembers().get(0).getPrediction());

        // simulate end of match
        fixture1_2.setResult(Result.DRAW);
        fixture1_2 = FixtureDAO.save(fixture1_2);
        league2 = leagueService.getLeague(league.getId(), fixture1_2.getId());
        assertNotNull(league2.getMembers().get(0).getPrediction());
    }
}
