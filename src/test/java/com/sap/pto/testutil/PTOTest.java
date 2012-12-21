package com.sap.pto.testutil;

import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_DRIVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_PASSWORD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_USER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_SERVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.TimeZone;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.logging.SessionLog;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.BeforeClass;

import com.google.gson.Gson;
import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.CompetitionDAO;
import com.sap.pto.dao.ConfigDAO;
import com.sap.pto.dao.EditorialDAO;
import com.sap.pto.dao.FixtureDAO;
import com.sap.pto.dao.GoalDAO;
import com.sap.pto.dao.LeagueDAO;
import com.sap.pto.dao.LeagueMemberDAO;
import com.sap.pto.dao.PlayerDAO;
import com.sap.pto.dao.PlayerStatDAO;
import com.sap.pto.dao.PredictionDAO;
import com.sap.pto.dao.SeasonDAO;
import com.sap.pto.dao.TeamDAO;
import com.sap.pto.dao.TeamOfficialDAO;
import com.sap.pto.dao.TeamStatDAO;
import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.User;
import com.sap.pto.importers.OptaMatchImporter;
import com.sap.pto.importers.OptaTeamImporter;
import com.sap.pto.startup.AppInitializer;
import com.sap.pto.util.MiscUtils;

/**
 * Utility class providing commonly used test helper functions and mocks.
 *
 */
@SuppressWarnings({ "nls" })
public abstract class PTOTest {
    protected User simpleUser = new User("testuser", "testuser@test.com");
    protected HttpServletRequestMock requestMock = new HttpServletRequestMock(simpleUser);
    protected ServletContext contextMock = new ServletContextMock();
    protected Gson gson = new Gson();

    private static final String DB_PASSWORD = "";
    private static final String DB_USER = "";
    private static final String DB_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    private static final String DB_CONN = "jdbc:derby:memory:PtoDB";
    private static final String DB_DDL = "drop-and-create-tables";

    protected Team team1;
    protected Team team2;
    protected Team team3;
    protected Team team4;
    protected DateTime testDay;
    protected Fixture fixture1_2;
    protected Fixture fixture3_4;

    public void prepareTest() throws Exception {
        deleteAllDBEntries();
        AppInitializer.initDefaultUsers();
        UserDAO.saveNew(simpleUser);

        System.getProperties().setProperty("net.fortuna.ical4j.timezone.update.enabled", "false");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateTimeZone.setDefault(DateTimeZone.UTC);

        AppInitializer.initConfiguration();

        testDay = new DateTime(2012, 8, 17, 10, 0, 0);
    }

    protected void setupSampleData() {
        // setup basic sample data
        team1 = new Team("1");
        team2 = new Team("2");
        team3 = new Team("3");
        team4 = new Team("4");
        TeamDAO.saveNew(team1);
        TeamDAO.saveNew(team2);
        TeamDAO.saveNew(team3);
        TeamDAO.saveNew(team4);

        fixture1_2 = new Fixture(testDay.withHourOfDay(20).toDate(), team1, team2);
        fixture3_4 = new Fixture(testDay.withHourOfDay(21).toDate(), team3, team4);
        FixtureDAO.saveNew(fixture1_2);
        FixtureDAO.saveNew(fixture3_4);
    }

    @BeforeClass
    public static void initDBConnection() {
        if (PersistenceAdapter.getService() == null) {
            createEMF(";create=true");
        }
    }

    private void deleteAllDBEntries() {
        // this order is intentionally since there are some dependencies which are not managed by JPA currently
        new EditorialDAO().deleteAll();
        new PredictionDAO().deleteAll();
        new TeamOfficialDAO().deleteAll();
        new GoalDAO().deleteAll();
        new PlayerStatDAO().deleteAll();
        new PlayerDAO().deleteAll();
        new FixtureDAO().deleteAll();
        new TeamStatDAO().deleteAll();
        new LeagueMemberDAO().deleteAll();
        new LeagueDAO().deleteAll();
        new TeamDAO().deleteAll();
        new CompetitionDAO().deleteAll();
        new SeasonDAO().deleteAll();
        new UserDAO().deleteAll();
        new ConfigDAO().deleteAll();
    }

    private static void createEMF(String command) {
        Properties props = getJPAProperties();
        props.put(JDBC_URL, props.get(JDBC_URL) + command);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pto", props);
        PersistenceAdapter.setService(emf);
        PersistenceAdapter.getEntityManager();
    }

    private static Properties getJPAProperties() {
        Properties properties = new Properties();
        properties.put(TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.name());
        properties.put(JDBC_DRIVER, DB_DRIVER);
        properties.put(JDBC_URL, DB_CONN);
        properties.put(JDBC_USER, DB_USER);
        properties.put(JDBC_PASSWORD, DB_PASSWORD);
        properties.put(LOGGING_LEVEL, SessionLog.INFO_LABEL);
        properties.put(TARGET_SERVER, TargetServer.None);
        properties.put(DDL_GENERATION, DB_DDL);

        return properties;
    }

    protected void assertResponseOK(Response response) {
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    protected void importTeams() throws URISyntaxException, IOException {
        URI results = MiscUtils.getResource("opta/srml-5-2012-squads.xml").toURI();
        new OptaTeamImporter().importData(results);
    }

    protected void importMatches() throws URISyntaxException, IOException {
        URI results = MiscUtils.getResource("opta/srml-5-2012-results.xml").toURI();
        new OptaMatchImporter().importData(results);
    }

}