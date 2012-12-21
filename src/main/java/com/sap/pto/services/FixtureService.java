package com.sap.pto.services;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sap.pto.dao.EditorialDAO;
import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.GoalDAO;
import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.dao.entities.User;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.paul.AveragePaul;
import com.sap.pto.util.UserUtil;

/**
 * This class is used for retrieving data about fixtures.
 * 
 */
@Path("/fixtureservice")
public class FixtureService extends BasicService {
    @GET
    @Path("/allfixtures")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Fixture> getAllFixtures() {
        List<Fixture> fixtures = new FixtureDAO().getAll();

        return addAdditionalData(fixtures);
    }

    @GET
    @Path("/futurefixtures")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Fixture> getFutureFixtures() {
        List<Fixture> fixtures = FixtureDAO.getFuture();

        return addAdditionalData(fixtures);
    }

    @GET
    @Path("/pastfixtures")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Fixture> getPastFixtures() {
        List<Fixture> fixtures = FixtureDAO.getPast();

        return addAdditionalData(fixtures);
    }

    /**
     * Add predictions if any exist for the fixtures and for the current user and for the special users.
     */
    private List<Fixture> addAdditionalData(List<Fixture> fixtures) {
        if (fixtures.isEmpty()) {
            return fixtures;
        }

        for (Fixture fixture : fixtures) {
            fixture.setEditorials(new EditorialDAO().getByFixture(fixture));
            fixture.setGoals(new GoalDAO().getByFixture(fixture));
        }

        User user = userUtil.getLoggedInUser(request);
        List<User> users = Arrays.asList(user, UserUtil.getPaul());

        if (!users.isEmpty()) {
            List<Prediction> predictions = PredictionDAO.getForUsersAndFixtures(users, fixtures);
            AveragePaul.predictMissingFixtures(fixtures, predictions);

            for (Fixture fixture : fixtures) {
                for (Prediction prediction : predictions) {
                    if (prediction.getFixture().getId() == fixture.getId()) {
                        // only add past predictions for other users
                        if (fixture.getResult() != Result.NONE || prediction.getUser().equals(user)) {
                            fixture.getPredictions().add(prediction);
                        }

                        // save user's prediction separately for easier processing
                        if (prediction.getUser().equals(user)) {
                            fixture.setUserPrediction(prediction);
                        }
                    }
                }
            }

            // remove fixture in prediction here to eliminate cycle
            for (Fixture fixture : fixtures) {
                for (Prediction prediction : fixture.getPredictions()) {
                    prediction.setFixture(null);
                }
                if (fixture.getUserPrediction() != null) {
                    fixture.getUserPrediction().setFixture(null);
                }
            }
        }

        return fixtures;
    }
}
