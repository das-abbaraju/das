package com.picsauditing.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class StringsTest extends TestCase {

	public StringsTest(String name) {
		super(name);
	}

	public void testInsertSpaceNull() {
		assertEquals(null, Strings.insertSpaces(null));
	}

	public void testInsertSpace1() {
		assertEquals("a", Strings.insertSpaces("a"));
	}

	public void testInsertSpace3() {
		assertEquals("a b c", Strings.insertSpaces("abc"));
	}

	public void testArray() {
		List<String> list = new ArrayList<String>();
		list.add("Hello");
		addString(list);
		addString(list);
		assertEquals(3, list.size());
	}
	
	private void addString(List<String> list) {
		list.add("World" + list.size());
	}

	public void testString() {
		String color = "Green";
		color = changeColor(color);
		assertEquals("Red", color);
	}
	
	private String changeColor(String color) {
		color = "Red";
		return color;
	}

	public void testHash() {
	 	System.out.println(Strings.hashUrlSafe("testinsfgsf")); 
	}
}
