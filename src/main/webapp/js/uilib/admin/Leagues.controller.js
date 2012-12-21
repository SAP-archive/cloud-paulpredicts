sap.ui.controller("sap.pto.admin.Leagues", {

    onInit : function() {
        this.leaguesModel = new sap.ui.model.json.JSONModel();
        this.getView().setModel(this.leaguesModel);
        this.update();
    },

    update : function() {
        loadRestDataSync(this.leaguesModel, "/server/b/api/adminservice/allleagues");
    },

    createLeague : function(data) {
        var _this = this;

        jQuery.ajax({
            url : "/server/b/api/leagueservice/leagues",
            type : 'POST',
            contentType : 'application/json',
            data : data,
            success : function(data) {
                _this.update();
            }
        });
    },

    createDummyLeague : function(data) {
        var _this = this;

        jQuery.ajax({
            url : "/server/b/api/adminservice/dummyleague",
            type : 'POST',
            contentType : 'application/json',
            data : data,
            success : function(data) {
                _this.update();
            }
        });
    }

});
