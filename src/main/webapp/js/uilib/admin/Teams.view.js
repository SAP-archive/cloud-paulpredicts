sap.ui.jsview("sap.pto.admin.Teams", {

    getControllerName : function() {
        return "sap.pto.admin.Teams";
    },

    createContent : function(controller) {
        var _this = this;
        var teamsTable = new sap.ui.table.Table({
            columns : [ {
                label : "Id",
                template : "id",
                sortProperty : "id",
                filterProperty : "id",
                width : "50px"
            }, {
                label : "External ID",
                template : "extId",
                sortProperty : "extId",
                filterProperty : "extId",
                width : "90px"
            }, {
                label : "Name",
                template : "name",
                sortProperty : "name",
                filterProperty : "name"
            }, {
                label : "Long Name",
                template : "longName",
                sortProperty : "longName",
                filterProperty : "longName"
            }, {
                label : "Country",
                template : "country",
                sortProperty : "country",
                filterProperty : "country"
            }, {
                label : "Record",
                template : "previousGameStats",
                sortProperty : "previousGameStats",
                filterProperty : "previousGameStats"
            } ],
            visibleRowCount : 18,
            selectionMode : "None",
            editable : false
        });
        teamsTable.bindRows("/");

        teamsTable.setToolbar(new sap.ui.commons.Toolbar({
            items : [ new sap.ui.commons.Button({
                text : "Create New...",
                press : function() {
                    var name = prompt("Team Name", "Team1");
                    if (name && name != "") {
                        _this.getController().createTeam('{"name":"' + name + '"}');
                    }
                }
            }), new sap.ui.commons.ToolbarSeparator(), new sap.ui.commons.Label({
                text : "Test Mode:"
            }), new sap.ui.commons.Button({
                text : "Create Team1",
                press : function() {
                    _this.getController().createTeam('{"name":"Team1"}');
                }
            }), new sap.ui.commons.Button({
                text : "Create Team2",
                press : function() {
                    _this.getController().createTeam('{"name":"Team2"}');
                }
            }), new sap.ui.commons.Button({
                text : "Create Team3",
                press : function() {
                    _this.getController().createTeam('{"name":"Team3"}');
                }
            }), new sap.ui.commons.Button({
                text : "Create Team4",
                press : function() {
                    _this.getController().createTeam('{"name":"Team4"}');
                }
            }) ]
        }));

        return teamsTable;
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
