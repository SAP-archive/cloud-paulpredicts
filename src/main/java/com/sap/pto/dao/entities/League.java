package com.sap.pto.dao.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.persistence.annotations.Index;

@Table(name = "Leagues")
@NamedQueries({ @NamedQuery(name = League.QUERY_BYKEY, query = "SELECT l FROM League l WHERE l.accessKey = :key"),
        @NamedQuery(name = League.QUERY_BYOWNER, query = "SELECT l FROM League l WHERE l.owner = :owner"),
        @NamedQuery(name = League.QUERY_BYUSER, query = "SELECT distinct l FROM League l left join l.members lm WHERE lm.user = :user") })
@Entity
public class League extends BasicEntity {
    public static final String QUERY_BYKEY = "findLeagueByKey";
    public static final String QUERY_BYOWNER = "findLeagueByOwner";
    public static final String QUERY_BYUSER = "findLeagueByUser";

    public enum Visibility {
        PRIVATE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    private String name;
    @Index
    private String accessKey = RandomStringUtils.randomAlphanumeric(12).toLowerCase(Locale.ENGLISH); // TODO: check for collisions before storing
    @Index
    private User owner;
    private Visibility visibility;
    @Index
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "LEAGUE_ID")
    private List<LeagueMember> members = new ArrayList<LeagueMember>();

    public League() {
        // just needed for JPA
    }

    public League(User owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<LeagueMember> getMembers() {
        return members;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<User>();

        users.add(owner);
        for (LeagueMember member : members) {
            users.add(member.getUser());
        }

        return users;
    }

    public void setMembers(List<LeagueMember> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "League [name=" + name + ", owner=" + owner + "]";
    }

}
