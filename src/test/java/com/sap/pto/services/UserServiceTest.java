package com.sap.pto.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.sap.pto.dao.entities.User;
import com.sap.pto.services.UserService;
import com.sap.pto.testutil.PTOTest;

@SuppressWarnings("nls")
public class UserServiceTest extends PTOTest {
    private UserService userService;

    @Before
    public void setup() throws Exception {
        prepareTest();

        userService = new UserService();
        userService.request = requestMock;
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        User user = userService.getCurrentUser();
        assertNotNull(user);
        assertEquals(user.getUserName(), simpleUser.getUserName());
    }
}
