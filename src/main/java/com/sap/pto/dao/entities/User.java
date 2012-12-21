package com.sap.pto.dao.entities;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.Index;

import com.sap.pto.services.util.JsonIgnore;

@Table(name = "Users")
@NamedQueries({ @NamedQuery(name = User.QUERY_BYUSERNAME, query = "SELECT u FROM User u WHERE lower(u.userName) = lower(:userName)"),
        @NamedQuery(name = User.QUERY_BYMAILKEY, query = "SELECT u FROM User u WHERE u.emailConfirmationKey = :key"),
        @NamedQuery(name = User.QUERY_BYMAIL, query = "SELECT u FROM User u WHERE u.email = :email") })
@Entity
public class User extends BasicEntity {
    public static final String QUERY_BYUSERNAME = "findUserByUserName";
    public static final String QUERY_BYMAIL = "findUserByMail";
    public static final String QUERY_BYMAILKEY = "findUserByMailKey";

    public static final String DEFAULT_IMAGELINK = "public/img/user@2x.png";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @Column(unique = true, nullable = false)
    @Index
    private String userName;
    @Column(unique = true, nullable = false)
    @Index
    @JsonIgnore
    private String email;
    private String fullName;
    private String imageLink = DEFAULT_IMAGELINK;
    @Index
    @JsonIgnore
    private String emailConfirmationKey = RandomStringUtils.randomAlphanumeric(12).toLowerCase(Locale.ENGLISH); // TODO: check for collisions before storing
    @JsonIgnore
    private String roles = "user"; // comma separated list
    @Transient
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String passwordHash;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date lastLoginDate;

    public User() {
        // just needed for JPA
    }

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public User(String userName, String email, String passwordHash, String roles) {
        this(userName, email);
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.emailConfirmationKey = "";
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailConfirmationKey() {
        return emailConfirmationKey;
    }

    public void setEmailConfirmationKey(String emailConfirmationKey) {
        this.emailConfirmationKey = emailConfirmationKey;
    }

    public boolean isEmailConfirmed() {
        return StringUtils.isBlank(emailConfirmationKey);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((emailConfirmationKey == null) ? 0 : emailConfirmationKey.hashCode());
        result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
        result = prime * result + ((imageLink == null) ? 0 : imageLink.hashCode());
        result = prime * result + ((passwordHash == null) ? 0 : passwordHash.hashCode());
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
        User other = (User) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (emailConfirmationKey == null) {
            if (other.emailConfirmationKey != null)
                return false;
        } else if (!emailConfirmationKey.equals(other.emailConfirmationKey))
            return false;
        if (fullName == null) {
            if (other.fullName != null)
                return false;
        } else if (!fullName.equals(other.fullName))
            return false;
        if (imageLink == null) {
            if (other.imageLink != null)
                return false;
        } else if (!imageLink.equals(other.imageLink))
            return false;
        if (passwordHash == null) {
            if (other.passwordHash != null)
                return false;
        } else if (!passwordHash.equals(other.passwordHash))
            return false;
        if (roles == null) {
            if (other.roles != null)
                return false;
        } else if (!roles.equals(other.roles))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "User [userName=" + userName + "]";
    }

}
