package com.hak.wymi.persistance.pojos.unsecure.user;

import com.hak.wymi.persistance.pojos.user.User;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class UserTest extends TestCase {
    private User user;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        user = new User();
        user.setUserId(10);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetUserId() throws Exception {
        assertNotNull("User should not be null", user);
        assertEquals("User id should match the one just created should not be null", user.getUserId(), new Integer(10));
    }
}