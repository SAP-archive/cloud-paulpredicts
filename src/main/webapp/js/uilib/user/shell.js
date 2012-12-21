shell = new sap.ui.ux3.Shell("userShell", {
    appTitle : "Paul Predicts",
    worksetItems : [ new sap.ui.ux3.NavigationItem({
        key : "home",
        id : "home",
        text : "You vs Paul"
    }), new sap.ui.ux3.NavigationItem({
        key : "allfixtures",
        text : "Fixtures",
        subItems : [ new sap.ui.ux3.NavigationItem({
            key : "futurefixtures",
            id : "futurefixtures",
            text : "Future Fixtures"
        }), new sap.ui.ux3.NavigationItem({
            key : "pastfixtures",
            id : "pastfixtures",
            text : "Past Fixtures"
        }) ]
    }) ],
    showFeederTool : false,
    showInspectorTool : false,
    showSearchTool : false,
    showLogoutButton : false,
    headerItems : [ new sap.ui.commons.TextView({
        text : "{/userName}"
    }) ]
});

function getContent(key) {
    if (key == "home") {
        return getView("sap.pto.user.Home");
    } else if (key == "pastfixtures") {
        return getView("sap.pto.user.PastFixtures");
    } else if (key == "futurefixtures") {
        return getView("sap.pto.user.FutureFixtures");
    }
}

shell.attachWorksetItemSelected(function(oEvent) {
    var key = oEvent.getParameter("key");
    setShellContent(key);
});
