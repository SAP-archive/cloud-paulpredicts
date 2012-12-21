sap.ui.controller("sap.pto.admin.Jobs", {

    onInit : function() {
        this.jobsModel = new sap.ui.model.json.JSONModel();
        this.getView().setModel(this.jobsModel);
        this.update();
    },

    update : function() {
        loadRestDataSync(this.jobsModel, "/server/b/api/adminservice/jobs");
    },

    startJob : function(id) {
        var _this = this;

        jQuery.post("/server/b/api/adminservice/jobs/" + id + "/start", id, function(data) {
            _this.update();
        });
    },

    pauseJob : function(id) {
        var _this = this;

        jQuery.post("/server/b/api/adminservice/jobs/" + id + "/pause", id, function(data) {
            _this.update();
        });
    },

    resumeJob : function(id) {
        var _this = this;

        jQuery.post("/server/b/api/adminservice/jobs/" + id + "/resume", id, function(data) {
            _this.update();
        });
    }

});
