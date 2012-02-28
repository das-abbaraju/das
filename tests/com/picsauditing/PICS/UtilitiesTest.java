package com.picsauditing.PICS;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;


public class UtilitiesTest {

	@Test
	public void testToListNullInput() {
		assertEquals(0, Utilities.toList().size());
	}
	
	@Test
	public void testToList() {
		List<Integer> numbers = Utilities.toList(1, 2, 3, 4);
		
		for (int index = 0; index < numbers.size(); index++) {
			assertTrue((index + 1) == numbers.get(index));
		}
	}
	
}
