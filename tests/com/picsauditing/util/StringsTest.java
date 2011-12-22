package com.picsauditing.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import junit.framework.TestCase;

import org.junit.Test;

import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.search.SelectUserUnion;

public class StringsTest extends TestCase {

	public StringsTest(String name) {
		super(name);
	}

	public void testEmail() throws AddressException {
		// FIXME Why is this test here in StringsTest? -- Craig Jones
		Address[] addresses = InternetAddress.parse("tallred@picsauditing.com");
		assertEquals("tallred@picsauditing.com", addresses[0].toString());

		addresses = InternetAddress.parse("tallred@picsauditing.com, kn@pics.com");
		assertEquals("tallred@picsauditing.com", addresses[0].toString());
		assertEquals("kn@pics.com", addresses[1].toString());
		
		addresses = InternetAddress.parse("Trevor <tallred@picsauditing.com>");
		assertEquals("Trevor <tallred@picsauditing.com>", addresses[0].toString());
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
		// FIXME What is the purpose of this test? -- Craig Jones
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
		// FIXME What is the purpose of this test? -- Craig Jones
		String color = "Green";
		color = changeColor(color);
		assertEquals("Red", color);
	}

	private String changeColor(String color) {
		color = "Red";
		return color;
	}

	@Test
	public void testHash_sha1Algorithm() {
		// Known SHA-1 encoding taken from http://en.wikipedia.org/wiki/SHA-1#Example_hashes
		assertEquals("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12",Strings.hash("The quick brown fox jumps over the lazy dog"));
		assertEquals("de9f2c7fd25e1b3afad3e85a0bd17d9b100db4b3",Strings.hash("The quick brown fox jumps over the lazy cog"));
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709",Strings.hash(""));
	}

	/**
	 * In the following assertions, the expected value is taken for granted. The point of this test is
	 * demonstrate a common way that the hash method is used for passwords, by adding a numeric seed to the end of the
	 * password before encoding it.
	 */
	@Test
	public void testHash_passwordPlusSeed() {
		assertEquals("9c0968191793a5eac6cfbd14a5fc6d4cf5767e60",Strings.hash("@Irvine1" + 2357));
		assertEquals("9c0968191793a5eac6cfbd14a5fc6d4cf5767e60",Strings.hash("@Irvine12357"));
	}

	@Test
	public void testHash_equals() {
		String source = "mypassword";
		String source2 = "mypassword";

		// Checking the two hashes are equal
		assertTrue(Strings.hash(source).equals(Strings.hash(source2)));
		assertTrue(Strings.hash(source2).equals(Strings.hash(source)));

		// Checking similar strings are not equal
		assertFalse(Strings.hash(source).equals("mypasswor"));
		assertFalse(Strings.hash(source).equals("ypassword"));

		// Check appended seeds are equal
		int val = 121314;
		int val2 = 121314;
		assertTrue(Strings.hash(source + val).equals(Strings.hash(source + val2)));
	}

	@Test
	public void testHash_length() {
		// Old hash function -- updated to SHA1
		// assertEquals(28,Strings.hash("").length());
		// Zero-length strings should be encoded
		assertEquals(40,Strings.hash("").length());

		// Strings longer than 28 bytes (size of return hash) should be encoded
		// assertTrue(Strings.hash("qwertyuiop[]asdfghjkl;'zxcvbnm,./").length()
		// == 28);
		// Strings longer than 40 bytes (size of return hash) should be encoded
		assertEquals(40,Strings.hash("qwertyuiop[]asdfghjkl;'zxcvbnm,./1234567890-=").length());
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

	public void testMapParams() {
		String url = "response=3&responsetext=Invalid Customer Vault Id REFID:101460773&authcode=&transactionid=0&avsresponse=&cvvresponse=&orderid=123&type=sale&response_code=300&customer_vault_id=0";
		Map<String, String> map = Strings.mapParams(url);
		assertEquals("3", map.get("response"));
		assertEquals("Invalid Customer Vault Id REFID:101460773", map.get("responsetext"));
		assertEquals("", map.get("authcode"));
		assertEquals("0", map.get("customer_vault_id"));
	}

	public void testIndexName() {
		assertEquals(null, oldIndexName(null));
		assertEquals("HBOBQJOHNS5STARCRANEINCBJCRANE", oldIndexName(" H. Bob & Q. John's 5 Star Crane Inc./BJ Crane"));
		assertEquals("QWERTYASDFZXCV", oldIndexName("QWERTYASDFZXCV"));
	}

	private String oldIndexName(String name) {
		if (name == null)
			return null;
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
		if (name == null)
			return null;

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

	public void testMd5() {
		assertEquals("593b069af7c100f8ee335184c763fad1", Strings.md5("e4d909c290d0fb1ca068ffaddf22cbd0|20080516190549"));
	}

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

	public void testEditDistance() {
		assertEquals(Strings.editDistance("Tom", "Time"), 2);
		assertEquals(Strings.editDistance("Tom", "Tom"), 0);
		assertEquals(Strings.editDistance("Angel", "Angle"), 2);
		assertEquals(Strings.editDistance("Total", "Angle"), 5);
		assertEquals(Strings.editDistance("Empty", ""), 5);
		assertEquals(Strings.editDistance("A", "B"), 1);
		assertEquals(Strings.editDistance("", ""), 0);
	}

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

	public void testMaskSSN() throws Exception {
		String ssnMask = Strings.maskSSN("999999999");

		assertEquals("XXX-XX-9999", ssnMask);
	}
	
	public void testEqualNullSafe() {
		assertTrue(Strings.isEqualNullSafe(null, null));
		assertFalse(Strings.isEqualNullSafe(null, "foobar"));
		assertFalse(Strings.isEqualNullSafe("foobar", null));
		assertFalse(Strings.isEqualNullSafe("foo", "bar"));
		assertTrue(Strings.isEqualNullSafe("foobar", "foobar"));
	}


}
