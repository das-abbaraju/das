package com.picsauditing.security;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;


public class KeyCodeTest {
    private static final int TEST_SIZE = 100;

    /*
     * Call generateRandom 100 times and make sure we get back a different
     * result each time
     */
    @Test
    public void testGenerateRandom() throws Exception {
        KeyCode encodedKey = new KeyCode(25);
        Set<String> keys = new HashSet<String>();
        for (int i = 0; i < TEST_SIZE; i++) {
            encodedKey.generateRandom();
            String key = encodedKey.getKey();
            keys.add(key);
            assertEquals(25, key.length());
        }
        assertEquals(TEST_SIZE, keys.size());
    }

    /*
     * Call generateRandomComplex 100 times and make sure we get back a
     * different result each time
     */
    @Test
    public void testGenerateRandomComplex() throws Exception {
        KeyCode encodedKey = new KeyCode(50);
        encodedKey.setRadix(62);
        encodedKey.setMaxTries(20);
        Set<String> keys = new HashSet<String>();
        for (int i = 0; i < TEST_SIZE; i++) {
            encodedKey.generateRandomComplex();
            String key = encodedKey.getKey();
            keys.add(key);
            assertEquals(50, key.length());
        }
        assertEquals(TEST_SIZE, keys.size());
    }

    /*
     * A key with a max length of 10 characters cannot possibly contain 12
     * unique characters, so make sure it throws an exception.
     */
    @Test(expected = SecurityException.class)
    public void testGenerateRandomComplex_impossible() throws Exception {
        KeyCode encodedKey = new KeyCode(10);
        encodedKey.setMaxTries(1);
        encodedKey.generateRandomComplex();
    }

    @Test
    /**
     * For the moment, any string of at least 12 unique characters is considered sufficiently complex
     */
    public void testVerifySufficientlyComplex_good() throws Exception {
        EncodedKey.verifySufficientlyComplex("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        EncodedKey.verifySufficientlyComplex("abcdefghijkl");
        EncodedKey.verifySufficientlyComplex("0123456789ab");
        EncodedKey.verifySufficientlyComplex("-=_+{}[];:.,");
        // The following is an example of an actual API kay as of Jan-2013
        EncodedKey.verifySufficientlyComplex("cvxfzc9bvb6txjrqasyejgcsrhjq68x2");
    }

    @Test(expected = SecurityException.class)
    public void testVerifySufficientlyComplex_null() throws Exception {
        EncodedKey.verifySufficientlyComplex(null);
    }

    @Test(expected = SecurityException.class)
    public void testVerifySufficientlyComplex_notLongEnough() throws Exception {
        EncodedKey.verifySufficientlyComplex("123");
    }

    @Test(expected = SecurityException.class)
    public void testVerifySufficientlyComplex_LongButNotUnique() throws Exception {
        EncodedKey.verifySufficientlyComplex("00000000000000000000");
    }

    @Test(expected = SecurityException.class)
    public void testVerifySufficientlyComplex_VeryLongButNotUnique() throws Exception {
        EncodedKey.verifySufficientlyComplex("123ABC123ABC123ABC123ABC123ABC123ABC123ABC123ABC123ABC123ABC"); // long
    }


}
