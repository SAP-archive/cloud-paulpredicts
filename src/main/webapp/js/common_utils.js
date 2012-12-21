var NO_LIMIT = Math.pow(2, 32) - 1;
var ADMINMODE = false;
var SHIROMODE = false;
var userDataModel = new sap.ui.model.json.JSONModel();
var sysinfoDataModel = new sap.ui.model.json.JSONModel();

function updateSystemInfo() {
    sysinfoDataModel.loadData("/server/b/api/systemservice/info", null, true);
}

function setSystemInfoConstants(sysinfoDataModel) {
    // extract commonly needed properties
    ADMINMODE = sysinfoDataModel.getProperty("/adminmode");
    SHIROMODE = sysinfoDataModel.getProperty("/shiromode");
}

function getView(name) {
    return sap.ui.view({
        type : sap.ui.core.mvc.ViewType.JS,
        viewName : name
    });
}

function ttAsDate(value) {
    if (value == undefined) {
        return value;
    }
    var year = value.substring(0, 4);
    var month = value.substring(5, 7) - 1;
    var day = value.substring(8, 10);
    var date = new Date(year, month, day);
    date.setHours(value.substring(11, 13));
    date.setMinutes(value.substring(14, 16));
    return date;
}

function ttDateFormatter(value) {
    if (value == undefined) {
        return value;
    }
    var date = ttAsDate(value);
    return date.toString("MMM dd HH:mm (ddd)");
}