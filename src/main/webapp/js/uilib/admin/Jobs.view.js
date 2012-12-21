sap.ui.jsview("sap.pto.admin.Jobs", {

    getControllerName : function() {
        return "sap.pto.admin.Jobs";
    },

    createContent : function(controller) {
        var _this = this;
        var startJobButton = new sap.ui.commons.Button({
            text : "Start"
        });
        var pauseJobButton = new sap.ui.commons.Button({
            text : "Pause"
        });
        var resumeJobButton = new sap.ui.commons.Button({
            text : "Resume"
        });
        var jobsTable = new sap.ui.table.Table({
            columns : [ {
                label : "Name",
                template : "name",
                width : "150px",
                sortProperty : "name",
                filterProperty : "name"
            }, {
                label : "State",
                template : "value",
                sortProperty : "value",
                filterProperty : "value"
            }, {
                label : "Action",
                width : "200px",
                template : stashHorizontally(startJobButton, pauseJobButton, resumeJobButton)
            } ],
            visibleRowCount : 11,
            selectionMode : "None",
            editable : false
        });

        startJobButton.attachPress(function(oEvent) {
            // get the binding context of the first selected row
            var selectedRowContext = oEvent.oSource.getBindingContext();
            var selectedId = _this.getModel().getProperty("name", selectedRowContext);

            _this.getController().startJob(selectedId);
        });
        pauseJobButton.attachPress(function(oEvent) {
            // get the binding context of the first selected row
            var selectedRowContext = oEvent.oSource.getBindingContext();
            var selectedId = _this.getModel().getProperty("name", selectedRowContext);

            _this.getController().pauseJob(selectedId);
        });
        resumeJobButton.attachPress(function(oEvent) {
            // get the binding context of the first selected row
            var selectedRowContext = oEvent.oSource.getBindingContext();
            var selectedId = _this.getModel().getProperty("name", selectedRowContext);

            _this.getController().resumeJob(selectedId);
        });

        jobsTable.bindRows("/");

        return jobsTable;
    }

});
