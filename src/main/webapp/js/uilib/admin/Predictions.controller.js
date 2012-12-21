sap.ui.controller("sap.pto.admin.Predictions", {

    onInit : function() {
        this.predictionsModel = new sap.ui.model.json.JSONModel();
        this.getView().setModel(this.predictionsModel);
        this.update();
    },

    update : function() {
        loadRestDataSync(this.predictionsModel, "/server/b/api/adminservice/allpredictions");
    },

    createDummyPrediction : function(data) {
        var _this = this;

        jQuery.ajax({
            url : "/server/b/api/adminservice/dummyprediction",
            type : 'POST',
            contentType : 'application/json',
            data : data,
            success : function(data) {
                _this.update();
            }
        });
    }

});
