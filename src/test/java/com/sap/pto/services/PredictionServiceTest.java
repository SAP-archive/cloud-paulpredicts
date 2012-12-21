package com.sap.pto.services;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.WebApplicationException;

import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.dao.entities.User;
import com.sap.pto.dao.entities.UserStatistics;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.services.PredictionService;
import com.sap.pto.testutil.PTOTest;
import com.sap.pto.util.UserUtil;

@SuppressWarnings("nls")
public class PredictionServiceTest extends PTOTest {
    private PredictionService predictionService;
    private User paul;

    @Before
    public void setup() throws Exception {
        prepareTest();

        predictionService = new PredictionService();
        predictionService.request = requestMock;

        DateTimeUtils.setCurrentMillisFixed(testDay.getMillis());
        setupSampleData();

        paul = UserUtil.getPaul();
    }

    @After
    public void teardown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testGetUserStatisticsEmpty() throws Exception {
        UserStatistics stats = predictionService.getUserStatistics(paul.getId());

        assertEquals(0, stats.getNumberOfPredictions());
        assertEquals(0, stats.getCorrectPredictions());
    }

    @Test
    public void testGetPaulStatisticsNoPast() throws Exception {
        PredictionDAO.saveNew(new Prediction(paul, fixture1_2, Result.AWAYWIN));

        UserStatistics stats = predictionService.getUserStatistics(paul.getId());
        assertEquals(0, stats.getNumberOfPredictions());
        assertEquals(0, stats.getCorrectPredictions());
    }

    @Test
    public void testGetPaulStatistics() throws Exception {
        fixture1_2.setResult(Result.AWAYWIN);
        fixture1_2 = FixtureDAO.save(fixture1_2);

        PredictionDAO.saveNew(new Prediction(paul, fixture1_2, Result.AWAYWIN));
        PredictionDAO.saveNew(new Prediction(paul, fixture3_4, Result.DRAW));

        UserStatistics stats = predictionService.getUserStatistics(paul.getId());
        assertEquals(1, stats.getNumberOfPredictions());
        assertEquals(1, stats.getCorrectPredictions());

        // add an incorrect prediction
        fixture3_4.setResult(Result.HOMEWIN);
        fixture3_4 = FixtureDAO.save(fixture3_4);
        stats = predictionService.getUserStatistics(paul.getId());
        assertEquals(2, stats.getNumberOfPredictions());
        assertEquals(1, stats.getCorrectPredictions());
    }

    @Test
    public void testCreatePrediction() throws Exception {
        predictionService.createPrediction(new Prediction(simpleUser, fixture1_2, Result.AWAYWIN));
        assertEquals(1, predictionService.getUserPredictions().size());
        assertEquals(Result.AWAYWIN, predictionService.getUserPredictions().get(0).getResult());
    }

    @Test(expected = WebApplicationException.class)
    public void testCreatePredictionTooLate() throws Exception {
        DateTimeUtils.setCurrentMillisFixed(testDay.plusDays(1).getMillis());
        predictionService.createPrediction(new Prediction(simpleUser, fixture1_2, Result.AWAYWIN));
    }

    @Test(expected = WebApplicationException.class)
    public void testCreatePredictionWithNoResult() throws Exception {
        predictionService.createPrediction(new Prediction(simpleUser, fixture1_2, null));
    }

    @Test(expected = WebApplicationException.class)
    public void testCreatePredictionTwice() throws Exception {
        predictionService.createPrediction(new Prediction(simpleUser, fixture1_2, Result.AWAYWIN));
        predictionService.createPrediction(new Prediction(simpleUser, fixture1_2, Result.DRAW));
    }

    @Test
    public void testUpdatePrediction() throws Exception {
        predictionService.createPrediction(new Prediction(simpleUser, fixture1_2, Result.AWAYWIN));
        predictionService.updatePrediction(new Prediction(simpleUser, fixture1_2, Result.DRAW));
        assertEquals(1, predictionService.getUserPredictions().size());
        assertEquals(Result.DRAW, predictionService.getUserPredictions().get(0).getResult());
    }
}
