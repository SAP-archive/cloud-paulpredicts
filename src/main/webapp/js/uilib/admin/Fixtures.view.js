sap.ui.jsview("sap.pto.admin.Fixtures", {

    getControllerName : function() {
        return "sap.pto.admin.Fixtures";
    },

    createContent : function(controller) {
        var _this = this;
        var setResultButton = new sap.ui.commons.Button({
            text : "Set Result"
        });
        setResultButton.attachPress(function(oEvent) {
            var selectedRowContext = oEvent.oSource.getBindingContext();
            var selectedFixture = _this.getModel().getProperty("", selectedRowContext);

            _this.openSetResultDialog(selectedFixture);
        });

        var fixturesTable = new sap.ui.table.Table({
            columns : [ {
                label : "Id",
                template : "id",
                sortProperty : "id",
                filterProperty : "id",
                width : "60px"
            }, {
                label : "External ID",
                template : "extId",
                sortProperty : "extId",
                filterProperty : "extId",
                width : "80px"
            }, {
                label : "Group",
                template : "groupName",
                sortProperty : "groupName",
                filterProperty : "groupName",
                width : "100px"
            }, {
                label : "Match Date",
                template : "matchDate",
                sortProperty : "matchDate",
                filterProperty : "matchDate"
            }, {
                label : "Home Team",
                template : "homeTeam/name",
                sortProperty : "homeTeam/name",
                filterProperty : "homeTeam/name"
            }, {
                label : "Away Team",
                template : "awayTeam/name",
                sortProperty : "awayTeam/name",
                filterProperty : "awayTeam/name"
            }, {
                label : "Result",
                template : "result",
                sortProperty : "result",
                filterProperty : "result",
                width : "100px"
            }, {
                label : "Score",
                template : "score",
                sortProperty : "score",
                filterProperty : "score",
                width : "100px"
            }, {
                label : "Action",
                template : setResultButton,
                width : "100px"
            } ],
            visibleRowCount : 18,
            selectionMode : "None",
            editable : false
        });
        fixturesTable.bindRows("/");

        fixturesTable.setToolbar(new sap.ui.commons.Toolbar({
            items : [ new sap.ui.commons.Button({
                text : "Create New...",
                press : function() {
                    _this.openCreateFixtureDialog();
                }
            }), new sap.ui.commons.ToolbarSeparator(), new sap.ui.commons.Label({
                text : "Test Mode:"
            }), new sap.ui.commons.Button({
                text : "Create Dummy-Fixture",
                press : function() {
                    _this.getController().createDummyFixture('{}');
                }
            }) ]
        }));

        return fixturesTable;
    },

    getSetResultPanel : function() {
        var layout = new sap.ui.commons.layout.MatrixLayout({
            layoutFixed : false,
            columns : 2
        });

        // ----------------------------
        this.result = new sap.ui.commons.DropdownBox({
            required : true,
            items : [ new sap.ui.core.ListItem({
                text : "DRAW"
            }), new sap.ui.core.ListItem({
                text : "HOMEWIN"
            }), new sap.ui.core.ListItem({
                text : "AWAYWIN"
            }) ]

        });
        addToFormMatrix(layout, "Result", this.result);

        // ----------------------------
        this.score = new sap.ui.commons.TextField({
            value : '0:0',
            required : true
        });
        addToFormMatrix(layout, "Score", this.score);

        return layout;
    },

    openSetResultDialog : function(fixture) {
        var _this = this;
        var dialog = new sap.ui.commons.Dialog({
            modal : true
        });
        dialog.setTitle("Set Result");

        dialog.addContent(this.getSetResultPanel());
        dialog.addButton(new sap.ui.commons.Button({
            text : "Save",
            press : function() {
                fixture.result = _this.result.getValue();
                fixture.score = _this.score.getValue();

                jQuery.ajax({
                    url : "/server/b/api/adminservice/fixtureresult",
                    type : 'POST',
                    contentType : 'application/json',
                    data : JSON.stringify(fixture),
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
    },

    getCreateFixturePanel : function() {
        var layout = new sap.ui.commons.layout.MatrixLayout({
            layoutFixed : false,
            columns : 2
        });

        // ----------------------------
        this.fixturedate = new sap.ui.commons.TextField({
            value : "2012-01-15T18:45:00 +0000",
            width : "15em"
        });
        addToFormMatrix(layout, "Date", this.fixturedate);

        // ----------------------------
        this.groupName = new sap.ui.commons.TextField({
            value : "Group A",
            width : "7em"
        });
        addToFormMatrix(layout, "Group", this.groupName);

        // ----------------------------
        this.hometeam = new sap.ui.commons.TextField({
            value : "42",
            width : "5em"
        });
        addToFormMatrix(layout, "Home Team ID", this.hometeam);

        // ----------------------------
        this.awayteam = new sap.ui.commons.TextField({
            value : "42",
            width : "5em"
        });
        addToFormMatrix(layout, "Away Team ID", this.awayteam);

        return layout;
    },

    openCreateFixtureDialog : function() {
        var _this = this;
        var dialog = new sap.ui.commons.Dialog({
            modal : true
        });
        dialog.setTitle("Create Fixture");

        dialog.addContent(this.getCreateFixturePanel());
        dialog.addButton(new sap.ui.commons.Button({
            text : "Save",
            press : function() {
                _this.getController().createFixture(
                        '{"matchDate":"' + _this.fixturedate.getValue() + '", "groupName":"' + _this.groupName.getValue()
                                + '", "awayTeam":{"id":' + _this.awayteam.getValue() + '}, "homeTeam":{"id":'
                                + _this.hometeam.getValue() + '}}');
                dialog.close();
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
