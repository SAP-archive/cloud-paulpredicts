package com.sap.pto.services;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;

import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.dao.entities.User;
import com.sap.pto.dao.entities.UserStatistics;

@Path("/predictionservice")
public class PredictionService extends BasicService {
    @POST
    @Path("/predictions")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPrediction(Prediction prediction) {
        User user = userUtil.getLoggedInUser(request);
        prediction.setUser(user);

        if (prediction.getResult() == null) {
            throwBadRequest("There was no result supplied.");
        }

        // check if deadline is over
        Fixture fixture = new FixtureDAO().getById(prediction.getFixture().getId());
        if (fixture == null) {
            throwNotFound("Fixture could not be found.");
        }
        if (new DateTime(fixture.getMatchDate()).isBefore(DateTime.now())) {
            throwBadRequest("Predictions can only be entered until the match started.");
        }

        // check if a prediction already exists
        List<Prediction> predictions = PredictionDAO.getForUser(user);
        for (Prediction oldPrediction : predictions) {
            if (oldPrediction.getFixture().getId() == prediction.getFixture().getId()) {
                throwBadRequest("A prediction for this fixture exists already.");
            }
        }

        PredictionDAO.saveNew(prediction);

        return Response.ok(prediction).build();
    }

    @PUT
    @Path("/predictions")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePrediction(Prediction prediction) {
        User user = userUtil.getLoggedInUser(request);
        prediction.setUser(user);

        // find prediction
        List<Prediction> predictions = PredictionDAO.getForUser(user);
        for (Prediction oldPrediction : predictions) {
            if (oldPrediction.getFixture().getId() == prediction.getFixture().getId()) {
                oldPrediction.setResult(prediction.getResult());
                oldPrediction = PredictionDAO.save(oldPrediction);

                return Response.ok(oldPrediction).build();
            }
        }

        throwNotFound("No matching existing prediction was found.");
        return RESPONSE_BAD;
    }

    @GET
    @Path("/predictions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Prediction> getUserPredictions() {
        User user = userUtil.getLoggedInUser(request);

        List<Prediction> predictions = PredictionDAO.getForUser(user);

        return predictions;
    }

    @GET
    @Path("/predictions/{fixtureId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Prediction getUserPredictionForFixture(@PathParam("fixtureId") long fixtureId) {
        User user = this.userUtil.getLoggedInUser(request);
    
        Fixture fixture = new FixtureDAO().getById(fixtureId);
        Prediction prediction = PredictionDAO.getForUserAndFixture(user, fixture);
    
        return prediction;
    }

    @GET
    @Path("/pastpredictions/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Prediction> getUserPastPredictions(@PathParam("id") long userId) {
        User user = new UserDAO().getById(userId);
        if (user == null) {
            throwBadRequest("The supplied user id is invalid.");
        }

        List<Prediction> predictions = PredictionDAO.getPastForUser(user);

        return predictions;
    }

    @GET
    @Path("/statistics/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserStatistics getUserStatistics(@PathParam("id") long userId) {
        User user = new UserDAO().getById(userId);
        if (user == null) {
            throwBadRequest("The supplied user id is invalid.");
        }

        UserStatistics stats = new UserStatistics(user);

        return stats;
    }

}
