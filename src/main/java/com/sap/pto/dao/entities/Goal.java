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

@Table(name = "Goals")
@NamedQueries({
        @NamedQuery(name = Goal.QUERY_BYFIXTURE, query = "SELECT g FROM Goal g WHERE g.fixture = :fixture order by g.eventMinute, g.period"),
        @NamedQuery(name = Goal.DELETE_BYFIXTURE, query = "DELETE FROM Goal g WHERE g.fixture = :fixture") })
@Entity
public class Goal extends BasicEntity {
    public static final String QUERY_BYFIXTURE = "findGoalsByFixture";
    public static final String DELETE_BYFIXTURE = "deleteGoalsByFixture";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Index
    @JsonIgnore
    private Fixture fixture;
    @Index
    private Player player;
    private int eventMinute;
    private String period;
    private String goalType;

    public Goal() {
        // just needed for JPA
    }

    public long getId() {
        return id;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getEventMinute() {
        return eventMinute;
    }

    public void setEventMinute(int eventMinute) {
        this.eventMinute = eventMinute;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + eventMinute;
        result = prime * result + ((fixture == null) ? 0 : fixture.hashCode());
        result = prime * result + ((goalType == null) ? 0 : goalType.hashCode());
        result = prime * result + ((period == null) ? 0 : period.hashCode());
        result = prime * result + ((player == null) ? 0 : player.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Goal other = (Goal) obj;
        if (eventMinute != other.eventMinute)
            return false;
        if (fixture == null) {
            if (other.fixture != null)
                return false;
        } else if (!fixture.equals(other.fixture))
            return false;
        if (goalType == null) {
            if (other.goalType != null)
                return false;
        } else if (!goalType.equals(other.goalType))
            return false;
        if (period == null) {
            if (other.period != null)
                return false;
        } else if (!period.equals(other.period))
            return false;
        if (player == null) {
            if (other.player != null)
                return false;
        } else if (!player.equals(other.player))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Goal [fixture=" + fixture + ", player=" + player + ", eventMinute=" + eventMinute + "]";
    }

}
