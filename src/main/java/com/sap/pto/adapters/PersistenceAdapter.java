package com.sap.pto.adapters;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used for connection with the database(Persistence Service).
 * The SAP NetWeaver Cloud Persistence Service provides persistence in a relational 
 * database for applications that are hosted on the SAP NetWeaver Cloud Platform.
 */
public class PersistenceAdapter {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceAdapter.class);
    public static boolean REUSE_ENTITYMANAGER = false;
    public static boolean USE_JNDI = true;

    private static EntityManagerFactory emf;
    private static ThreadLocal<PersistenceContext> threadLocal = new PersistenceContext();

    public static EntityManager getEntityManager() {
        if (USE_JNDI && emf == null) {
            try {
                InitialContext ctx = new InitialContext();
                DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
                String productName = ds.getConnection().getMetaData().getDatabaseProductName();
                logger.info("Database Product Name: " + productName);

                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);

                // only activate driver if on HANA
                if ("HDB".equalsIgnoreCase(productName)) {
                    logger.info("Activating HANA driver");
                    properties.put("eclipselink.target-database", "com.sap.persistence.platform.database.HDBPlatform");
                }
                emf = Persistence.createEntityManagerFactory("pto", properties);

                logger.info("EntityManagerFactory created through JNDI lookup.");
            } catch (NamingException e) {
                logger.error("JNDI DataSource could not be found.", e);
            } catch (SQLException e) {
                logger.error("Could not determine database type.");
            }
        }

        if (REUSE_ENTITYMANAGER) {
            return threadLocal.get().getEntityManager();
        } else {
            return emf.createEntityManager();
        }
    }

    public static void releaseEntityManager() {
        threadLocal.get().remove();
    }

    public static synchronized void setService(EntityManagerFactory emFactory) {
        emf = emFactory;
    }

    public static synchronized void unsetService(EntityManagerFactory emFactory) {
        emf = null;
    }

    public static synchronized EntityManagerFactory getService() {
        return emf;
    }

    public static boolean beginTransactionOnDemand(EntityManager em) {
        boolean closeTransaction = false;

        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
            closeTransaction = true;
        }

        return closeTransaction;
    }

    public static void commitTransactionOnDemand(EntityManager em, boolean closeTransaction) {
        if (closeTransaction && em.getTransaction().isActive()) {
            try {
                em.getTransaction().commit();
            } finally {
                if (!REUSE_ENTITYMANAGER && em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    public static void clearCache() {
        emf.getCache().evictAll();
    }

    private static class PersistenceContext extends ThreadLocal<PersistenceContext> {
        private static Logger logger = LoggerFactory.getLogger(PersistenceContext.class);
        private EntityManager em = null;

        @Override
        public PersistenceContext initialValue() {
            try {
                this.em = emf.createEntityManager();
            } catch (Exception e) {
                logger.warn(Thread.currentThread().getName() + " cannot obtain EntityManagerFactory yet.", e);
            }

            return this;
        }

        public EntityManager getEntityManager() {
            if (this.em == null) {
                this.em = emf.createEntityManager();
            }

            return this.em;
        }

        @Override
        public void remove() {
            if (this.em != null && this.em.isOpen()) {
                this.em.close();
            }
            this.em = null;
        }

    }

}
