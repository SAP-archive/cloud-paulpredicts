sap.ui.controller("sap.pto.user.FutureFixtures", {

    onInit : function() {
        this.userPredictionsModel = new sap.ui.model.json.JSONModel();
        this.getView().setModel(this.userPredictionsModel);
        this.update();
    },

    update : function() {
        loadRestDataSync(this.userPredictionsModel, "/server/b/api/fixtureservice/futurefixtures");
    },

    makePrediction : function(predictionValue, model, fixture) {
        var _this = this;
        var data = '{"result":"' + predictionValue + '","fixture":' + JSON.stringify(fixture) + '}';

        jQuery.ajax({
            url : "/server/b/api/predictionservice/predictions",
            type : 'POST',
            contentType : 'application/json',
            data : data,
            success : function(data) {
                _this.updatePredictionModel(model, fixture.id);
                _this.update();
            },
            error : function() {
                jQuery.ajax({
                    url : "/server/b/api/predictionservice/predictions",
                    type : 'PUT',
                    contentType : 'application/json',
                    data : data,
                    success : function(data) {
                        _this.updatePredictionModel(model, fixture.id);
                        _this.update();
                    }
                });
            }
        });
    },

    updatePredictionModel : function(model, fixtureId) {
        loadRestDataSync(model, "/server/b/api/predictionservice/predictions/" + fixtureId);
    }
});
