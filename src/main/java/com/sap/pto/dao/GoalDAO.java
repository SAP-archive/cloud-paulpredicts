package com.sap.pto.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Goal;

public class GoalDAO extends BasicDAO<Goal> {
    public static List<Goal> getForFixture(Fixture fixture) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Goal> query = em.createNamedQuery(Goal.QUERY_BYFIXTURE, Goal.class);
        query.setParameter("fixture", fixture);
        List<Goal> goals = query.getResultList();

        return goals;
    }

    public static int deleteForFixture(Fixture fixture) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);

        Query query = em.createNamedQuery(Goal.DELETE_BYFIXTURE);
        query.setParameter("fixture", fixture);

        int count = query.executeUpdate();
        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

        return count;
    }
}
