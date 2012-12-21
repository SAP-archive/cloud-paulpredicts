var bgLoadCounter = 0;
var connectionErrorBox;

createConnectionErrorBox();
enableBackAndForwardButtons();

function enableBackAndForwardButtons() {
    jQuery(window).bind('hashchange', function(e) {
        var hashKey = location.hash;
        setShellContent(hashKey.replace("#", ""));
    });
}

function initPaul() {
    loadUserData();
    loadSystemInfo();
}

var loadImage = new sap.ui.commons.Image("dataLoading", {
    src : "public/img/loading.gif",
    decorative : true,
    alt : "loading data"
});

function createConnectionErrorBox() {
    connectionErrorBox = new sap.ui.commons.TextView({
        text : "Server connection lost, try reloading the page",
        visible : false
    });
    connectionErrorBox.addStyleClass("connectionError");
}

function boxify(element, width) {
    var container = new sap.ui.commons.layout.VerticalLayout();
    container.addContent(element);
    container.addStyleClass("basicbox bigmargin contentbox block");
    if (width != undefined) {
        container.setWidth(width);
    }

    return container;
}

function matrixCenter(element) {
    return new sap.ui.commons.layout.MatrixLayoutCell({
        content : element,
        hAlign : 'Center'
    });
}

function matrixRight(element) {
    return new sap.ui.commons.layout.MatrixLayoutCell({
        content : element,
        hAlign : 'Right'
    });
}

function matrixTop(element) {
    return new sap.ui.commons.layout.MatrixLayoutCell({
        content : element,
        vAlign : 'Top'
    });
}

function matrixColSpan(element, span) {
    return new sap.ui.commons.layout.MatrixLayoutCell({
        content : element,
        colSpan : span
    });
}

function createPanel(text, icon, collapsed) {
    var panel = new sap.ui.commons.Panel({
        showCollapseIcon : false,
        title : new sap.ui.commons.Title({
            text : text,
            icon : icon
        }),
        areaDesign : sap.ui.commons.enums.AreaDesign.Transparent,
        borderDesign : sap.ui.commons.enums.BorderDesign.None
    });

    if (collapsed != undefined) {
        panel.setShowCollapseIcon(true);
        panel.setCollapsed(collapsed);
    }

    return panel;
}

function loadUserData() {
    // load currently logged in user
    sap.ui.getCore().setModel(userDataModel);
    loadRestDataSync(userDataModel, "/server/b/api/userservice/user");
}

function loadSystemInfo() {
    // load infos about the system
    loadRestDataSync(sysinfoDataModel, "/server/b/api/systemservice/info");
    var version = new sap.ui.commons.TextView({
        text : "{/version}"
    });
    version.setModel(sysinfoDataModel);
    shell.addHeaderItem(version);

    // extract commonly needed properties
    setSystemInfoConstants(sysinfoDataModel);
}

function addToFormMatrix(layout, text, element, helpText) {
    var hlayout = new sap.ui.commons.layout.HorizontalLayout();
    hlayout.addContent(element);

    var label = new sap.ui.commons.Label();
    if (typeof text == "string") {
        if (text != "") {
            label.setText(text + ":");
            label.setLabelFor(element);
            label.addStyleClass("rightpadding");
        }
    } else {
        label = text;
    }

    if (helpText != undefined && helpText != "") {
        var img = createHelpImg(helpText);
        hlayout.addContent(img);
    }

    layout.createRow(matrixTop(label), hlayout);

    return label;
}

function createHelpImg(helpText) {
    var tooltip = new sap.ui.commons.RichTooltip({
        text : helpText,
        title : "Quick Help"
    });
    var img = new sap.ui.commons.Image({
        src : "public/img/icons/help.png",
        tooltip : tooltip
    });
    img.addStyleClass("leftpaddingsmall helpcursor");

    return img;
}

function incLoadCounter() {
    if (bgLoadCounter == 0) {
        try {
            shell.insertHeaderItem(loadImage, 0);
        } catch (e) {
        }
    }
    bgLoadCounter += 1;
}

function decLoadCounter() {
    bgLoadCounter -= 1;
    if (bgLoadCounter == 0) {
        try {
            shell.removeHeaderItem(loadImage);
        } catch (e) {
        }
    }
}

function loadRestDataSync(model, url, merge) {
    if (merge == undefined) {
        merge = false;
    }

    incLoadCounter();
    connectionErrorBox.setVisible(false);
    model.attachRequestFailed(function(data) {
        alert(data.responseText);
        connectionErrorBox.setVisible(true);
    });
    model.loadData(url, null, false);
    decLoadCounter();
}

function setInitialShellContent(defaultIfNone) {
    var pageHash = defaultIfNone;
    if (window.location.hash) {
        pageHash = window.location.hash.substring(1);
    }
    setShellContent(pageHash);
    shell.placeAt("shellArea");
}

function setShellContent(key) {
    if (key == "") {
        key = "home";
    }
    var newContent = getContent(key);

    var oldState = connectionErrorBox.getVisible();
    shell.setContent(newContent, true);

    createConnectionErrorBox();
    connectionErrorBox.setVisible(oldState);
    shell.addContent(connectionErrorBox);

    try {
        shell.setSelectedWorksetItem(key);
    } catch (e) {
    }

    window.location.hash = jQuery.sap.encodeURL(key);
}

function getUserPicture(pathToUser) {
    var img = new sap.ui.commons.Image({
        src : pathToUser + "imageLink}",
        width : "16px",
        height : "16px"
    });

    img.attachBrowserEvent("error", function(data) {
        try {
            this.setSrc("public/img/icons/user.png");
        } catch (e) {
        }
    });

    return img;
}

function getUserDetailsTextRow(label, text) {
    var c = sap.ui.commons.layout;
    var oLabel = new sap.ui.commons.TextView({
        text : label
    });
    var oTextView = new sap.ui.commons.TextView({
        text : text
    });
    var oLeftCell = new c.MatrixLayoutCell({
        hAlign : c.HAlign.End,
        vAlign : c.VAlign.Top,
        content : [ oLabel ]
    });
    oLeftCell.addStyleClass("qvlabel");
    var oRightCell = new c.MatrixLayoutCell({
        hAlign : c.HAlign.Begin,
        vAlign : c.VAlign.Top,
        content : oTextView
    });
    oRightCell.addStyleClass("qvvalue");

    var oRow = new c.MatrixLayoutRow({
        cells : [ oLeftCell, oRightCell ]
    });

    return oRow;
}

function getUserDetailsLinkRow(label, text, href) {
    var c = sap.ui.commons.layout;
    var oLabel = new sap.ui.commons.TextView({
        text : label
    });
    var oLink = new sap.ui.commons.Link({
        text : text,
        href : href
    });
    var oLeftCell = new c.MatrixLayoutCell({
        hAlign : c.HAlign.End,
        vAlign : c.VAlign.Top,
        content : [ oLabel ]
    });
    oLeftCell.addStyleClass("qvlabel");
    var oRightCell = new c.MatrixLayoutCell({
        hAlign : c.HAlign.Begin,
        vAlign : c.VAlign.Top,
        content : oLink
    });
    oRightCell.addStyleClass("qvvalue");

    var oRow = new c.MatrixLayoutRow({
        cells : [ oLeftCell, oRightCell ]
    });

    return oRow;
}

function getUserDetails(pathToUser) {
    var oContent = new sap.ui.commons.layout.MatrixLayout({
        layoutFixed : true,
        widths : [ "45%", "140px" ]
    });
    oContent.addRow(getUserDetailsTextRow("Name:", pathToUser + "fullName}"));
    oContent.addRow(getUserDetailsLinkRow("E-Mail:", pathToUser + "email}", pathToUser + "mailtoLink}"));
    if (ADMINMODE) {
        oContent.addRow(getUserDetailsTextRow("Internal DB ID:", pathToUser + "id}"));
    }

    return oContent;
}

function getUserField(initialPathToUser, textWidth) {
    // pathToUser is the prefix to the user object, e.g. "buddyUser/" if you
    // start on a buddy object or "" if you already have an user object
    var pathToUser = "{" + initialPathToUser;
    var text;

    if (textWidth != undefined) {
        text = new sap.ui.commons.TextView({
            text : pathToUser + "userName}",
            width : textWidth
        });
    } else {
        text = new sap.ui.commons.Link({
            text : pathToUser + "userName}",
            href : pathToUser + "profileLink}",
            target : "_new"
        });
    }

    var userField = stashHorizontally(getUserPicture(pathToUser), text);

    var userQuickThing = new sap.ui.ux3.QuickView({
        type : "User Info",
        firstTitle : pathToUser + "userName}",
        firstTitleHref : pathToUser + "profileLink}",
        secondTitle : pathToUser + "department}",
        icon : pathToUser + "imageLink}",
        content : getUserDetails(pathToUser),
        showActionBar : false,
        width : "350px"
    });

    userField.setTooltip(userQuickThing);

    return userField;
}

function stashHorizontally(allowWrapping) {
    if (arguments.length == 0) {
        return null;
    }

    var layout = new sap.ui.commons.layout.HorizontalLayout();
    var startIdx = 0;
    if (typeof allowWrapping == "boolean") {
        layout.setAllowWrapping(allowWrapping);
        startIdx = 1;
    }
    var el = null;

    for ( var i = startIdx; i < arguments.length; i++) {
        el = arguments[i];
        if (typeof el == "string") {
            el = new sap.ui.commons.TextView({
                text : arguments[i],
                wrapping : false
            });
        }
        if (!el.hasStyleClass("rightmargin")) {
            el.addStyleClass("rightmarginsmall");
        }
        layout.addContent(el);
    }

    if (arguments.length == 1) {
        // no need to return in a layout
        return el;
    } else {
        return layout;
    }
}

function openOverlay(content) {
    var vlayout = new sap.ui.commons.layout.VerticalLayout();
    vlayout.addContent(content);

    var overlay = new sap.ui.ux3.OverlayContainer({
        openButtonVisible : false
    });
    vlayout.addStyleClass("hpadding");
    overlay.addContent(vlayout);
    overlay.open();
}
