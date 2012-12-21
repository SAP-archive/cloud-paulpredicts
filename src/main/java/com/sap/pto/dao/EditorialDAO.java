package com.sap.pto.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.Editorial;
import com.sap.pto.dao.entities.Fixture;

public class EditorialDAO extends BasicDAO<Editorial> {
    public static int deleteForFixture(Fixture fixture) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);

        Query query = em.createNamedQuery(Editorial.DELETE_BYFIXTURE);
        query.setParameter("fixture", fixture);

        int count = query.executeUpdate();
        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

        return count;
    }

}
