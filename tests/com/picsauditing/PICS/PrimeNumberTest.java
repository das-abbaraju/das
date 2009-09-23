package com.picsauditing.PICS;

import junit.framework.TestCase;

import org.junit.Test;

public class PrimeNumberTest extends TestCase {
	@Test
	public void testPrimeNumber() {
		int i = 2;
		long starttime = System.currentTimeMillis();
		while( i < 100000) {
			if(isPrime(i))
				System.out.println(i);
			i++;
		}
		long endtime = System.currentTimeMillis();
		System.out.println("Time Taken :" + (endtime-starttime));
	}
	
	private boolean isPrime(int candidate) {
		for(int i=2; i<=  Math.ceil(Math.sqrt(candidate)); i++) {
			if(candidate%i == 0)
				return false;
		}
		return true;
	}

}
