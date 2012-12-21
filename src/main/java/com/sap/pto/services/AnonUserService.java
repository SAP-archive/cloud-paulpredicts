package com.sap.pto.services;

import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.adapters.MailAdapter;
import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.User;
import com.sap.pto.util.Consts;
import com.sap.pto.util.SecurityUtil;
import com.sap.pto.util.configuration.ConfigUtil;

@Path("anonuserservice")
public class AnonUserService extends BasicService {
    private static Logger logger = LoggerFactory.getLogger(AnonUserService.class);

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(User user) {
        if (user == null || StringUtils.isEmpty(user.getUserName())) {
            throwBadRequest("Username must not be empty.");
        }
        if (!StringUtils.isAlphanumeric(user.getUserName())) {
            throwBadRequest("Only numbers and characters are allowed in the username.");
        }

        user.setUserName(sanitize(user.getUserName()));
        user.setEmail(sanitize(user.getEmail()));

        // data checks
        if (StringUtils.isEmpty(user.getPassword())) {
            throwBadRequest("Password must not be empty.");
        }
        if (user.getPassword().length() < Consts.PW_MINLENGTH) {
            throwBadRequest("Password must be at least " + Consts.PW_MINLENGTH + " characters in length.");
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            throwBadRequest("E-Mail must not be empty.");
        }
        if (!EmailValidator.getInstance().isValid(user.getEmail())) {
            throwBadRequest("E-Mail is invalid.");
        }

        // DB checks
        if (UserDAO.getUserByUserName(user.getUserName()) != null) {
            throwError(Status.CONFLICT, "Username is already taken.");
        }
        if (UserDAO.getUserByMail(user.getEmail()) != null) {
            throwError(Status.CONFLICT, "E-Mail is already registered.");
        }

        User newUser = new User(user.getUserName(), user.getEmail());
        newUser.setPasswordHash(SecurityUtil.getPasswordHash(newUser.getUserName(), user.getPassword()));
        UserDAO.saveNew(newUser);

        if (ConfigUtil.getBooleanProperty("pto", "validateemail")) {
            String emailConfirmationKey = newUser.getEmailConfirmationKey();
            String subject = ConfigUtil.getProperty("mail", "emailVerificationSubject");
            String userEmail = newUser.getEmail();

            String template = MailAdapter.getTemplate("emailVerification.txt");
            String userName = newUser.getUserName();
            template = template.replace("${username}", userName);
            template = template.replace("${link}", ConfigUtil.getTempProperty(Consts.SERVERNAME_PROPERTY_KEY)
                    + "/server/public/verifyEmail.jsp?username=" + userName + "&emailConfirmationKey=" + emailConfirmationKey);

            MailAdapter.send(userEmail, subject, template);
        }

        return Response.ok(newUser).build();
    }

    @POST
    @Path("/verifymail/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyMail(@PathParam("key") String key) {
        User user = UserDAO.getUserByMailKey(key);

        if (user == null) {
            throwBadRequest("The supplied mail challenge could not be found.");
        }

        user.setEmailConfirmationKey("");
        user = UserDAO.save(user);

        return RESPONSE_OK;
    }

    @GET
    @Path("/forgotpassword/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response triggerForgotPassword(@PathParam("username") String username) {
        User user = UserDAO.getUserByUserName(username);
        if (user == null) {
            throwBadRequest("The supplied username is invalid.");
        }

        // send password reset email to user
        String tempPassword = RandomStringUtils.randomAlphanumeric(8).toLowerCase(Locale.ENGLISH);
        user.setPasswordHash(SecurityUtil.getPasswordHash(username, tempPassword));
        user = UserDAO.save(user);

        String template = MailAdapter.getTemplate("passwordResetNotification.txt");
        if (template != null) {
            String subject = ConfigUtil.getProperty("mail", "passwordResetSubject");
            String userEmail = user.getEmail();

            template = template.replace("${username}", username);
            template = template.replace("${password}", tempPassword);
            MailAdapter.send(userEmail, subject, template);
        } else {
            logger.error("Mail configuration incomplete. Template not found.");
            return RESPONSE_BAD;
        }

        return RESPONSE_OK;
    }

}
