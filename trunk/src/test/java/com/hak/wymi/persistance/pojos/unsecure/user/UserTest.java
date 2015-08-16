package com.hak.wymi.persistance.pojos.unsecure.user;

import junit.framework.TestCase;

public class UserTest extends TestCase {
    private User user;

    public void setUp() throws Exception {
        super.setUp();
        user = new User();
        user.setUserId(10);
    }

    public void tearDown() throws Exception {

    }

    public void testGetUserId() throws Exception {
        assertNotNull(user);
        assertEquals(user.getUserId(), new Integer(10));
    }
}