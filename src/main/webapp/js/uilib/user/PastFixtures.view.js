sap.ui.jsview("sap.pto.user.PastFixtures", {

    getControllerName : function() {
        return "sap.pto.user.PastFixtures";
    },

    createContent : function(controller) {
        var _this = this;
        var selectedFixtureModel = new sap.ui.model.json.JSONModel();

        var predictedResult = new sap.ui.commons.TextView();
        predictedResult.bindProperty("text", "predictions", function(predictions) {
            var userPrediction = "NONE";
            if (predictions != null) {
                for ( var i = 0; i < predictions.length; i++) {
                    if (predictions[i].user.userName == sap.ui.getCore().getModel().getProperty("/userName")) {
                        userPrediction = predictions[i].result;
                        break;
                    }
                }
            }
            return userPrediction;
        });

        var pastFixturesTable = new sap.ui.table.Table({
            columns : [ {
                label : "Match Date",
                template : new sap.ui.commons.TextView({
                    text : {
                        path : "matchDate",
                        formatter : ttDateFormatter
                    }
                }),
                sortProperty : "matchDate",
                filterProperty : "matchDate",
                width : "100px"
            }, {
                label : "Home Team",
                template : "homeTeam/name",
                sortProperty : "homeTeam/name",
                filterProperty : "homeTeam/name",
                width : "100px"
            }, {
                label : "Away Team",
                template : "awayTeam/name",
                sortProperty : "awayTeam/name",
                filterProperty : "awayTeam/name",
                width : "100px"
            }, {
                label : "Result",
                template : "result",
                width : "100px"
            }, {
                label : "Predicted Result",
                template : predictedResult,
                width : "100px"
            }, {
                label : "Score",
                template : "score",
                sortProperty : "score",
                filterProperty : "score",
                width : "100px"
            } ],
            selectionMode : sap.ui.table.SelectionMode.Single,
            visibleRowCount : 10,
            editable : false
        });
        pastFixturesTable.bindRows("/");

        pastFixturesTable.attachRowSelectionChange(function(oEvent) {
            var iRowIndex = oEvent.getParameter("rowIndex");
            var rowSelectedContext = pastFixturesTable.getContextByIndex(iRowIndex);
            var fixture = _this.getModel().getProperty("", rowSelectedContext);

            selectedFixtureModel.setData(fixture);
            details.setVisible(true);
        });

        pastFixturesTable.addStyleClass("basicbox contentbox block");

        var details = createDetailsPanel(selectedFixtureModel);
        details.addStyleClass("basicbox bigmargin contentbox block");

        var pastFixturesMatrix = new sap.ui.commons.layout.MatrixLayout({
            layoutFixed : true,
            columns : 1,
            width : "100%"
        });

        pastFixturesMatrix.createRow(pastFixturesTable);
        pastFixturesMatrix.createRow(details);

        return pastFixturesMatrix;
    }
});
