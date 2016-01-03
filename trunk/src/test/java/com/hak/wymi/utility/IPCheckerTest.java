package com.hak.wymi.utility;

import junit.framework.TestCase;
import org.junit.Test;

public class IPCheckerTest extends TestCase {

    @Test
    public void testIpToInt() throws Exception {
        assertEquals("0A0101FF", String.format("%08X", IPChecker.ipToInt("10.1.1.255")));
        assertEquals("FFFFFFFF", String.format("%08X", IPChecker.ipToInt("255.255.255.255")));
        assertEquals("63636363", String.format("%08X", IPChecker.ipToInt("99.99.99.99")));
    }

    @Test
    public void testCheckIp() throws Exception {
        assertTrue(IPChecker.checkIp("10.0.0.1/1", "10.0.0.1"));
        assertTrue(IPChecker.checkIp("10.0.0.1/1", "127.0.0.1"));
        assertTrue(!IPChecker.checkIp("10.0.0.1/1", "128.0.0.1"));
        assertTrue(IPChecker.checkIp("10.0.0.1/24", "10.0.0.10"));

        //Coinbase IPs
        assertTrue(IPChecker.checkIp("54.175.255.192/27", "54.175.255.196"));
        assertTrue(IPChecker.checkIp("54.175.255.192/27", "54.175.255.221"));
    }
}