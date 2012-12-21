shell = new sap.ui.ux3.Shell("myShell", {
    appTitle : "Paul Predicts - Administration",
    worksetItems : [ new sap.ui.ux3.NavigationItem({
        key : "home",
        id : "home",
        text : "Home"
    }), new sap.ui.ux3.NavigationItem({
        key : "users",
        id : "users",
        text : "Users"
    }), new sap.ui.ux3.NavigationItem({
        key : "teams",
        id : "teams",
        text : "Teams"
    }), new sap.ui.ux3.NavigationItem({
        key : "fixtures",
        id : "fixtures",
        text : "Fixtures"
    }), new sap.ui.ux3.NavigationItem({
        key : "predictions",
        id : "predictions",
        text : "Predictions"
    }), new sap.ui.ux3.NavigationItem({
        key : "jobs",
        id : "jobs",
        text : "Jobs"
    }) ],
    showFeederTool : false,
    showInspectorTool : false,
    showSearchTool : false,
    showLogoutButton : true,
    headerItems : [ new sap.ui.commons.TextView({
        text : "{/userName}"
    }) ],
    logout : function() {
        jQuery.post("/server/b/api/userservice/logout");
        window.location = "/server";
    }
});

function getContent(key) {
    if (key == "home") {
        return getView("sap.pto.admin.Overview");
    } else if (key == "users") {
        return getView("sap.pto.admin.Users");
    } else if (key == "teams") {
        return getView("sap.pto.admin.Teams");
    } else if (key == "fixtures") {
        return getView("sap.pto.admin.Fixtures");
    } else if (key == "predictions") {
        return getView("sap.pto.admin.Predictions");
    } else if (key == "jobs") {
        return getView("sap.pto.admin.Jobs");
    } else if (key == "database") {
        return getView("sap.pto.admin.Database");
    }
}

shell.attachWorksetItemSelected(function(oEvent) {
    var key = oEvent.getParameter("key");
    setShellContent(key);
});
