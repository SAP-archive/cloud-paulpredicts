package com.sap.pto.services;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.services.FixtureService;
import com.sap.pto.testutil.PTOTest;

@SuppressWarnings("nls")
public class FixtureServiceTest extends PTOTest {
    private FixtureService fixtureService;

    @Before
    public void setup() throws Exception {
        prepareTest();

        fixtureService = new FixtureService();
        fixtureService.request = requestMock;

        DateTimeUtils.setCurrentMillisFixed(testDay.getMillis());
        setupSampleData();
    }

    @Test
    public void testGetAllFixtures() throws Exception {
        List<Fixture> allFixtures = fixtureService.getAllFixtures();
        assertEquals(2, allFixtures.size());
        assertEquals(0, allFixtures.get(0).getPredictions().size());
    }

    @Test
    public void testGetAllFixturesWithPredictions() throws Exception {
        PredictionDAO.saveNew(new Prediction(simpleUser, fixture1_2, Result.DRAW));
        PredictionDAO.saveNew(new Prediction(simpleUser, fixture3_4, Result.AWAYWIN));

        List<Fixture> allFixtures = fixtureService.getAllFixtures();
        assertEquals(1, allFixtures.get(0).getPredictions().size());
    }

    @Test
    public void testGetAllFixturesWithPredictionsTwice() throws Exception {
        importMatches();

        fixtureService.getAllFixtures();
        assertEquals(new FixtureDAO().getCount(), new PredictionDAO().getCount());

        fixtureService.getAllFixtures();
        assertEquals(new FixtureDAO().getCount(), new PredictionDAO().getCount());
    }

    @Test
    public void testGetFutureFixtures() throws Exception {
        DateTimeUtils.setCurrentMillisFixed(testDay.minusDays(1).getMillis());

        List<Fixture> fixtures = fixtureService.getFutureFixtures();
        assertEquals(2, fixtures.size());

        DateTimeUtils.setCurrentMillisFixed(testDay.plusDays(1).getMillis());

        fixtures = fixtureService.getFutureFixtures();
        assertEquals(0, fixtures.size());
    }

    @Test
    public void testPastFixtures() throws Exception {
        List<Fixture> fixtures = fixtureService.getPastFixtures();
        assertEquals(0, fixtures.size());

        DateTimeUtils.setCurrentMillisFixed(testDay.plusDays(1).getMillis());

        fixtures = fixtureService.getPastFixtures();
        assertEquals(2, fixtures.size());
    }
}
