package com.sap.pto.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.User;
import com.sap.pto.services.AnonUserService;
import com.sap.pto.testutil.PTOTest;

@SuppressWarnings("nls")
public class AnonUserServiceTest extends PTOTest {
    private AnonUserService anonUserService;
    private static final String TEST_USER_NAME = "testüöä";
    private static final String TEST_USER_EMAIL = "test@test.com";

    @Before
    public void setup() throws Exception {
        prepareTest();
        anonUserService = new AnonUserService();
        anonUserService.request = requestMock;
        anonUserService.context = contextMock;
    }

    @Test
    public void testRegisterUser() throws Exception {
        User user = new User(TEST_USER_NAME, TEST_USER_EMAIL);
        user.setPassword("1234");

        Response response = anonUserService.registerUser(user);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        User newUser = UserDAO.getUserByUserName(TEST_USER_NAME);
        assertNotNull(newUser);
        assertEquals(newUser.getUserName(), user.getUserName());
        assertFalse(StringUtils.isEmpty(newUser.getPasswordHash()));
    }

    @Test(expected = WebApplicationException.class)
    public void testRegisterUserTwice() throws Exception {
        User user = new User(TEST_USER_NAME, TEST_USER_EMAIL);
        user.setPassword("1234");

        Response response = anonUserService.registerUser(user);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        response = anonUserService.registerUser(user);
    }

    @Test(expected = WebApplicationException.class)
    public void testRegisterUserWithoutValidEmail() throws Exception {
        User user = new User(TEST_USER_NAME, "novalidmail");
        user.setPassword("1234");

        anonUserService.registerUser(user);
    }

    @Test(expected = WebApplicationException.class)
    public void testRegisterUserWithInvalidPW() throws Exception {
        User user = new User(TEST_USER_NAME, TEST_USER_EMAIL);
        user.setPassword("123");

        anonUserService.registerUser(user);
    }

    @Test
    public void testRegisterUserInvalidPassword() throws Exception {
        User user = new User(TEST_USER_EMAIL, TEST_USER_EMAIL);

        try {
            anonUserService.registerUser(user);
            fail("should have thrown an exception");
        } catch (WebApplicationException e) {
            User newUser = UserDAO.getUserByUserName(TEST_USER_NAME);
            assertNull(newUser);
        }
    }

    @Test
    public void testTriggerForgotPassword() throws Exception {
        String passwordOld = simpleUser.getPasswordHash();

        // call forget password method to reset user's password
        anonUserService.triggerForgotPassword(simpleUser.getUserName());

        // get user1 data as a new user2 to retrieve user1's new password
        User user2 = UserDAO.getUserByUserName(simpleUser.getUserName());
        String passwordNew = user2.getPasswordHash();

        // Check user1's old and new password are different
        assertFalse(passwordNew.equals(passwordOld));
    }
}
