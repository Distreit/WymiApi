package com.hak.wymi.utility;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPChecker {
    public static int ipToInt(String ipAddress) throws UnknownHostException {
        Inet4Address a = (Inet4Address) InetAddress.getByName(ipAddress);
        byte[] b = a.getAddress();
        return ((b[0] & 0xFF) << 24) |
                ((b[1] & 0xFF) << 16) |
                ((b[2] & 0xFF) << 8) |
                ((b[3] & 0xFF) << 0);
    }

    public static boolean checkIp(String addressWithMask, String ipAddress) {
        try {
            String[] temp = addressWithMask.split("/");
            if (temp.length == 2) {
                int mask = Integer.valueOf(temp[1]);
                int ip = ipToInt(ipAddress);
                int subnet = ipToInt(temp[0]);
                mask = -1 << (32 - mask);

                boolean result = (subnet & mask) == (ip & mask);
                if (!result) {
                    System.out.println(String.format("%s not in %s", ipAddress, addressWithMask));
                }
                return result;
            }

            System.out.println("Wrong ip/mask");
            return false;
        } catch (UnknownHostException e) {
            System.out.println("Unknown host " + ipAddress);
            return false;
        }
    }
}
