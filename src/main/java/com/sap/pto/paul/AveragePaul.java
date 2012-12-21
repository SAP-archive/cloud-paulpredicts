package com.sap.pto.paul;

import java.util.List;
import java.util.Random;

import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Fixture.Result;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.dao.entities.User;
import com.sap.pto.util.UserUtil;

/**
 * Implements the logic for Paul's betting behavior based on a crowd-sourced approach, 
 * taking into account all predictions and betting on the average.
 *
 */
public class AveragePaul {
    /**
     * High-Speed version for synchronous calls which only adds missing predictions. 
     */
    public synchronized static void predictMissingFixtures(List<Fixture> fixtures, List<Prediction> predictions) {
        User paul = UserUtil.getPaul();

        for (Fixture fixture : fixtures) {
            boolean found = false;
            for (Prediction prediction : predictions) {
                if (prediction.getUser().equals(paul) && prediction.getFixture().getId() == fixture.getId()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                Prediction prediction = new Prediction(paul, fixture, getPaulPrediction(fixture));
                PredictionDAO.saveNew(prediction);
                predictions.add(prediction);
            }
        }
    }

    public synchronized static void predictFixtures(List<Fixture> fixtures) {
        User paul = UserUtil.getPaul();

        for (Fixture fixture : fixtures) {
            Prediction prediction = PredictionDAO.getForUserAndFixture(paul, fixture);
            if (prediction == null) {
                prediction = new Prediction(paul, fixture, getPaulPrediction(fixture));
                PredictionDAO.saveNew(prediction);
            } else {
                prediction.setResult(getPaulPrediction(fixture));
                PredictionDAO.save(prediction);
            }
        }
    }

    public static Result getPaulPrediction(Fixture fixture) {
        long[] count = new long[Result.values().length];

        List<Prediction> predictions = PredictionDAO.getForFixture(fixture);
        for (Prediction prediction : predictions) {
            count[prediction.getResult().ordinal()] += 1;
        }

        int predictionPos = new Random().nextInt(count.length - 1) + 1; // start off with a random prediction
        for (int i = 0; i < count.length; i++) {
            if (count[i] > count[predictionPos]) {
                predictionPos = i;
            }
        }

        return Result.values()[predictionPos];
    }
}
