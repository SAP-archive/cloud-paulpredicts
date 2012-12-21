sap.ui.controller("sap.pto.user.PastFixtures", {

    onInit : function() {
        this.userPredictionsModel = new sap.ui.model.json.JSONModel();
        this.getView().setModel(this.userPredictionsModel);
        this.update();
    },

    update : function() {
        loadRestDataSync(this.userPredictionsModel, "/server/b/api/fixtureservice/pastfixtures");
    }
});
