package com.picsauditing.util;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.search.SelectUserUnion;

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
	
	public void testSQL() {
		SelectUserUnion sql = new SelectUserUnion();
		//System.out.println(sql.toString());
		sql.addField("u.id");
		sql.addField("u.username");
		sql.addField("u.name");
		sql.addField("u.accountID");
		sql.addField("a.name");
		sql.addJoin("JOIN accounts a ON a.id = u.accountID");
		
		sql.addWhere("u.isActive = 'Y'");
		sql.addOrderBy("u.name");
		sql.setLimit(100);
		//System.out.println(sql.toString());
		sql.toString();
	}
}
