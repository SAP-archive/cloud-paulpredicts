sap.ui.jsview("sap.pto.admin.Overview", {

    getControllerName : function() {
        return "sap.pto.admin.Overview";
    },

    createContent : function(controller) {
        var vLayout = new sap.ui.commons.layout.VerticalLayout();
        var text = new sap.ui.commons.TextView({
            text : "This front-end is for administrators only. NOT FOR PUBLIC USE."
        });
        vLayout.addContent(text);

        text = new sap.ui.commons.TextView({
            text : "User Source: " + (SHIROMODE ? "Shiro" : "SAP ID Service")
        });
        text.addStyleClass("vpadding");
        vLayout.addContent(text);

        var clearCache = new sap.ui.commons.Button({
            text : "Clear DB Cache",
            press : function(oEvent) {
                jQuery.post("/server/b/api/adminservice/clearcache", "", function(data) {
                    sap.ui.commons.MessageBox.alert("Cache has been cleared.");
                });
            }
        });
        var testMail = new sap.ui.commons.Button({
            text : "Send Testmail",
            press : function(oEvent) {
                jQuery.post("/server/b/api/adminservice/testmail", "", function(data) {
                    sap.ui.commons.MessageBox.alert("Mail has been sent.");
                });
            }
        });
        vLayout.addContent(stashHorizontally(clearCache, testMail));

        return vLayout;
    }

});
