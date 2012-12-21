sap.ui.controller("sap.pto.user.Home", {

    onInit : function() {
    },

    getUserSuccessRate : function(userID) {
        var userPredictionStatisticsModel = new sap.ui.model.json.JSONModel();
        loadRestDataSync(userPredictionStatisticsModel, "/server/b/api/predictionservice/statistics/" + userID);

        var numberOfPredictions = userPredictionStatisticsModel.getProperty("/numberOfPredictions");
        var correctPredictions = userPredictionStatisticsModel.getProperty("/correctPredictions");
        var successRate = 0;
        if (numberOfPredictions != 0) {
            successRate = parseInt((correctPredictions / numberOfPredictions) * 100);
        }
        return successRate;
    }
});
