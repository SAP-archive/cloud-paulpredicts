package com.sap.pto.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.Team;
import com.sap.pto.dao.entities.TeamStat;

public class TeamStatDAO extends BasicDAO<TeamStat> {
    private static final Logger logger = LoggerFactory.getLogger(TeamStatDAO.class);

    public static TeamStat getForTeam(Team team, String key) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TeamStat user = getForTeam(team, key, em);

        return user;
    }

    public static TeamStat getForTeam(Team team, String key, EntityManager em) {
        try {
            return (TeamStat) em.createNamedQuery(TeamStat.QUERY_BYTEAMANDKEY).setParameter("team", team).setParameter("key", key)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("TeamStat appears multiple times. This could mean database corruption. Fix immediately.", e);
            throw e;
        }
    }

    public static List<TeamStat> getForTeam(Team team) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<TeamStat> query = em.createNamedQuery(TeamStat.QUERY_BYTEAM, TeamStat.class);
        query.setParameter("team", team);
        List<TeamStat> teamStats = query.getResultList();

        return teamStats;
    }

}
