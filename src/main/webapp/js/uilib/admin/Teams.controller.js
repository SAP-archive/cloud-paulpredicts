sap.ui.controller("sap.pto.admin.Teams", {

    onInit : function() {
        this.teamsModel = new sap.ui.model.json.JSONModel();
        this.getView().setModel(this.teamsModel);
        this.update();
    },

    update : function() {
        loadRestDataSync(this.teamsModel, "/server/b/api/teamservice/teams");
    },

    createTeam : function(data) {
        var _this = this;

        jQuery.ajax({
            url : "/server/b/api/adminservice/team",
            type : 'POST',
            contentType : 'application/json',
            data : data,
            success : function(data) {
                _this.update();
            }
        });
    }

});
