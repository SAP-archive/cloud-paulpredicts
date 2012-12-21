package com.sap.pto.services;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.sap.pto.services.SystemService;
import com.sap.pto.testutil.PTOTest;

@SuppressWarnings("nls")
public class SystemServiceTest extends PTOTest {
    private SystemService systemService;

    @Before
    public void setup() throws Exception {
        prepareTest();

        systemService = new SystemService();
        systemService.request = requestMock;
    }

    @Test
    public void testGetSystemInfo() throws Exception {
        assertTrue(systemService.getSytemInfo().contains("currenttime"));
    }
}
