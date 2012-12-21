package com.sap.pto.paul;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.testutil.PTOTest;
import com.sap.pto.util.UserUtil;

@SuppressWarnings("nls")
public class PaulTest extends PTOTest {
    @Before
    public void setup() throws Exception {
        prepareTest();

        DateTimeUtils.setCurrentMillisFixed(testDay.getMillis());
        setupSampleData();
    }

    @Test
    public void testGetPaulPrediction() throws Exception {
        PredictionDAO.save(new Prediction(simpleUser, fixture1_2, Result.AWAYWIN));
        assertEquals(Result.AWAYWIN, AveragePaul.getPaulPrediction(fixture1_2));

        PredictionDAO.save(new Prediction(simpleUser, fixture1_2, Result.DRAW));
        PredictionDAO.save(new Prediction(simpleUser, fixture1_2, Result.DRAW));
        assertEquals(Result.DRAW, AveragePaul.getPaulPrediction(fixture1_2));
    }

    @Test
    public void testGetPaulDefaultPrediction() throws Exception {
        // make sure Paul always predicts
        for (int i = 0; i < 100; i++) {
            assertFalse(AveragePaul.getPaulPrediction(fixture1_2).equals(Result.NONE));
        }
    }

    @Test
    public void testPredictMissingFixtures() throws Exception {
        assertEquals(0, new PredictionDAO().getAll().size());
        AveragePaul.predictMissingFixtures(new FixtureDAO().getAll(), new PredictionDAO().getAll());
        assertEquals(2, new PredictionDAO().getAll().size());
    }

    @Test
    public void testPredictMissingFixturesTwice() throws Exception {
        AveragePaul.predictMissingFixtures(new FixtureDAO().getAll(), new PredictionDAO().getAll());
        AveragePaul.predictMissingFixtures(new FixtureDAO().getAll(), new PredictionDAO().getAll());
        assertEquals(2, new PredictionDAO().getAll().size());
    }

    @Test
    public void testPredictUpcomingFixtures() throws Exception {
        PredictionDAO.save(new Prediction(simpleUser, fixture1_2, Result.AWAYWIN));
        AveragePaul.predictFixtures(FixtureDAO.getFuture());
        Prediction prediction = PredictionDAO.getForUserAndFixture(UserUtil.getPaul(), fixture1_2);
        assertEquals(Result.AWAYWIN, prediction.getResult());

        PredictionDAO.save(new Prediction(simpleUser, fixture1_2, Result.HOMEWIN));
        PredictionDAO.save(new Prediction(simpleUser, fixture1_2, Result.HOMEWIN));
        PredictionDAO.save(new Prediction(simpleUser, fixture1_2, Result.HOMEWIN));

        AveragePaul.predictFixtures(FixtureDAO.getFuture());
        prediction = PredictionDAO.getForUserAndFixture(UserUtil.getPaul(), fixture1_2);
        assertEquals(Result.HOMEWIN, prediction.getResult());
    }
}
