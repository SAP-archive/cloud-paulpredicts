package com.sap.pto.services;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.LeagueDAO;
import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.League;
import com.sap.pto.dao.entities.LeagueMember;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.dao.entities.User;
import com.sap.pto.dao.entities.UserStatistics;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.paul.AveragePaul;
import com.sap.pto.util.UserUtil;

@Path("/leagueservice")
public class LeagueService extends BasicService {
    @POST
    @Path("/leagues")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createLeague(League league) {
        User user = userUtil.getLoggedInUser(request);
        league.setName(sanitize(league.getName()));

        if (StringUtils.isBlank(league.getName())) {
            throwBadRequest("Name must be supplied.");
        }

        List<League> leagues = LeagueDAO.getForOwner(user);
        for (League existingLeague : leagues) {
            if (existingLeague.getName().equalsIgnoreCase(league.getName())) {
                throwError(Status.CONFLICT, "League with that name already exists.");
            }
        }

        // copy settings so that user cannot create a league for someone else
        League newLeague = new League(user, league.getName());
        LeagueDAO.saveNew(newLeague);

        // join
        joinLeague(newLeague.getAccessKey());

        // smuggle in Paul into every league
        User paul = UserUtil.getPaul();
        newLeague = LeagueDAO.getLeagueByKey(newLeague.getAccessKey());
        newLeague.getMembers().add(new LeagueMember(paul));
        newLeague = LeagueDAO.save(newLeague);

        return Response.ok(newLeague).build();
    }

    @GET
    @Path("/leagues/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public League getLeague(@PathParam("id") long id) {
        User user = userUtil.getLoggedInUser(request);
        League league = new LeagueDAO().getById(id);
        if (league == null) {
            throwBadRequest();
        }
        if (!league.getUsers().contains(user)) {
            throwUnauthorized();
        }

        for (LeagueMember member : league.getMembers()) {
            UserStatistics stats = new UserStatistics(member.getUser());
            member.setStatistics(stats);
        }

        return league;
    }

    @GET
    @Path("/leagues/{id}/{fixtureid}")
    @Produces(MediaType.APPLICATION_JSON)
    public League getLeague(@PathParam("id") long id, @PathParam("fixtureid") long fixtureId) {
        User user = userUtil.getLoggedInUser(request);

        League league = getLeague(id);
        Fixture fixture = new FixtureDAO().getById(fixtureId);
        if (fixture == null) {
            throwNotFound("Fixture could not be found.");
        }

        // check if league members predicted the game and return their predictions 
        List<Prediction> predictions = PredictionDAO.getPastForFixture(fixture); // FIXME: inefficient for many users, actually only pull those predictions of the league members
        AveragePaul.predictMissingFixtures(Arrays.asList(fixture), predictions);

        for (Prediction prediction : predictions) {
            for (LeagueMember member : league.getMembers()) {
                if (prediction.getUser().equals(member.getUser())) {
                    if (prediction.getUser().equals(user) || fixture.getResult() != Result.NONE) {
                        member.setPrediction(prediction);
                    }
                }
            }
        }

        return league;
    }

    @POST
    @Path("/join/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response joinLeague(@PathParam("key") String key) {
        League league = LeagueDAO.getLeagueByKey(key);
        if (league == null) {
            throwBadRequest("League could not be found.");
        }

        User user = userUtil.getLoggedInUser(request);

        // don't allow to join twice
        for (LeagueMember member : league.getMembers()) {
            if (member.getUser().equals(user)) {
                throwError(Status.CONFLICT, "User is already a league member.");
            }
        }
        league.getMembers().add(new LeagueMember(user));
        league = LeagueDAO.save(league);

        return RESPONSE_OK;
    }

    @POST
    @Path("/leagues/{id}/leave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response leaveLeague(@PathParam("id") long leagueId) {
        League league = new LeagueDAO().getById(leagueId);
        if (league == null) {
            throwBadRequest("League could not be found.");
        }

        User user = userUtil.getLoggedInUser(request);

        for (Iterator<LeagueMember> iterator = league.getMembers().iterator(); iterator.hasNext();) {
            LeagueMember member = iterator.next();
            if (member.getUser().equals(user)) {
                iterator.remove();
                league = LeagueDAO.save(league);

                // delete league if only bots are left
                // FIXME: will not work if second bot comes into play
                if (league.getMembers().size() == 1) {
                    league.getMembers().clear();
                    league = LeagueDAO.save(league);
                }
                if (league.getMembers().size() == 0) {
                    new LeagueDAO().deleteById(league.getId());
                }

                return RESPONSE_OK;
            }
        }

        return RESPONSE_BAD;
    }

    @DELETE
    @Path("/leagues/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteLeague(@PathParam("id") long leagueId) {
        League league = new LeagueDAO().getById(leagueId);
        if (league == null) {
            throwBadRequest("League could not be found.");
        }
        User user = userUtil.getLoggedInUser(request);
        if (!league.getOwner().equals(user)) {
            throwUnauthorized("Only the league owner can delete a league.");
        }

        // TODO: implement

        return RESPONSE_OK;
    }

    @GET
    @Path("/leagues")
    @Produces(MediaType.APPLICATION_JSON)
    public List<League> getUserLeagues() {
        User user = userUtil.getLoggedInUser(request);
        List<League> leagues = LeagueDAO.getForUser(user);

        return leagues;
    }

}
