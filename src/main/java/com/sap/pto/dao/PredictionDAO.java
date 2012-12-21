package com.sap.pto.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Prediction;
import com.sap.pto.dao.entities.User;

public class PredictionDAO extends BasicDAO<Prediction> {
    private static final Logger logger = LoggerFactory.getLogger(PredictionDAO.class);

    public static List<Prediction> getForUser(User user) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Prediction> query = em.createNamedQuery(Prediction.QUERY_BYUSER, Prediction.class);
        query.setParameter("user", user);
        List<Prediction> predictions = query.getResultList();

        return predictions;
    }

    public static List<Prediction> getPastForUser(User user) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Prediction> query = em.createNamedQuery(Prediction.QUERY_PASTBYUSER, Prediction.class);
        query.setParameter("user", user);
        List<Prediction> predictions = query.getResultList();

        return predictions;
    }

    public static List<Prediction> getPastForFixture(Fixture fixture) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Prediction> query = em.createNamedQuery(Prediction.QUERY_PASTBYFIXTURE, Prediction.class);
        query.setParameter("fixture", fixture);
        List<Prediction> predictions = query.getResultList();

        return predictions;
    }

    public static List<Prediction> getForFixture(Fixture fixture) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Prediction> query = em.createNamedQuery(Prediction.QUERY_BYFIXTURE, Prediction.class);
        query.setParameter("fixture", fixture);
        List<Prediction> predictions = query.getResultList();

        return predictions;
    }

    public static List<Prediction> getForUsersAndFixtures(List<User> users, List<Fixture> fixtures) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Prediction> query = em.createNamedQuery(Prediction.QUERY_BYUSERSANDFIXTURES, Prediction.class);

        // JPA can only compare with IDs, not objects
        ArrayList<Long> fixtureIds = new ArrayList<Long>();
        for (Fixture fixture : fixtures) {
            fixtureIds.add(fixture.getId());
        }
        ArrayList<Long> userIds = new ArrayList<Long>();
        for (User user : users) {
            userIds.add(user.getId());
        }

        query.setParameter("users", userIds);
        query.setParameter("fixtures", fixtureIds);
        List<Prediction> predictions = query.getResultList();

        return predictions;
    }

    public static Prediction getForUserAndFixture(User user, Fixture fixture) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        Prediction prediction = getForUserAndFixture(user, fixture, em);

        return prediction;
    }

    public static Prediction getForUserAndFixture(User user, Fixture fixture, EntityManager em) {
        try {
            return (Prediction) em.createNamedQuery(Prediction.QUERY_BYUSERANDFIXTURE).setParameter("userId", user.getId())
                    .setParameter("fixtureId", fixture.getId()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("Prediction appears multiple times. This could mean severe database corruption. Fix immediately.", e);
            throw e;
        }
    }

}
