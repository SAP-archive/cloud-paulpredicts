package com.sap.pto.dao.entities;

import java.util.List;

import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.entities.Fixture.Result;

public class UserStatistics {
    private long numberOfPredictions;
    private long correctPredictions;
    private List<Prediction> predictions;

    public UserStatistics() {
    }

    public UserStatistics(User user) {
        List<Prediction> predictions = PredictionDAO.getPastForUser(user);
        setPredictions(predictions);
        setNumberOfPredictions(predictions.size());
        setCorrectPredictions(calculateCorrectPredictions(predictions));
    }

    public long getNumberOfPredictions() {
        return numberOfPredictions;
    }

    public void setNumberOfPredictions(long numberOfPredictions) {
        this.numberOfPredictions = numberOfPredictions;
    }

    public long getCorrectPredictions() {
        return correctPredictions;
    }

    public void setCorrectPredictions(long correctPredictions) {
        this.correctPredictions = correctPredictions;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    private long calculateCorrectPredictions(List<Prediction> predictions) {
        int numCorrect = 0;

        for (Prediction prediction : predictions) {
            if (!prediction.getResult().equals(Result.NONE) && prediction.getResult().equals(prediction.getFixture().getResult())) {
                numCorrect += 1;
            }
        }

        return numCorrect;
    }

}
