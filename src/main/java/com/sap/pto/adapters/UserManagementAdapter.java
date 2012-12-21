package com.sap.pto.adapters;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.dao.entities.User;
import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.UserProvider;

/**
 * This class contains methods for retrieving attributes of the already
 * authenticated user, managed by the ID Service.
 * The SAP NetWeaver Cloud ID Service provides user management
 * for applications that are hosted on the SAP NetWeaver Cloud Platform.
 */
public class UserManagementAdapter {
    private static final Logger logger = LoggerFactory.getLogger(UserManagementAdapter.class);

    /**
     * Update user fields with properties that are gotten from the User
     * principal, provided by SAP NetWeaver cloud ID Service.
     * 
     */
    public static User getAuthenticatedUser(ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null.");
        }
        User user = null;
        try {
            user = new User();
            com.sap.security.um.user.User idmUser = getIdmUser(request);
            if (idmUser != null) {
                String userName = idmUser.getName();
                user.setUserName(userName);

                String firstName = idmUser.getAttribute("firstname");
                String lastName = idmUser.getAttribute("lastname");
                user.setFullName(firstName + " " + lastName);

                String email = idmUser.getAttribute("email");
                user.setEmail(email);
            }
        } catch (PersistenceException e) {
            logger.error("Could not get the user provider.", e);
        } catch (UnsupportedUserAttributeException e) {
            logger.error("Could not get user's attribute.", e);
        }
        return user;
    }

    private static com.sap.security.um.user.User getIdmUser(ServletRequest request) throws PersistenceException {
        UserProvider provider = UserManagementAccessor.getUserProvider();
        com.sap.security.um.user.User idmUser = null;
        Principal principal = ((HttpServletRequest) request).getUserPrincipal();
        if (principal != null) {
            // Read the currently logged in user from the user storage
            idmUser = provider.getUser(principal.getName());
        }
        return idmUser;
    }

    public static String getAuthenticatedName(ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null.");
        }
        String userName = null;

        if (request instanceof HttpServletRequest) {
            Principal userPrincipal = ((HttpServletRequest) request).getUserPrincipal();
            if (userPrincipal != null) {
                userName = StringUtils.lowerCase(userPrincipal.getName(), Locale.ENGLISH);
            }
        }

        return userName;
    }

}
