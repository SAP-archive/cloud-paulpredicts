package com.sap.pto.dao.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

import com.sap.pto.services.util.JsonIgnore;

@Table(name = "PlayerStatistics")
@NamedQueries({
        @NamedQuery(name = PlayerStat.QUERY_BYPLAYERANDKEY, query = "SELECT ps FROM PlayerStat ps WHERE ps.player = :player and ps.statKey = :key"),
        @NamedQuery(name = PlayerStat.QUERY_BYPLAYER, query = "SELECT ps FROM PlayerStat ps WHERE ps.player = :player") })
@Entity
public class PlayerStat extends BasicEntity {
    public static final String QUERY_BYPLAYERANDKEY = "findPlayerStatByPlayerAndKey";
    public static final String QUERY_BYPLAYER = "findPlayerStatByPlayer";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Index
    @JsonIgnore
    private Player player;
    @Index
    private String statKey;
    private String statValue;

    public PlayerStat() {
        // just needed for JPA
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getStatKey() {
        return statKey;
    }

    public void setStatKey(String statKey) {
        this.statKey = statKey;
    }

    public String getStatValue() {
        return statValue;
    }

    public void setStatValue(String statValue) {
        this.statValue = statValue;
    }

    public long getId() {
        return id;
    }

}
