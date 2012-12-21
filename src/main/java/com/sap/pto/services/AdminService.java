package com.sap.pto.services;

import static org.quartz.impl.matchers.GroupMatcher.jobGroupEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.LeagueDAO;
import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.League;
import com.sap.pto.dao.entities.LeagueMember;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.User;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.services.util.GsonMessageBodyHandler;
import com.sap.pto.startup.AppInitializer;
import com.sap.pto.util.SecurityUtil;
import com.sap.pto.util.UserUtil;

@Path("/adminservice")
public class AdminService extends BasicService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @POST
    @Path("/clearcache")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCaches() {
        PersistenceAdapter.clearCache();

        return RESPONSE_OK;
    }

    @POST
    @Path("/testmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendTestMail() {
        User user = userUtil.getLoggedInUser(request);
        UserUtil.sendWelcomeMail(user);

        return RESPONSE_OK;
    }

    @GET
    @Path("/allusers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        GsonMessageBodyHandler.setSkipDataProtection(true);
        List<User> users = new UserDAO().getAllDesc();

        return users;
    }

    @POST
    @Path("/userpassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setUserPassword(User user) {
        User existing = new UserDAO().getById(user.getId());
        if (existing == null) {
            throwNotFound();
        }
        existing.setPasswordHash(SecurityUtil.getPasswordHash(existing.getUserName(), user.getPassword()));
        existing = UserDAO.save(existing);

        return Response.ok(existing).build();
    }

    @POST
    @Path("/dummyleague")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDummyLeague(League league) {
        List<User> users = new UserDAO().getAll();

        league.getMembers().add(new LeagueMember(users.get(0)));
        league.getMembers().add(new LeagueMember(users.get(1)));

        LeagueDAO.saveNew(league);

        return RESPONSE_OK;
    }

    @GET
    @Path("/allleagues")
    @Produces(MediaType.APPLICATION_JSON)
    public List<League> getAllLeagues() {
        GsonMessageBodyHandler.setSkipDataProtection(true);
        List<League> leagues = new LeagueDAO().getAllDesc();

        return leagues;
    }

    @POST
    @Path("/team")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTeam(Team team) {
        TeamDAO.saveNew(team);

        return Response.ok(team).build();
    }

    @POST
    @Path("/fixture")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createFixture(Fixture fixture) {
        FixtureDAO.saveNew(fixture);

        return Response.ok(fixture).build();
    }

    @POST
    @Path("/fixtureresult")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setFixtureResult(Fixture fixture) {
        Fixture existing = new FixtureDAO().getById(fixture.getId());
        if (existing == null) {
            throwNotFound();
        }
        existing.setResult(fixture.getResult());
        existing.setScore(fixture.getScore());
        existing = FixtureDAO.save(existing);

        return Response.ok(existing).build();
    }

    @POST
    @Path("/dummyfixture")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDummmyFixture(Fixture fixture) {
        List<Team> teams = new TeamDAO().getAll();

        Random r = new Random();
        fixture.setMatchDate(new DateTime(2012, r.nextInt(11) + 1, r.nextInt(27) + 1, 18, 00).toDate());
        fixture.setAwayTeam(teams.get(r.nextInt(teams.size())));
        do {
            fixture.setHomeTeam(teams.get(r.nextInt(teams.size())));
        } while (fixture.getAwayTeam().equals(fixture.getHomeTeam()));

        fixture.setResult(Result.values()[r.nextInt(Result.values().length)]);
        fixture.setScore(r.nextInt(5) + ":" + r.nextInt(5));

        FixtureDAO.saveNew(fixture);

        return RESPONSE_OK;
    }

    @POST
    @Path("/dummyprediction")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDummmyPrediction(Prediction prediction) {
        List<Fixture> fixtures = new FixtureDAO().getAll();
        List<User> users = new UserDAO().getAll();

        Random r = new Random();
        prediction.setFixture(fixtures.get(r.nextInt(fixtures.size())));
        prediction.setUser(users.get(r.nextInt(users.size())));

        PredictionDAO.saveNew(prediction);

        return RESPONSE_OK;
    }

    @GET
    @Path("/allpredictions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Prediction> getAllPredictions() {
        GsonMessageBodyHandler.setSkipDataProtection(true);
        List<Prediction> predictions = new PredictionDAO().getAllDesc();

        return predictions;
    }

    @GET
    @Path("/jobs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<NameValuePair> getAllJobs() {
        GsonMessageBodyHandler.setSkipDataProtection(true);

        try {
            return getJobList();
        } catch (SchedulerException e) {
            logger.error("Could not retrieve all jobs.", e);
            return null;
        }
    }

    @POST
    @Path("/jobs/{name}/start")
    public Response startJob(@PathParam("name") String name) {
        name = SecurityUtil.textOnly(name);

        Scheduler scheduler = AppInitializer.getScheduler();
        try {
            scheduler.triggerJob(new JobKey(name, name));
            logger.info("Job '" + name + "' has been triggered.");

            return RESPONSE_OK;
        } catch (SchedulerException e) {
            logger.error("Could not manually trigger job '" + name + "'.", e);
            throwError(Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return RESPONSE_BAD;
    }

    @POST
    @Path("/jobs/{name}/pause")
    public Response pauseJob(@PathParam("name") String name) {
        name = SecurityUtil.textOnly(name);

        Scheduler scheduler = AppInitializer.getScheduler();
        try {
            scheduler.pauseJob(new JobKey(name, name));
            logger.info("Job '" + name + "' has been paused.");

            return RESPONSE_OK;
        } catch (SchedulerException e) {
            logger.error("Could not pause job '" + name + "'.", e);
            throwError(Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return RESPONSE_BAD;
    }

    @POST
    @Path("/jobs/{name}/resume")
    public Response resumeJob(@PathParam("name") String name) {
        name = SecurityUtil.textOnly(name);

        Scheduler scheduler = AppInitializer.getScheduler();
        try {
            scheduler.resumeJob(new JobKey(name, name));
            logger.info("Job '" + name + "' has been resumed.");

            return RESPONSE_OK;
        } catch (SchedulerException e) {
            logger.error("Could not resume job '" + name + "'.", e);
            throwError(Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return RESPONSE_BAD;
    }

    private List<NameValuePair> getJobList() throws SchedulerException {
        List<NameValuePair> jobInfo = new ArrayList<NameValuePair>();

        Scheduler scheduler = AppInitializer.getScheduler();
        if (scheduler == null) {
            return jobInfo;
        }

        List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(jobGroupEquals(groupName))) {
                String state = "not running";
                for (JobExecutionContext job : jobs) {
                    if (jobKey.equals(job.getJobDetail().getKey())) {
                        state = "running";
                    }
                }

                // get triggers and their trigger times
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                if (triggers.size() > 0) {
                    state += " (";
                    for (Trigger trigger : triggers) {
                        Date previous = trigger.getPreviousFireTime();
                        Date next = trigger.getNextFireTime();
                        String name = trigger.getKey().getName();
                        TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

                        state += name + ": prev: " + previous + ", next: " + next + ", " + triggerState;
                    }
                    state += ")";
                }

                jobInfo.add(new BasicNameValuePair(jobKey.getName(), state));
            }
        }

        return jobInfo;
    }

}
