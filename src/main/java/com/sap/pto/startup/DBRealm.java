package com.sap.pto.startup;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.User;
import com.sap.pto.util.SecurityUtil;
import com.sap.pto.util.configuration.ConfigUtil;

public class DBRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(DBRealm.class);

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        User user = getUser(username);

        // load roles
        String roleNames = user.getRoles();
        if (roleNames == null) {
            logger.error("No roles found for user [" + username + "]");
            throw new AccountException("No roles assigned");
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(new HashSet<String>(Arrays.asList(StringUtils.split(roleNames, ','))));

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
        String userName = userPassToken.getUsername();
        String password = new String(userPassToken.getPassword());
        String passwordHash = SecurityUtil.getPasswordHash(userName, password);
        User user = getUser(userName);

        // check credentials
        if (!user.getPasswordHash().equals(passwordHash)) {
            logger.error("Incorrect password");
            throw new CredentialsException();
        }
        if (ConfigUtil.getBooleanProperty("pto", "validateemail")) {
            if (!StringUtils.isEmpty(user.getEmailConfirmationKey())) {
                logger.error("E-Mail not yet confirmed");
                throw new AccountException();
            }
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userName, passwordHash, SecurityUtil.getSalt(userName), getName());

        return info;
    }

    private User getUser(String userName) {
        if (StringUtils.isEmpty(userName)) {
            logger.error("Username is empty.");
            throw new UnknownAccountException("Username is empty");
        }

        // fetch user from DB 
        User user = UserDAO.getUserByUserName(userName);
        if (user == null) {
            logger.error("No account found for user [" + userName + "]");
            throw new UnknownAccountException("Username not found");
        }

        return user;
    }

}
