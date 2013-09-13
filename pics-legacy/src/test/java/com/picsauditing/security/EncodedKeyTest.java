package com.picsauditing.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.picsauditing.security.EncodedKey;

public class EncodedKeyTest {
	private static final int TEST_SIZE = 100;


	@Test
	/*
	 * Same test as above, but using the randomApiKey() convenience method
	 */
	public void testApiKey() throws Exception {
		Set<String> keys = new HashSet<String>();
		for (int i = 0; i < TEST_SIZE; i++) {
			String apiKey = EncodedKey.randomApiKey();
			keys.add(apiKey);
			assertEquals(32, apiKey.length());
		}
		assertEquals(TEST_SIZE, keys.size());
	}

	@Test
	/*
	 * Same test as above, but using the randomApiKey() convenience method
	 */
	public void testNewServerSecretKey() throws Exception {
		Set<String> keys = new HashSet<String>();
		for (int i = 0; i < TEST_SIZE; i++) {
			String secretKey = EncodedKey.newServerSecretKey();
			keys.add(secretKey);
			assertEquals(76+1+10+2, secretKey.length());
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

}
