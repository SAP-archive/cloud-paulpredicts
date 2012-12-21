sap.ui.controller("sap.pto.admin.Users", {

    onInit : function() {
        this.usersModel = new sap.ui.model.json.JSONModel();
        this.getView().setModel(this.usersModel);
        this.update();
    },

    update : function() {
        loadRestDataSync(this.usersModel, "/server/b/api/adminservice/allusers");
    },

    createUser : function(data) {
        var _this = this;

        jQuery.ajax({
            url : "/server/b/api/anonuserservice/users",
            type : 'POST',
            contentType : 'application/json',
            data : data,
            success : function(data) {
                _this.update();
            }
        });
    }

});
