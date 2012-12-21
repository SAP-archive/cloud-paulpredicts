sap.ui.jsview("sap.pto.admin.Predictions", {

    getControllerName : function() {
        return "sap.pto.admin.Predictions";
    },

    createContent : function(controller) {
        var _this = this;
        var predictionsTable = new sap.ui.table.Table({
            columns : [ {
                label : "Id",
                template : "id",
                sortProperty : "id",
                filterProperty : "id",
                width : "60px"
            }, {
                label : "Match Date",
                template : "fixture/matchDate",
                sortProperty : "fixture/matchDate",
                filterProperty : "fixture/matchDate"
            }, {
                label : "Away Team",
                template : "fixture/awayTeam/name",
                sortProperty : "fixture/awayTeam/name",
                filterProperty : "fixture/awayTeam/name"
            }, {
                label : "Home Team",
                template : "fixture/homeTeam/name",
                sortProperty : "fixture/homeTeam/name",
                filterProperty : "fixture/homeTeam/name"
            }, {
                label : "User",
                template : "user/userName",
                sortProperty : "user/userName",
                filterProperty : "user/userName",
                width : "70px"
            }, {
                label : "Predicted Result",
                template : "result",
                sortProperty : "result",
                filterProperty : "result",
                width : "120px"
            }, {
                label : "Actual Result",
                template : "fixture/result",
                sortProperty : "fixture/result",
                filterProperty : "fixture/result",
                width : "120px"
            }, {
                label : "Date Created",
                template : "dateCreated",
                sortProperty : "dateCreated",
                filterProperty : "dateCreated"
            } ],
            visibleRowCount : 18,
            selectionMode : "None",
            editable : false
        });
        predictionsTable.bindRows("/");

        predictionsTable.setToolbar(new sap.ui.commons.Toolbar({
            items : [ new sap.ui.commons.Label({
                text : "Test Mode:"
            }), new sap.ui.commons.Button({
                text : 'Create Dummy Prediction',
                press : function() {
                    _this.getController().createDummyPrediction('{"result":"HOMEWIN"}');
                }
            }) ]
        }));

        return predictionsTable;
    }

});
