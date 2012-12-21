sap.ui.jsview("sap.pto.user.FutureFixtures", {

    getControllerName : function() {
        return "sap.pto.user.FutureFixtures";
    },

    createContent : function(controller) {
        var _this = this;
        var selectedFixtureModel = new sap.ui.model.json.JSONModel();

        var predictedResult = new sap.ui.commons.TextView();
        predictedResult.bindProperty("text", "userPrediction", function(prediction) {
            return (prediction != null) ? prediction.result : "NONE";
        });
        predictedResult.bindProperty("design", "userPrediction", function(prediction) {
            return (prediction != null) ? sap.ui.commons.TextViewDesign.Bold : sap.ui.commons.TextViewDesign.Default;
        });
        predictedResult.bindProperty("semanticColor", "userPrediction", function(prediction) {
            return (prediction != null) ? sap.ui.commons.TextViewColor.Critical : sap.ui.commons.TextViewColor.Default;
        });

        var predictButton = new sap.ui.commons.Button({
            press : function(oEvent) {
                var selectedRowContext = oEvent.oSource.getBindingContext();
                var fixture = _this.getModel().getProperty("", selectedRowContext);
                _this.openPredictionDialog(fixture);
            },
            width : "80px"
        });
        predictButton.bindProperty("text", "userPrediction", function(prediction) {
            var action = "Predict";
            if (prediction != null) {
                if (prediction.result != "NONE") {
                    action = "Change";
                }
            }
            return action;
        });

        var futureFixturesTable = new sap.ui.table.Table({
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
                template : predictButton,
                width : "100px"
            } ],
            selectionMode : sap.ui.table.SelectionMode.Single,
            visibleRowCount : 10,
            editable : false
        });
        futureFixturesTable.bindRows("/");

        futureFixturesTable.attachRowSelectionChange(function(oEvent) {
            var iRowIndex = oEvent.getParameter("rowIndex");
            var rowSelectedContext = futureFixturesTable.getContextByIndex(iRowIndex);
            var fixture = _this.getModel().getProperty("", rowSelectedContext);

            selectedFixtureModel.setData(fixture);
            details.setVisible(true);
        });

        futureFixturesTable.addStyleClass("basicbox contentbox block");

        var details = createDetailsPanel(selectedFixtureModel);
        details.addStyleClass("basicbox bigmargin contentbox block");

        var futureFixturesMatrix = new sap.ui.commons.layout.MatrixLayout({
            layoutFixed : true,
            columns : 1,
            width : "100%"
        });

        futureFixturesMatrix.createRow(futureFixturesTable);
        futureFixturesMatrix.createRow(details);
        return futureFixturesMatrix;
    },

    openPredictionDialog : function(fixture) {
        var _this = this;
        var textViewDesign = sap.ui.commons.TextViewDesign.H6;
        var predictionModel = new sap.ui.model.json.JSONModel();
        _this.getController().updatePredictionModel(predictionModel, fixture.id);

        var homeTeamNameLabel = new sap.ui.commons.TextView({
            text : "Home team:",
            design : textViewDesign
        });
        var versus = new sap.ui.commons.TextView({
            text : "vs.",
            design : textViewDesign
        });
        var awayTeamNameLabel = new sap.ui.commons.TextView({
            text : "Away team:",
            design : textViewDesign
        });

        var homeTeamName = new sap.ui.commons.TextView({
            text : fixture.homeTeam.name
        });
        var awayTeamName = new sap.ui.commons.TextView({
            text : fixture.awayTeam.name
        });

        var homeTeamFlag = new sap.ui.commons.Image({
            src : "public/img/flags/" + fixture.homeTeam.country + ".png"
        });
        var awayTeamFlag = new sap.ui.commons.Image({
            src : "public/img/flags/" + fixture.awayTeam.country + ".png"
        });

        var homeWinButton = new sap.ui.commons.Button({
            text : "Home win",
            width : "100px",
            height : "25px",
            press : function() {
                _this.getController().makePrediction("HOMEWIN", predictionModel, fixture);
            }
        });
        var awayWinButton = new sap.ui.commons.Button({
            text : "Away win",
            width : "100px",
            height : "25px",
            press : function() {
                _this.getController().makePrediction("AWAYWIN", predictionModel, fixture);
            }
        });
        var drawButton = new sap.ui.commons.Button({
            text : "Draw",
            width : "100px",
            height : "25px",
            press : function() {
                _this.getController().makePrediction("DRAW", predictionModel, fixture);
            }
        });

        homeWinButton.bindProperty("style", "/result", function(result) {
            if (result == "HOMEWIN") {
                return sap.ui.commons.ButtonStyle.Accept;
            } else {
                return sap.ui.commons.ButtonStyle.Default;
            }
        });
        awayWinButton.bindProperty("style", "/result", function(result) {
            if (result == "AWAYWIN") {
                return sap.ui.commons.ButtonStyle.Accept;
            } else {
                return sap.ui.commons.ButtonStyle.Default;
            }
        });
        drawButton.bindProperty("style", "/result", function(result) {
            if (result == "DRAW") {
                return sap.ui.commons.ButtonStyle.Accept;
            } else {
                return sap.ui.commons.ButtonStyle.Default;
            }
        });

        var predictionDialogLayout = new sap.ui.commons.layout.MatrixLayout();
        predictionDialogLayout.setModel(predictionModel);
        predictionDialogLayout.addStyleClass("matrixCenter");

        predictionDialogLayout.createRow(homeTeamNameLabel, versus, awayTeamNameLabel);
        predictionDialogLayout.createRow(homeTeamName, null, awayTeamName);
        predictionDialogLayout.createRow(homeTeamFlag, null, awayTeamFlag);
        // insert an empty line
        predictionDialogLayout.createRow(null);
        predictionDialogLayout.createRow(null, drawButton, null);
        predictionDialogLayout.createRow(homeWinButton, null, awayWinButton);

        var predictionDialog = new sap.ui.commons.Dialog({
            contentBorderDesign : sap.ui.commons.enums.BorderDesign.Box,
            modal : true,
            width : "30%"
        });

        predictionDialog.setTitle("Make your Prediction");
        predictionDialog.addContent(createHelpImg("Green button indicates your current prediction."));
        predictionDialog.addContent(predictionDialogLayout);

        predictionDialog.addButton(new sap.ui.commons.Button({
            text : "Close",
            width : "80px",
            height : "25px",
            press : function() {
                predictionDialog.close();
            }
        }));

        predictionDialog.open();
    }
});
