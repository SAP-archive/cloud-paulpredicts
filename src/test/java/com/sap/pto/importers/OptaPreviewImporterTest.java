package com.sap.pto.importers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.EditorialDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.entities.Editorial;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.testutil.PTOTest;
import com.sap.pto.util.MiscUtils;

@SuppressWarnings("nls")
public class OptaPreviewImporterTest extends PTOTest {
    @Before
    public void setup() throws Exception {
        prepareTest();
    }

    @Test
    public void testImportEditorial() throws Exception {
        importMatches();
        importEditorial();

        List<Editorial> editorials = new EditorialDAO().getAll();
        assertEquals(8, editorials.size());

        Team team = new TeamDAO().getByExtId("178");
        assertEquals("WDWLDW", team.getPreviousGameStats());
    }

    private void importEditorial() throws URISyntaxException, IOException {
        URI results = MiscUtils.getResource("opta/opta-476857-matchpreview.xml").toURI();
        new OptaPreviewImporter().importData(results);
    }
}
