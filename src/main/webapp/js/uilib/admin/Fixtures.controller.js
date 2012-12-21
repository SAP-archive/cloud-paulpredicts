sap.ui.controller("sap.pto.admin.Fixtures", {

    onInit : function() {
        this.fixturesModel = new sap.ui.model.json.JSONModel();
        this.getView().setModel(this.fixturesModel);
        this.update();
    },

    update : function() {
        loadRestDataSync(this.fixturesModel, "/server/b/api/fixtureservice/allfixtures");
    },

    createFixture : function(data) {
        var _this = this;

        jQuery.ajax({
            url : "/server/b/api/adminservice/fixture",
            type : 'POST',
            contentType : 'application/json',
            data : data,
            success : function(data) {
                _this.update();
            }
        });
    },

    createDummyFixture : function(data) {
        var _this = this;

        jQuery.ajax({
            url : "/server/b/api/adminservice/dummyfixture",
            type : 'POST',
            contentType : 'application/json',
            data : data,
            success : function(data) {
                _this.update();
            }
        });
    }

});
