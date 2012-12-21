package com.sap.pto.util;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

import com.sap.pto.adapters.MailAdapter;
import com.sap.pto.adapters.PersistenceAdapter;
import com.sap.pto.adapters.UserManagementAdapter;
import com.sap.pto.dao.LeagueDAO;
import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.User;
import com.sap.security.auth.login.LoginContextFactory;

/**
 * Helper class to manage users.
 */
@SuppressWarnings("nls")
public class UserUtil {
    private static final ThreadLocal<User> threadUser = new ThreadLocal<User>();
    private static UserUtil instance = null;

    private UserUtil() {
    }

    public static UserUtil getInstance() {
        if (instance == null) {
            instance = new UserUtil();
        }
        return instance;
    }

    /**
     * @return The cached instance of the current user which might not be up-to-date if user data was changed but contains the correct id and user id.
     */
    public static User getLoggedInUser() {
        return threadUser.get();
    }

    public synchronized User getLoggedInUser(ServletRequest request) {
        EntityManager em = PersistenceAdapter.getEntityManager();
        boolean closeTransaction = PersistenceAdapter.beginTransactionOnDemand(em);

        User user = getLoggedInUser(request, em);

        PersistenceAdapter.commitTransactionOnDemand(em, closeTransaction);
        return user;
    }

    public synchronized User getLoggedInUser(ServletRequest request, EntityManager em) {
        String userName = UserManagementAdapter.getAuthenticatedName(request);

        if (userName == null) {
            return null;
        }

        User user = UserDAO.getUserByUserName(userName, em);
        if (user == null) {
            user = createFromUserPrincipal(request);
        }

        user.setLastLoginDate(new Date());

        // put into cache
        threadUser.set(user);

        return user;
    }

    public boolean isAdmin(ServletRequest request) {
        return ((HttpServletRequest) request).isUserInRole("admin");
    }

    public int deleteUserData(User user) {
        int recordCount = 0;

        recordCount += new LeagueDAO().deleteAll(user);

        return recordCount;
    }

    public static User getPaul() {
        return UserDAO.getUserByUserName(Consts.PAUL);
    }

    /**
     * Removes thread specific caches.
     */
    public static void cleanUp() {
        threadUser.remove();
    }

    private User createFromUserPrincipal(ServletRequest request) {
        if (request == null) {
            return null;
        }

        User user = UserManagementAdapter.getAuthenticatedUser(request);
        if (user != null) {
            UserDAO.saveNew(user);
            sendWelcomeMail(user);
        }

        return user;
    }

    public static void sendWelcomeMail(User user) {
        String template = MailAdapter.getTemplate("welcome.txt");
        template = template.replace("${username}", user.getUserName());
        String subject = "Welcome to Paul the Octopus!";
        MailAdapter.send(user.getEmail(), subject, template);
    }

    public static boolean logOut(HttpServletRequest request) {
        if (isShiroActive()) {
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                subject.logout();
            }
            HttpSession session = request.getSession();
            if (session != null) {
                session.invalidate();
            }
        } else {
            LoginContext loginContext = null;
            if (request.getRemoteUser() != null) {
                try {
                    loginContext = LoginContextFactory.createLoginContext();
                    loginContext.logout();
                    return true;
                } catch (LoginException e) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isShiroActive() {
        try {
            SecurityManager securityManager = SecurityUtils.getSecurityManager();
            return securityManager != null;
        } catch (Exception e) {
            // not available   
        }

        return false;
    }

}
