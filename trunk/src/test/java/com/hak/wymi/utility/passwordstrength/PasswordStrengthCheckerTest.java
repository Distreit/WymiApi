package com.hak.wymi.utility.passwordstrength;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.junit.Test;

public class PasswordStrengthCheckerTest extends TestCase {

    private int getStrength(String password) {
        return PasswordStrengthChecker.getStrengthResults(password).getFinalResult();
    }

    public void testPassword(String password, int expectedResult) {
        try {
            assertEquals("Password: " + password, expectedResult, getStrength(password));
        } catch (AssertionFailedError e) {
            throw e;
        }
    }

    @Test
    public void test() {
        testPassword("", 0);
        testPassword(null, 0);
        testPassword("000000", 0);
        testPassword("aaaaaaaaaaaa", 0);
        testPassword("aaaaaaaaaaaaaaaaaaaaaaaaaaa", 0);
        testPassword("111111", 0);
        testPassword("1234567", 4);
        testPassword("123456", 4);
        testPassword("12345678", 4);
        testPassword("123456789", 4);
        testPassword("12345", 4);
        testPassword("1234", 4);
        testPassword("123123", 7);
        testPassword("admin", 7);
        testPassword("ADMIN", 7);
        testPassword("1234567890", 7);
        testPassword("password", 8);
        testPassword("qwerty", 0);
        testPassword("password", 8);
        testPassword("letmein", 8);
        testPassword("monkey", 8);
        testPassword("shadow", 8);
        testPassword("princess", 8);
        testPassword("azerty", 2);
        testPassword("sunshine", 9);
        testPassword("photoshop", 9);
        testPassword("iloveyou", 9);
        testPassword("trustno1", 25);
        testPassword("password1", 26);
        testPassword("abc123", 32);
        testPassword("adobe123", 39);
        testPassword("12345678 ", 56);
        testPassword("ldflksdfmkl\"d", 37);
        testPassword("1234abcdsalgasdcba", 53);
        testPassword("lPfVZweLF3Wg", 82);
        testPassword("the qugaed oagasve dog", 87);
        testPassword("~!@#$%^&*(", 92);
        testPassword("#$%^&*(RFGjkbljkbnsd fasdkgh asgn agawv ", 100);
    }

    @Test
    public void testSymbols() {
        String symbols = "";
        for (int i = 32; i < 127; i += 1) {
            String character = String.valueOf(Character.toChars(i));
            if (!character.matches("[a-zA-Z0-9]")) {
                symbols += character;
            }
        }
        assertTrue(symbols.length() > 0);

        for (int i = 0, iLength = symbols.length(); i < iLength; i += 1) {
            char character = symbols.charAt(i);
            testPassword("frank" + character + "rizzo", 36);
            testPassword(character + "frankrizzo", 32);
            testPassword("frankrizzo" + character, 32);
        }
    }
}