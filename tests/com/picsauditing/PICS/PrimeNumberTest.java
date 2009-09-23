package com.picsauditing.PICS;

import junit.framework.TestCase;

import org.junit.Test;

public class PrimeNumberTest extends TestCase {
	public void testPrimeNumber() {
		assertEquals(true, isPrime(3));
		assertEquals(false, isPrime(4));
		assertEquals(true, isPrime(5));
		assertEquals(true, isPrime(11));
		assertEquals(false, isPrime(12));
		assertEquals(true, isPrime(13));
		assertEquals(false, isPrime(27));
		assertEquals(true, isPrime(29));
		assertEquals(false, isPrime(30));
	}
	
	private boolean isPrime(int candidate) {
		// TODO write
		return true;
	}

}
