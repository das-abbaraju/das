package com.picsauditing.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.picsauditing.security.EncodedKey;


public class EncodedKeyTest {

	private static final int TEST_SIZE = 100;

	/*
	 * If we force randomApiKey() to only one try, then we're bound to get an insufficiently complex code at least once in 100 tests.
	 */
	@Test(expected = SecurityException.class)
	public void testApiKey_LimitToOneTry() throws Exception {
		for (int i = 0; i < TEST_SIZE; i++) {
			EncodedKey.randomApiKey(1);
		}
	}

	@Test
	/*
	 * If we allow randomApiKey() to have 10 tries, then the odds of failing should be non existent.
	 */
	public void testApiKey() throws Exception {
		Set<String> keys = new HashSet<String>();
		for (int i = 0; i < TEST_SIZE; i++) {
			String apiKey = EncodedKey.randomApiKey();
			keys.add(apiKey);
			// System.out.println(apiKey); // e.g. 16096rdumluz761fh2ty84956v7lelav
			assertEquals(32, apiKey.length());
		}
		assertEquals(TEST_SIZE, keys.size());
	}

	@Test
	/*
	 * This is merely a test for "works as coded," not necessarily the desired
	 * behavior. Feel free to change this. -- Craig Jones 9/11/2012
	 */
	public void testPassword() throws Exception {
		Set<String> passwords = new HashSet<String>();
		for (int i = 0; i < TEST_SIZE; i++) {
			String pass = EncodedKey.randomPassword();
			// System.out.println(pass); // e.g. -6291831365154337367
			assertTrue(pass.length() <= 20);
			passwords.add(pass);
		}
		assertEquals(TEST_SIZE, passwords.size());
	}

    @Test
    /**
     * For the moment, any string of at least 20 characters is considered sufficiently complex and will not throw an
     * exception
     */
    public void testVerifySufficientlyComplex_good() throws Exception {
        EncodedKey.verifySufficientlyComplex("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    @Test
    public void testVerifySufficientlyComplex_exactly75percentUnique() throws Exception {
        EncodedKey.verifySufficientlyComplex("0123456789ABCDEABCDE"); // 15 unique chars out of 20 (75% unique)
    }

    @Test(expected = SecurityException.class)
    public void testVerifySufficientlyComplex_null() throws Exception {
        EncodedKey.verifySufficientlyComplex(null);
    }

    @Test(expected = SecurityException.class)
    public void testVerifySufficientlyComplex_neitherUniqueNorLongEnough() throws Exception {
        EncodedKey.verifySufficientlyComplex("123");
    }

    @Test(expected = SecurityException.class)
    public void testVerifySufficientlyComplex_nothingUniqueAtAll() throws Exception {
        EncodedKey.verifySufficientlyComplex("00000000000000000000");
    }

    @Test(expected = SecurityException.class)
    public void testVerifySufficientlyComplex_notUniqueEnough() throws Exception {
        EncodedKey.verifySufficientlyComplex("0123456789ABCD0123456789ABCD"); // long enough,
        // but only 14 unique chars out of 28 (50% unique)
    }

    @Test(expected = SecurityException.class)
    public void testVerifySufficientlyComplex_uniqueButTooShort() throws Exception {
        EncodedKey.verifySufficientlyComplex("ABCDEFGHIJKLMNOPQRS"); // unique, but only 19 chars
    }
}
