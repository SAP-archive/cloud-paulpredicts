package com.sap.pto.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.dao.entities.Player;
import com.sap.pto.dao.entities.PlayerStat;

public class PlayerStatDAO extends BasicDAO<PlayerStat> {
    private static final Logger logger = LoggerFactory.getLogger(PlayerStatDAO.class);

    public static PlayerStat getForPlayer(Player player, String key) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        PlayerStat user = getForPlayer(player, key, em);

        return user;
    }

    public static PlayerStat getForPlayer(Player player, String key, EntityManager em) {
        try {
            return (PlayerStat) em.createNamedQuery(PlayerStat.QUERY_BYPLAYERANDKEY).setParameter("player", player)
                    .setParameter("key", key).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("PlayerStat appears multiple times. This could mean database corruption. Fix immediately.", e);
            throw e;
        }
    }

    public static List<PlayerStat> getForPlayer(Player player) {
        EntityManager em = PersistenceAdapter.getEntityManager();

        TypedQuery<PlayerStat> query = em.createNamedQuery(PlayerStat.QUERY_BYPLAYER, PlayerStat.class);
        query.setParameter("player", player);
        List<PlayerStat> playerStats = query.getResultList();

        return playerStats;
    }

}
