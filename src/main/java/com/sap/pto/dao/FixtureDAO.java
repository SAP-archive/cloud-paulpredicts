package com.sap.pto.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Team;

public class FixtureDAO extends BasicDAO<Fixture> {
    private static final Logger logger = LoggerFactory.getLogger(FixtureDAO.class);

    public static List<Fixture> getForTeam(Team team) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Fixture> query = em.createNamedQuery(Fixture.QUERY_BYTEAM, Fixture.class);
        query.setParameter("team", team);
        List<Fixture> fixtures = query.getResultList();

        return fixtures;
    }

    public static List<Fixture> getFuture() {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Fixture> query = em.createNamedQuery(Fixture.QUERY_FUTURE, Fixture.class);
        query.setParameter("date", DateTime.now().toDate());
        List<Fixture> fixtures = query.getResultList();

        return fixtures;
    }

    public static List<Fixture> getPast() {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Fixture> query = em.createNamedQuery(Fixture.QUERY_PAST, Fixture.class);
        query.setParameter("date", DateTime.now().toDate());
        List<Fixture> fixtures = query.getResultList();

        return fixtures;
    }

    public static Fixture getForDayAndTeams(DateTime matchDate, Team homeTeam, Team awayTeam) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        Fixture fixture = getForDayAndTeams(matchDate, homeTeam, awayTeam, em);

        return fixture;
    }

    public static Fixture getForDayAndTeams(DateTime matchDate, Team homeTeam, Team awayTeam, EntityManager em) {
        try {
            return (Fixture) em.createNamedQuery(Fixture.QUERY_BYDATEANDTEAMS).setParameter("homeTeam", homeTeam)
                    .setParameter("awayTeam", awayTeam).setParameter("minDate", matchDate.toDateMidnight().toDate())
                    .setParameter("maxDate", matchDate.toDateMidnight().plusDays(1).toDate()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("Fixture appears multiple times. This could mean that this use case has to be rethought.", e);
            throw e;
        }
    }

}
