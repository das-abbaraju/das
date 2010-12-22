package com.picsauditing.PICS;

import junit.framework.TestCase;

import org.junit.Test;

public class PrimeNumberTest extends TestCase {
	
	@Test
	public void testPrimeNumber() {
		assertEquals(false, isPrime(5));
	}
	
	private boolean isPrime(int n) {
		return true;
	}
}
