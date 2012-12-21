package com.sap.pto.importers;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sap.pto.dao.CompetitionDAO;
import com.sap.pto.dao.SeasonDAO;
import com.sap.pto.dao.entities.Competition;
import com.sap.pto.dao.entities.Season;
import com.sap.pto.util.XmlUtilsExt;

public abstract class BasicImporter {
    public void importData(URI uri) throws IOException {
        Document doc = XmlUtilsExt.loadXMLDoc(uri.toString());
        importData(doc);
    }

    public void importData(String content) throws IOException {
        Document doc = XmlUtilsExt.parseXMLString(content);
        importData(doc);
    }

    public void importData(Document doc) {
    }

    Season extractSeason(Document doc) {
        return extractSeason(doc, "SoccerDocument");
    }

    Season extractSeason(Document doc, String nodeName) {
        String seasonId = stripId(XmlUtilsExt.getXPathResultValue(doc, "//" + nodeName + "/@season_id"));
        String seasonName = XmlUtilsExt.getXPathResultValue(doc, "//" + nodeName + "/@season_name");

        Season season = new SeasonDAO().getByExtId(seasonId);
        if (season == null) {
            season = new Season(seasonId);
        }
        season.setName(seasonName);
        season = SeasonDAO.save(season);

        return season;
    }

    Competition extractCompetition(Document doc) {
        return extractCompetition(doc, "SoccerDocument");
    }

    Competition extractCompetition(Document doc, String nodeName) {
        String competitionId = stripId(XmlUtilsExt.getXPathResultValue(doc, "//" + nodeName + "/@competition_id"));
        String competitionCode = XmlUtilsExt.getXPathResultValue(doc, "//" + nodeName + "/@competition_code");
        String competitionName = XmlUtilsExt.getXPathResultValue(doc, "//" + nodeName + "/@competition_name");

        Competition competition = new CompetitionDAO().getByExtId(competitionId);
        if (competition == null) {
            competition = new Competition(competitionId);
        }
        if (StringUtils.isNotBlank(competitionCode)) {
            competition.setCode(competitionCode);
        }
        competition.setName(competitionName);
        competition = CompetitionDAO.save(competition);

        return competition;
    }

    /**
     * Removes leading non-numeric character due to format inconsistencies
     */
    String stripId(String extId) {
        while (!StringUtils.isBlank(extId) && !StringUtils.isNumeric(extId.substring(0, 1))) {
            extId = extId.substring(1);
        }

        return extId;
    }

    String getAttribute(NamedNodeMap attributes, String name) {
        Node value = attributes.getNamedItem(name);

        return (value == null) ? null : value.getTextContent();
    }

}