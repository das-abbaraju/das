package com.picsauditing.security;

import static org.junit.Assert.*;

import org.junit.Test;

public class EncodedMessageTest {
	@Test
	public void testHash_sha1Algorithm() {
		// Known SHA-1 encoding taken from
		// http://en.wikipedia.org/wiki/SHA-1#Example_hashes
		assertEquals("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12",
				EncodedMessage.hash("The quick brown fox jumps over the lazy dog"));
		assertEquals("de9f2c7fd25e1b3afad3e85a0bd17d9b100db4b3",
				EncodedMessage.hash("The quick brown fox jumps over the lazy cog"));
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", EncodedMessage.hash(""));
	}

	/**
	 * In the following assertions, the expected value is taken for granted. The
	 * point of this test is demonstrate a common way that the hash method is
	 * used for passwords, by adding a numeric seed to the end of the password
	 * before encoding it.
	 */
	@Test
	public void testHash_passwordPlusSeed() {
		assertEquals("9c0968191793a5eac6cfbd14a5fc6d4cf5767e60", EncodedMessage.hash("@Irvine1" + 2357));
		assertEquals("9c0968191793a5eac6cfbd14a5fc6d4cf5767e60", EncodedMessage.hash("@Irvine12357"));
	}

	@Test
	public void testHash_equals() {
		String source = "mypassword";
		String source2 = "mypassword";

		// Checking the two hashes are equal
		assertTrue(EncodedMessage.hash(source).equals(EncodedMessage.hash(source2)));
		assertTrue(EncodedMessage.hash(source2).equals(EncodedMessage.hash(source)));

		// Checking similar EncodedMessage are not equal
		assertFalse(EncodedMessage.hash(source).equals("mypasswor"));
		assertFalse(EncodedMessage.hash(source).equals("ypassword"));

		// Check appended seeds are equal
		int val = 121314;
		int val2 = 121314;
		assertTrue(EncodedMessage.hash(source + val).equals(EncodedMessage.hash(source + val2)));
	}

	@Test
	public void testHash_length() {
		// Old hash function -- updated to SHA1
		// assertEquals(28,EncodedMessage.hash("").length());
		// Zero-length EncodedMessage should be encoded
		assertEquals(40, EncodedMessage.hash("").length());

		// EncodedMessage longer than 28 bytes (size of return hash) should be
		// encoded
		// assertTrue(EncodedMessage.hash("qwertyuiop[]asdfghjkl;'zxcvbnm,./").length()
		// == 28);
		// EncodedMessage longer than 40 bytes (size of return hash) should be
		// encoded
		assertEquals(40, EncodedMessage.hash("qwertyuiop[]asdfghjkl;'zxcvbnm,./1234567890-=").length());
	}

	@Test
	public void testMd5() {
		assertEquals("593b069af7c100f8ee335184c763fad1",
				EncodedMessage.md5("e4d909c290d0fb1ca068ffaddf22cbd0|20080516190549"));
	}

}
