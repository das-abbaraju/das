package com.picsauditing.auditbuilder.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class StringsTest {

	@Test
	public void testParseFloat() {
		NumberFormat format = new DecimalFormat("#,##0");

		String answer = "1234567890";

		BigDecimal value = new BigDecimal(answer);
		String valueString = format.format(value);

		assertEquals("1,234,567,890", valueString);
	}

	@Test
	public void testIndexName() {
		assertEquals(null, oldIndexName(null));
		assertEquals("HBOBQJOHNS5STARCRANEINCBJCRANE", oldIndexName(" H. Bob & Q. John's 5 Star Crane Inc./BJ Crane"));
		assertEquals("QWERTYASDFZXCV", oldIndexName("QWERTYASDFZXCV"));
	}

	private String oldIndexName(String name) {
		if (name == null) {
			return null;
		}
		name = name.toUpperCase();

		String expression = "[A-Z0-9]+";
		Pattern pattern = Pattern.compile(expression, Pattern.CANON_EQ);
		Matcher matcher = pattern.matcher(name);

		StringBuffer buf = new StringBuffer();
		while (matcher.find()) {
			buf.append(matcher.group());
		}

		return buf.toString();
	}

	@Test
	public void testIndexNameTrim() {
		// Test indexName with spaces
		assertEquals("5 STAR RESTAURANT".toUpperCase(), indexName("   The 5 Star Restaurant".trim()));
		assertEquals("NAME COMPANY", indexName("The Name Company Inc LLC"));
		assertEquals("2000 HORIZON COMPANY", indexName("The 2000 Horizon Company,Inc."));
		assertEquals("HARRY E MUELLER THE KEY MAN", indexName("Harry E. Mueller \"The Key Man\", Inc"));
		assertEquals("LEBLANC THERIOT EQUIP CO", indexName("LeBlanc & Theriot Equip. Co., Inc."));
		assertEquals("AHC SERVICES", indexName("AHC Services, L.L.C. "));
		assertEquals("COMPLETE DATACOM", indexName("Complete DataCom,LLC"));
		assertEquals("NOHS NATIONAL OCCUPATIONAL HEALTH SERVICES",
				indexName("NOHS (National Occupational Health Services, LLC)"));
		assertEquals("DESALTERS", indexName("DESALTERS-LLC"));
		assertEquals("EUROPEAN MACHINE TOOLS", indexName("European Machine Tools.LLC"));
		assertEquals("AAK MECHANICAL", indexName("AAK Mechanical, In.c"));
		assertEquals("QUOTED COMPANY", indexName("The \"Quoted\" Company"));
	}

	private String indexName(String name) {
		if (name == null) {
			return null;
		}

		name = name.toUpperCase();
		name = name.replaceAll("[\\'\\\"]", "");
		name = name.replaceAll("\\W", " ");
		name = name.replaceAll("_", " ");
		name = name.replaceAll(" +", " ");
		name = name.trim();
		name = name.replaceAll("^THE ", "");
		name = name.replaceAll(" L ?L ?C$", "");
		name = name.replaceAll(" I ?N ?C$", "");

		return name;
	}

	char singleQuote = '\'';
	char backSlash = '\\';

	@Test
	public void testNoQuotes() {
		assertEquals("abc", Strings.escapeQuotes("abc"));
	}

	@Test
	public void testSingleQuote() {
		assertEquals("ab" + singleQuote + singleQuote, Strings.escapeQuotes("ab" + singleQuote));
	}

	@Test
	public void testDoubleSingleQuote() {
		assertEquals("ab" + singleQuote + singleQuote + singleQuote + singleQuote,
				Strings.escapeQuotes("ab" + singleQuote + singleQuote));
	}

	@Test
	public void testNoSlash() {
		assertEquals("abc", Strings.escapeSlashes("abc"));
	}

	@Test
	public void testSingleSlash() {
		assertEquals("ab" + backSlash + backSlash, Strings.escapeSlashes("ab" + backSlash));
	}

	@Test
	public void testDoubleSingleSlash() {
		assertEquals("ab" + backSlash + backSlash + backSlash + backSlash,
				Strings.escapeSlashes("ab" + backSlash + backSlash));
	}

	@Test
	public void testNoSlashOrQuote() {
		assertEquals("abc", Strings.escapeQuotesAndSlashes("abc"));
	}

	@Test
	public void testSingleSlashAndSingleQuote() {
		assertEquals("ab" + backSlash + backSlash + singleQuote + singleQuote, Strings.escapeQuotesAndSlashes("ab" + backSlash + singleQuote));
	}

	@Test
	public void testDoubleSingleSlashAndDoubleSingleQuote() {
		assertEquals("ab" + backSlash + backSlash + backSlash + backSlash + singleQuote + singleQuote + singleQuote + singleQuote,
				Strings.escapeQuotesAndSlashes("ab" + backSlash + backSlash + singleQuote + singleQuote));
	}

    @Test
    public void testImplode_List() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        assertEquals("1,2,3", Strings.implode(list));
    }

    @Test
	public void testImplode_WhenIPassInAnEmptyList_ThenItReturnsEmptyString() {
		assertEquals("", Strings.implode(Collections.EMPTY_LIST));
	}
}
