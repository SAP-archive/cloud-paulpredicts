package com.sap.pto.dao;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.League;
import com.sap.pto.dao.entities.User;

public class LeagueDAO extends BasicDAO<League> {
    private static final Logger logger = LoggerFactory.getLogger(LeagueDAO.class);

    public static League getLeagueByKey(String key) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        League result = getLeagueByKey(key, em);

        return result;
    }

    public static League getLeagueByKey(String key, EntityManager em) {
        try {
            return (League) em.createNamedQuery(League.QUERY_BYKEY).setParameter("key", StringUtils.lowerCase(key, Locale.ENGLISH))
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("League appears multiple times. This could mean severe database corruption. Fix immediately.", e);
            throw e;
        }
    }

    public static List<League> getForOwner(User owner) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<League> query = em.createNamedQuery(League.QUERY_BYOWNER, League.class);
        query.setParameter("owner", owner);
        List<League> leagues = query.getResultList();

        return leagues;
    }

    public static List<League> getForUser(User user) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<League> query = em.createNamedQuery(League.QUERY_BYUSER, League.class);
        query.setParameter("user", user);
        List<League> leagues = query.getResultList();

        return leagues;
    }

}
