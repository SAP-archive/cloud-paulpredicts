package com.sap.pto.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.Config;

@SuppressWarnings("nls")
public class ConfigDAO extends BasicDAO<Config> {
    public static List<Config> getByGroup(String group) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Config> query = em.createNamedQuery(Config.QUERY_BYGROUP, Config.class);
        query.setParameter("group", group);
        List<Config> configs = query.getResultList();

        return configs;
    }
}
