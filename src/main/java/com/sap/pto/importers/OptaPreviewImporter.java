package com.sap.pto.importers;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.pto.dao.EditorialDAO;
import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.entities.Editorial;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.util.XmlUtilsExt;

public class OptaPreviewImporter extends BasicImporter {
    @Override
    public void importData(Document doc) {
        extractTeams(doc);

        NodeList matchList = XmlUtilsExt.getXPathResultSet(doc, "//MatchPreviews/Match");
        for (int i = 0; i < matchList.getLength(); i++) {
            Node node = matchList.item(i);

            Fixture fixture = extractFixture(node);
            extractEditorials(node, fixture);
        }
    }

    private Fixture extractFixture(Node node) {
        String extId = stripId(XmlUtilsExt.getXPathResultValue(node, "@Id"));

        Fixture fixture = new FixtureDAO().getByExtId(extId);
        if (fixture == null) {
            fixture = new Fixture(extId);
            FixtureDAO.saveNew(fixture);
        }

        return fixture;
    }

    private void extractEditorials(Node node, Fixture fixture) {
        EditorialDAO.deleteForFixture(fixture);

        NodeList playerList = XmlUtilsExt.getXPathResultSet(node, "./Editorial//Report");
        for (int i = 0; i < playerList.getLength(); i++) {
            Node reportNode = playerList.item(i);
            extractEditorial(reportNode, fixture);
        }
    }

    private Editorial extractEditorial(Node node, Fixture fixture) {
        String extId = stripId(XmlUtilsExt.getXPathResultValue(node, "@id"));
        String text = XmlUtilsExt.getXPathResultValue(node, ".");
        String language = XmlUtilsExt.getXPathResultValue(node, "../@lang");

        Editorial editorial = new Editorial(extId);
        editorial.setFixture(fixture);
        editorial.setLanguage(language);
        editorial.setText(text);
        EditorialDAO.saveNew(editorial);

        return editorial;
    }

    private void extractTeams(Node node) {
        NodeList teamList = XmlUtilsExt.getXPathResultSet(node, "//Team");
        for (int i = 0; i < teamList.getLength(); i++) {
            Node teamNode = teamList.item(i);
            extractTeam(teamNode);
        }
    }

    private Team extractTeam(Node node) {
        String extId = stripId(XmlUtilsExt.getXPathResultValue(node, "@uID"));
        String prevStats = XmlUtilsExt.getXPathResultValue(node, ".//FormText");

        Team team = new TeamDAO().getByExtId(extId);
        if (team == null) {
            // create new
            team = new Team(extId);
        }

        if (StringUtils.isNotBlank(prevStats)) {
            team.setPreviousGameStats(prevStats);
        }
        team = TeamDAO.save(team);

        return team;
    }
}
