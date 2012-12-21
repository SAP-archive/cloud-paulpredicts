function createDetailsPanel(singleFixtureModel) {
    var homeTeamInfoLayout = getTeamInfoLayout("homeTeam", singleFixtureModel);
    var awayTeamInfoLayout = getTeamInfoLayout("awayTeam", singleFixtureModel);

    var versus = new sap.ui.commons.TextView({
        text : "vs.",
        design : sap.ui.commons.TextViewDesign.H6
    });
    var score = new sap.ui.commons.TextView({
        text : "{/score}",
        design : sap.ui.commons.TextViewDesign.H1
    });
    var scoreLayout = new sap.ui.commons.layout.VerticalLayout({
        content : [ versus, score ]
    });

    var details = new sap.ui.commons.Panel({
        title : new sap.ui.commons.Title({
            text : "Details"
        }),
        visible : false,
        width : "98%",
        showCollapseIcon : false
    });

    var detailsMatrix = new sap.ui.commons.layout.MatrixLayout({
        layoutFixed : true,
        columns : 3,
        width : "100%",
        widths : [ "40%", "20%", "40%" ]
    });

    detailsMatrix.addStyleClass("matrixCenter");
    detailsMatrix.setModel(singleFixtureModel);
    detailsMatrix.createRow(homeTeamInfoLayout, matrixTop(scoreLayout), awayTeamInfoLayout);

    details.addContent(detailsMatrix);
    return details;
}

function getTeamInfoLayout(team, singleFixtureModel) {
    var teamNameDesign = sap.ui.commons.TextViewDesign.H3;
    var labelsDesign = sap.ui.commons.TextViewDesign.H6;

    var teamHeader = new sap.ui.commons.TextView({
        text : "{/" + team + "/name}",
        design : teamNameDesign
    });
    var teamFlag = new sap.ui.commons.Image();

    teamFlag.bindProperty("src", "/" + team + "/country", function(country) {
        return "public/img/flags/" + country + "@2x.png";
    });

    var teamNameLabel = new sap.ui.commons.TextView({
        design : labelsDesign
    });
    if (team == "homeTeam") {
        teamNameLabel.setText("Home team:");
    } else {
        teamNameLabel.setText("Away team:");
    }

    var teamCountryLabel = new sap.ui.commons.TextView({
        text : "Country:",
        design : labelsDesign
    });
    var teamRegionLabel = new sap.ui.commons.TextView({
        text : "Region:",
        design : labelsDesign
    });
    var teamStadiumnNameLabel = new sap.ui.commons.TextView({
        text : "Stadium name:",
        design : labelsDesign
    });
    var teamStadiumnCapacityLabel = new sap.ui.commons.TextView({
        text : "Stadium capacity:",
        design : labelsDesign
    });
    var teamStadiumnFoundingDateLabel = new sap.ui.commons.TextView({
        text : "Founding date:",
        design : labelsDesign
    });

    var teamName = new sap.ui.commons.TextView({
        text : "{/" + team + "/name}"
    });
    var teamCountry = new sap.ui.commons.TextView({
        text : "{/" + team + "/country}"
    });
    var teamRegion = new sap.ui.commons.TextView({
        text : "{/" + team + "/region}"
    });
    var teamStadiumnName = new sap.ui.commons.TextView({
        text : "{/" + team + "/stadiumName}"
    });
    var teamStadiumnCapacity = new sap.ui.commons.TextView({
        text : "{/" + team + "/stadiumCapacity}"
    });
    var teamStadiumnFoundingDate = new sap.ui.commons.TextView({
        text : "{/" + team + "/foundingDate}"
    });

    var teamInfoVerticalLayout = new sap.ui.commons.layout.VerticalLayout({
        content : [ teamHeader, teamFlag, teamNameLabel, teamName, teamCountryLabel, teamCountry, teamRegionLabel, teamRegion,
                teamStadiumnNameLabel, teamStadiumnName, teamStadiumnCapacityLabel, teamStadiumnCapacity,
                teamStadiumnFoundingDateLabel, teamStadiumnFoundingDate ]
    });

    return teamInfoVerticalLayout;
}
