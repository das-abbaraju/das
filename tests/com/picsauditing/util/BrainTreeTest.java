package com.picsauditing.util;

import junit.framework.TestCase;

public class BrainTreeTest extends TestCase {
	public BrainTreeTest(String name) {
		super(name);
	}

	public void testHashGenerator() {
		// Used values from http://tools.getbraintree.com/hasher
		String hash = BrainTree.buildHash("123", "100", "954812", "20091231235959", "jf294lka9rhjtr981jfkig491");
		assertEquals("46fc4025b07f3ee09febc3f562e474a2", hash);
	}

	public void testHashGenerator2() {
		// Used values from http://tools.getbraintree.com/hasher
		String hash = BrainTree.buildHash("", "", "", "20091231235959", "jf294lka9rhjtr981jfkig491");
		assertEquals("70b71f5c7ec93657c006e5b9f72e021d", hash);
	}

}
