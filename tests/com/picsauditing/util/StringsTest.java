package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class StringsTest {

	@Test
	public void testInsertSpaceNull() {
		assertEquals(null, Strings.insertSpaces(null));
	}

	@Test
	public void testInsertSpace1() {
		assertEquals("a", Strings.insertSpaces("a"));
	}

	@Test
	public void testInsertSpace3() {
		assertEquals("a b c", Strings.insertSpaces("abc"));
	}

	@Test
	public void testParseFloat() {
		NumberFormat format = new DecimalFormat("#,##0");

		String answer = "1234567890";

		BigDecimal value = new BigDecimal(answer);
		String valueString = format.format(value);

		assertEquals("1,234,567,890", valueString);
	}

	@Test
	public void testPhoneStripper() {
		assertEquals("9112223333", Strings.stripPhoneNumber("(911)222-3333"));
		assertEquals("9112223333", Strings.stripPhoneNumber("911-222-3333"));
		assertEquals("8002223333", Strings.stripPhoneNumber("1(800) 222-3333"));
		assertEquals("9112223333", Strings.stripPhoneNumber("911.222.3333 x4"));
	}

	@Test
	public void testExtractAccountID() {
		assertEquals(123456, Strings.extractAccountID("123456"));
		assertEquals(123456, Strings.extractAccountID("123456.7"));
		assertEquals(0, Strings.extractAccountID("123412341234123412341"));
		assertEquals(0, Strings.extractAccountID("Bob's Cranes"));
		assertEquals(0, Strings.extractAccountID("1 Micro"));
	}

	@Test
	public void testMapParams() {
		String url = "response=3&responsetext=Invalid Customer Vault Id REFID:101460773&authcode=&transactionid=0&avsresponse=&cvvresponse=&orderid=123&type=sale&response_code=300&customer_vault_id=0";
		Map<String, String> map = Strings.mapParams(url);
		assertEquals("3", map.get("response"));
		assertEquals("Invalid Customer Vault Id REFID:101460773", map.get("responsetext"));
		assertEquals("", map.get("authcode"));
		assertEquals("0", map.get("customer_vault_id"));
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

	@Test
	public void testFormat() {
		assertEquals("0", Strings.formatShort(0.000001234f));
		assertEquals("0.001", Strings.formatShort(0.001235678f));
		assertEquals("0.123", Strings.formatShort(0.123456789f));
		assertEquals("0.124", Strings.formatShort(0.12356789f));
		assertEquals("1", Strings.formatShort(1f));
		assertEquals("1.2", Strings.formatShort(1.2f));
		assertEquals("1.23", Strings.formatShort(1.23f));
		assertEquals("1.23", Strings.formatShort(1.234f));
		assertEquals("12.3", Strings.formatShort(12.34567f));
		assertEquals("123", Strings.formatShort(123.4567f));
		assertEquals("1.23K", Strings.formatShort(1234.567f));
		assertEquals("12.3K", Strings.formatShort(12345.67f));
		assertEquals("123K", Strings.formatShort(123456.7f));
		assertEquals("1.23M", Strings.formatShort(1234567f));
		assertEquals("1.23B", Strings.formatShort(1234567890f));
	}

	@Test
	public void testIsInCountries() {
		Set<String> usOnly = new HashSet<String>();
		usOnly.add("US");
		Set<String> usAndCanada = new HashSet<String>();
		usAndCanada.add("US");
		usAndCanada.add("CA");
		Set<String> franceOnly = new HashSet<String>();
		franceOnly.add("FR");
		Set<String> germanyOnly = new HashSet<String>();
		germanyOnly.add("DE");
		Set<String> all = new HashSet<String>();
		all.add("US");
		all.add("CA");
		all.add("FR");
		all.add("DE");

		String expression = "";
		assertTrue(Strings.isInCountries(expression, null));
		assertTrue(Strings.isInCountries(expression, usOnly));

		expression = "|US|";
		assertTrue(Strings.isInCountries(expression, usOnly));
		assertFalse(Strings.isInCountries(expression, franceOnly));

		expression = "|US|FR|";
		assertTrue(Strings.isInCountries(expression, usOnly));
		assertFalse(Strings.isInCountries(expression, germanyOnly));
		assertTrue(Strings.isInCountries(expression, usAndCanada));
		assertTrue(Strings.isInCountries(expression, all));

		expression = "!|CA|US|";
		assertTrue(Strings.isInCountries(expression, franceOnly));
		assertFalse(Strings.isInCountries(expression, usOnly));
		assertTrue(Strings.isInCountries(expression, all));

		expression = "!|US|";
		usOnly.add("US");
		assertFalse(Strings.isInCountries(expression, usOnly));
		boolean inCountries = Strings.isInCountries(expression, usAndCanada);
		assertTrue(inCountries);
	}

	@Test
	public void testSimilarTo() {
		assertFalse(Strings.isSimilarTo("Tom", "Time", 0));
		assertFalse(Strings.isSimilarTo("Tom", "Time", 1));
		assertTrue(Strings.isSimilarTo("Tom", "Time", 2));
		assertTrue(Strings.isSimilarTo("Tom", "Time", 3));
		assertTrue(Strings.isSimilarTo("Tom", "Time", 4));
		assertTrue(Strings.isSimilarTo("Tom", "Time", 5));
		assertTrue(Strings.isSimilarTo("Tom", "Tom", 0)); // exact matching
		assertTrue(Strings.isSimilarTo("Tom", "Tom", 1));
		assertFalse(Strings.isSimilarTo("Empty", "", 4));
		assertTrue(Strings.isSimilarTo("Empty", "", 5));
		assertTrue(Strings.isSimilarTo("Empty", "", 6));
		assertFalse(Strings.isSimilarTo("A", "B", 0));
		assertTrue(Strings.isSimilarTo("A", "B", 1));
		assertTrue(Strings.isSimilarTo("A", "B", 2));
		assertTrue(Strings.isSimilarTo("", "", 0));
	}

	@Test
	public void testMaskSSN() throws Exception {
		String ssnMask = Strings.maskSSN("999999999");

		assertEquals("XXX-XX-9999", ssnMask);
	}

	@Test
	public void testEqualNullSafe() {
		assertTrue(Strings.isEqualNullSafe(null, null));
		assertFalse(Strings.isEqualNullSafe(null, "foobar"));
		assertFalse(Strings.isEqualNullSafe("foobar", null));
		assertFalse(Strings.isEqualNullSafe("foo", "bar"));
		assertTrue(Strings.isEqualNullSafe("foobar", "foobar"));
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
	public void nullToBlank() {
		assertEquals("", Strings.nullToBlank(null));
		assertEquals("", Strings.nullToBlank(""));
		assertEquals("  ", Strings.nullToBlank("  "));
		assertEquals(" This is a test ", Strings.nullToBlank(" This is a test "));
	}

	@Test
	public void testBothNonBlanksAndOneBeginsWithTheOther() {
		assertTrue(Strings.bothNonBlanksAndOneBeginsWithTheOther("Starts", "Starts With"));
		assertTrue(Strings.bothNonBlanksAndOneBeginsWithTheOther("Starts with", "Starts"));
		assertFalse(Strings.bothNonBlanksAndOneBeginsWithTheOther(null, "Starts with"));
		assertFalse(Strings.bothNonBlanksAndOneBeginsWithTheOther("", null));
		assertFalse(Strings.bothNonBlanksAndOneBeginsWithTheOther("", "Starts with"));
	}

	@Test
	public void testStripNonAlphaNumericCharacters() {
		assertEquals("ABC", Strings.stripNonAlphaNumericCharacters("A-B C"));
		assertEquals("A1b2", Strings.stripNonAlphaNumericCharacters("A1,b-\"2\""));
		assertEquals("FR7QKSpgKId4", Strings.stripNonAlphaNumericCharacters("FR7Q*KS$pg*@KId4"));
		assertEquals(Strings.EMPTY_STRING, Strings.stripNonAlphaNumericCharacters("!@#$%^&*()"));
	}

	@Test
	public void testImplode_WhenIPassInAnEmptyList_ThenItReturnsEmptyString() {
		assertEquals("", Strings.implode(Collections.EMPTY_LIST));
	}

	@Test
	public void testToStringPreserveNull_WhenIPassNull_ThenItReturnsNull() {
		assertEquals(null, Strings.toStringPreserveNull(null));
	}

	@Test
	public void testToString_WhenIPassNull_ThenItReturnsEmptyString() {
		assertEquals("", Strings.toString(null));
	}
}
