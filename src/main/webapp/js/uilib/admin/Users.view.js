sap.ui.jsview("sap.pto.admin.Users", {

    getControllerName : function() {
        return "sap.pto.admin.Users";
    },

    createContent : function(controller) {
        var _this = this;
        var setPasswordButton = new sap.ui.commons.Button({
            text : "Set Password"
        }).attachPress(function(oEvent) {
            var selectedRowContext = oEvent.oSource.getBindingContext();
            var selectedUser = _this.getModel().getProperty("", selectedRowContext);

            _this.openSetPasswordDialog(selectedUser);
        });

        var usersTable = new sap.ui.table.Table({
            columns : [ {
                label : "Id",
                template : "id",
                sortProperty : "id",
                filterProperty : "id",
                width : "50px"
            }, {
                label : "Name",
                template : getUserField(""),
                sortProperty : "userName",
                filterProperty : "userName"
            }, {
                label : "E-Mail Key",
                template : "emailConfirmationKey",
                sortProperty : "emailConfirmationKey",
                filterProperty : "emailConfirmationKey"
            }, {
                label : "Last Login",
                template : "lastLoginDate",
                sortProperty : "lastLoginDate",
                filterProperty : "lastLoginDate"
            }, {
                label : "Action",
                template : setPasswordButton
            } ],
            visibleRowCount : 18,
            selectionMode : "None",
            editable : false
        });
        usersTable.bindRows("/");

        if (SHIROMODE) {
            usersTable.setToolbar(new sap.ui.commons.Toolbar({
                items : [
                        new sap.ui.commons.Label({
                            text : "Test Mode:"
                        }),
                        new sap.ui.commons.Button({
                            text : 'Create John',
                            press : function() {
                                _this.getController().createUser(
                                        '{"userName":"TestUser1", "email":"testuser1@test.com", "password":"1234"}');
                            }
                        }),
                        new sap.ui.commons.Button({
                            text : 'Create Robert',
                            press : function() {
                                _this.getController().createUser(
                                        '{"userName":"TestUser2", "email":"testuser2@test.com", "password":"1234"}');
                            }
                        }),
                        new sap.ui.commons.Button({
                            text : 'Create Sean',
                            press : function() {
                                _this.getController().createUser(
                                        '{"userName":"TestUser3", "email":"testuser3@test.com", "password":"1234"}');
                            }
                        }) ]
            }));
        }

        return usersTable;
    },

    getSetPasswordPanel : function() {
        var layout = new sap.ui.commons.layout.MatrixLayout({
            layoutFixed : false,
            columns : 2
        });

        // ----------------------------
        this.password = new sap.ui.commons.TextField({
            value : '',
            required : true
        });
        addToFormMatrix(layout, "New Password", this.password);

        return layout;
    },

    openSetPasswordDialog : function(user) {
        var _this = this;
        var dialog = new sap.ui.commons.Dialog({
            modal : true
        });
        dialog.setTitle("Set Password");

        dialog.addContent(this.getSetPasswordPanel());
        dialog.addButton(new sap.ui.commons.Button({
            text : "Save",
            press : function() {
                user.password = _this.password.getValue();

                jQuery.ajax({
                    url : "/server/b/api/adminservice/userpassword",
                    type : 'POST',
                    contentType : 'application/json',
                    data : JSON.stringify(user),
                    success : function(data) {
                        dialog.close();
                        _this.getController().update();
                    }
                });
            }
        }));
        dialog.addButton(new sap.ui.commons.Button({
            text : "Cancel",
            press : function() {
                dialog.close();
            }
        }));
        dialog.open();
    }

});
