package com.sap.pto.dao;

import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.User;

public class UserDAO extends BasicDAO<User> {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public static User getUserByUserName(String userName) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        User user = getUserByUserName(userName, em);

        return user;
    }

    public static User getUserByUserName(String userName, EntityManager em) {
        try {
            return (User) em.createNamedQuery(User.QUERY_BYUSERNAME)
                    .setParameter("userName", StringUtils.lowerCase(userName, Locale.ENGLISH)).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("User appears multiple times. This could mean severe database corruption. Fix immediately.", e);
            throw e;
        }
    }

    public static User getUserByMailKey(String key) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        User user = getUserByMailKey(key, em);

        return user;
    }

    public static User getUserByMailKey(String key, EntityManager em) {
        try {
            return (User) em.createNamedQuery(User.QUERY_BYMAILKEY).setParameter("key", StringUtils.lowerCase(key, Locale.ENGLISH))
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("Mail key appears multiple times. This could mean severe database corruption. Fix immediately.", e);
            throw e;
        }
    }

    public static User getUserByMail(String email) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        User user = getUserByMail(email, em);

        return user;
    }

    public static User getUserByMail(String email, EntityManager em) {
        if (email == null) {
            return null;
        }

        String cleanMail = email;
        if (cleanMail.contains("<")) {
            cleanMail = email.substring(email.indexOf("<") + 1, email.indexOf(">"));
        }

        try {
            return (User) em.createNamedQuery(User.QUERY_BYMAIL).setParameter("email", StringUtils.lowerCase(cleanMail, Locale.ENGLISH))
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("User appears multiple times. This could mean severe database corruption. Fix immediately.", e);
            throw e;
        }
    }

}
