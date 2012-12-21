sap.ui.jsview("sap.pto.admin.Leagues", {

    getControllerName : function() {
        return "sap.pto.admin.Leagues";
    },

    createContent : function(controller) {
        var _this = this;
        var leaguesTable = new sap.ui.table.Table({
            columns : [ {
                label : "Id",
                template : "id",
                sortProperty : "id",
                filterProperty : "id",
                width : "50px"
            }, {
                label : "Name",
                template : "name",
                sortProperty : "name",
                filterProperty : "name"
            }, {
                label : "Key",
                template : "accessKey",
                sortProperty : "accessKey",
                filterProperty : "accessKey"
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
        leaguesTable.bindRows("/");

        if (SHIROMODE) {
            leaguesTable.setToolbar(new sap.ui.commons.Toolbar({
                items : [ new sap.ui.commons.Label({
                    text : "Test Mode:"
                }), new sap.ui.commons.Button({
                    text : "Create John's league",
                    press : function() {
                        _this.getController().createDummyLeague('{"name":"John\'s league"}');
                    }
                }), new sap.ui.commons.Button({
                    text : "Create Robert's league",
                    press : function() {
                        _this.getController().createDummyLeague('{"name":"Robert\'s league"}');
                    }
                }), new sap.ui.commons.Button({
                    text : "Create Sean's league",
                    press : function() {
                        _this.getController().createDummyLeague('{"name":"Sean\'s league"}');
                    }
                }) ]
            }));
        }

        return leaguesTable;
    }

});
