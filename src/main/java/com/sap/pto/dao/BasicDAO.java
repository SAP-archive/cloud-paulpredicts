package com.sap.pto.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.Fixture;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.User;

@SuppressWarnings("unchecked")
public class BasicDAO<T> {
    private static final Logger logger = LoggerFactory.getLogger(BasicDAO.class);

    public enum Resolution {
        DAILY, WEEKLY
    }

    public static <T> T save(T t) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);

        T merge = em.merge(t);

        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

        return merge;
    }

    public static <T> T save(T t, EntityManager em) {
        T merge = em.merge(t);

        return merge;
    }

    /**
     * An error safe version of {@link #save} which does not return the saved
     * entity but rather if it could be saved. This is useful when handling
     * items with unique keys.
     */
    public static <T> boolean trySave(T t) {
        try {
            save(t);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <T> T saveNew(T t) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);

        em.persist(t);

        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

        return t;
    }

    public static <T> void saveNew(T t, EntityManager em) {
        em.persist(t);
    }

    public T getById(long id) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        T t = getById(id, em);

        return t;
    }

    public T getById(long id, EntityManager em) {
        T t = null;

        try {
            Query query = em.createQuery("select u from " + getTableName() + " u where u.id = :id");
            query.setParameter("id", id);
            t = (T) query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("Could not retrieve entity " + id + " from table " + getTableName() + ".");
        }

        return t;
    }

    public T getByExtId(String extId) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        T t = getByExtId(extId, em);

        return t;
    }

    public T getByExtId(String extId, EntityManager em) {
        T t = null;

        try {
            Query query = em.createQuery("select u from " + getTableName() + " u where u.extId = :extId");
            query.setParameter("extId", extId);
            t = (T) query.getSingleResult();
        } catch (NoResultException e) {
            // nothing needs to be done
        }

        return t;
    }

    public List<T> getByTeam(Team team) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        List<T> t = getByTeam(team, em);

        return t;
    }

    public List<T> getByTeam(Team team, EntityManager em) {
        Query query = em.createQuery("select u from " + getTableName() + " u where u.team = :team");
        query.setParameter("team", team);
        List<T> result = query.getResultList();

        return result;
    }

    public List<T> getByFixture(Fixture fixture) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        List<T> t = getByFixture(fixture, em);

        return t;
    }

    public List<T> getByFixture(Fixture fixture, EntityManager em) {
        Query query = em.createQuery("select u from " + getTableName() + " u where u.fixture = :fixture");
        query.setParameter("fixture", fixture);
        List<T> result = query.getResultList();

        return result;
    }

    public long getCount() {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<Long> query = em.createQuery("select count(u) from " + getTableName() + " u", Long.class);
        Long result = query.getSingleResult();

        return result;
    }

    public Map<DateTime, Long> getCountByPeriod(boolean cumulative, Resolution resolution) {
        return getCountByPeriod(cumulative, "", "", resolution);
    }

    public Map<DateTime, Long> getCountByPeriod(boolean cumulative, String whereClause, String dateClause, Resolution resolution) {
        if (StringUtils.isBlank(dateClause)) {
            dateClause = "u.dateCreated";
        }
        String queryString = "select " + dateClause + ", count(u) from " + getTableName() + " u";
        if (StringUtils.isNotBlank(whereClause)) {
            queryString += " where " + whereClause;
        }
        queryString += " group by " + dateClause + " order by " + dateClause;

        EntityManager em = PersistenceAdapter.getEntityManager();
        TypedQuery<Object[]> query = em.createQuery(queryString, Object[].class);

        List<Object[]> result = query.getResultList();

        return calculateCountByPeriod(cumulative, resolution, result);
    }

    protected Map<DateTime, Long> calculateCountByPeriod(boolean cumulative, Resolution resolution, List<Object[]> result) {
        Map<DateTime, Long> data = new TreeMap<DateTime, Long>();

        for (int i = 0; i < result.size(); i++) {
            Object[] arr = result.get(i);
            Date date = (Date) arr[0];
            DateTime day = new DateTime(date).withTime(0, 0, 0, 0);

            // do resolution handling
            if (resolution == Resolution.WEEKLY) {
                while (day.getDayOfWeek() != DateTimeConstants.SUNDAY) {
                    day = day.plusDays(1);
                }
            }

            Long count = (Long) arr[1];
            if (data.containsKey(day)) {
                data.put(day, data.get(day) + count);
            } else {
                data.put(day, count);
            }
        }

        if (cumulative) {
            DateTime[] dates = data.keySet().toArray(new DateTime[data.keySet().size()]);
            for (int i = 1; i < dates.length; i++) {
                data.put(dates[i], data.get(dates[i]) + data.get(dates[i - 1]));
            }
        }

        return data;
    }

    public long getStatsNumber(String whereClause) {
        return getStatsNumber(null, whereClause);
    }

    public long getStatsNumber(String selectClause, String whereClause) {
        if (StringUtils.isBlank(selectClause)) {
            selectClause = "u";
        }
        String queryString = "select count(" + selectClause + ") from " + getTableName() + " u";
        if (StringUtils.isNotBlank(whereClause)) {
            queryString += " where " + whereClause;
        }

        EntityManager em = PersistenceAdapter.getEntityManager();
        Query query = em.createQuery(queryString);

        return (Long) query.getSingleResult();
    }

    public List<T> getAll() {
        EntityManager em = PersistenceAdapter.getEntityManager();

        Query query = em.createQuery("select u from " + getTableName() + " u");
        List<T> result = query.getResultList();

        return result;
    }

    public List<T> getAll(String orderBy) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        Query query = em.createQuery("select u from " + getTableName() + " u order by u." + orderBy);
        List<T> result = query.getResultList();

        return result;
    }

    public List<T> getAllDesc() {
        EntityManager em = PersistenceAdapter.getEntityManager();

        Query query = em.createQuery("select u from " + getTableName() + " u order by u.dateModified desc");
        List<T> result = query.getResultList();

        return result;
    }

    /**
     * Deletes all items for the entity.
     */
    public int deleteAll() {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);

        Query query = em.createQuery("delete from " + getTableName());
        int count = query.executeUpdate();

        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

        return count;
    }

    /**
     * Deletes all items for the entity after a certain date.
     */
    public int deleteAll(DateTime afterDate) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);

        Query query = em.createQuery("delete from " + getTableName() + " where dateCreated > :date");
        query.setParameter("date", afterDate.toDate());

        try {
            int count = query.executeUpdate();
            PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

            return count;
        } catch (Exception e) {
            // logger.warn("Could not delete all records for table '" +
            // getTableName() + "' after '" + afterDate + "'");
            return -1;
        }
    }

    /**
     * Deletes all entries owned by the user.
     */
    public int deleteAll(User user) {
        // this is just a special case of deleting by custom column
        return deleteFromAll(user, "user");
    }

    /**
     * Deletes all entries where the user is referenced in the specified column.
     */
    public int deleteFromAll(User user, String refUserColumn) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);

        Query query = em.createQuery("delete from " + getTableName() + " e where e." + refUserColumn + " = :user");
        query.setParameter("user", user);
        int count = query.executeUpdate();

        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

        return count;
    }

    /**
     * Sets the user reference of all entries where the user is referenced to
     * null.
     */
    public int detachFromAll(User user, String refUserColumn) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);

        Query query = em.createQuery("update " + getTableName() + " e set e." + refUserColumn + " = null where e." + refUserColumn
                + " = :user");
        query.setParameter("user", user);
        int count = query.executeUpdate();

        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

        return count;
    }

    /**
     * Deletes an item through it's primary key id.
     */
    public boolean deleteById(long id) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);
        boolean deleted = false;

        Query query = em.createQuery("delete from " + getTableName() + " t where t.id = :id");
        query.setParameter("id", id);
        deleted = query.executeUpdate() > 0;

        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

        return deleted;
    }

    /**
     * Deletes an item through it's primary key id double checking with the user
     * to avoid misuse.
     */
    public boolean deleteById(long id, User user) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);
        boolean deleted = false;

        Query query = em.createQuery("delete from " + getTableName() + " t where t.id = :id and t.user = :user");
        query.setParameter("id", id);
        query.setParameter("user", user);
        deleted = query.executeUpdate() > 0;

        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);

        return deleted;
    }

    private Type getActualType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) genericSuperclass;
        Type type = pt.getActualTypeArguments()[0];

        return type;
    }

    private String getTableName() {
        String[] arr = StringUtils.split(getActualType().toString(), ".");

        return arr[arr.length - 1];
    }
}
