package com.picsauditing.jpa.entities;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: pschlesinger
 * Date: 7/17/13
 * Time: 9:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class CountrySubdivisionTest extends TestCase {
	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetTwoLetterIsoCodeWithFullISOCode() throws Exception {
		CountrySubdivision countrySubdivision = new CountrySubdivision("US-CA");
		String expected = "CA";
		String actual = countrySubdivision.getTwoLetterIsoCode();
		assertEquals(expected,actual);
	}

	@Test
	public void testGetTwoLetterIsoCodeWithNullISOCode() throws Exception {
		CountrySubdivision countrySubdivision = new CountrySubdivision();
		String expected = null;
		String actual = countrySubdivision.getTwoLetterIsoCode();
		assertEquals(expected,actual);
	}

	@Test
	public void testGetTwoLetterIsoCodeWithTwoLetterISOCode() throws Exception {
		CountrySubdivision countrySubdivision = new CountrySubdivision("NY");
		String expected = "NY";
		String actual = countrySubdivision.getTwoLetterIsoCode();
		assertEquals(expected,actual);
	}

	@Test
	public void testGetTwoLetterIsoCodeWithEmptyStringISOCode() throws Exception {
		CountrySubdivision countrySubdivision = new CountrySubdivision("");
		String expected = "";
		String actual = countrySubdivision.getTwoLetterIsoCode();
		assertEquals(expected,actual);
	}


	@Test
	public void testGetIsoCode() throws Exception {

	}

	@Test
	public void testSetIsoCode() throws Exception {

	}

	@Test
	public void testGetEnglish() throws Exception {

	}

	@Test
	public void testSetEnglish() throws Exception {

	}

	@Test
	public void testGetCountry() throws Exception {

	}

	@Test
	public void testSetCountry() throws Exception {

	}

	@Test
	public void testSetName() throws Exception {

	}

	@Test
	public void testGetName() throws Exception {

	}

	@Test
	public void testGetSimpleName() throws Exception {

	}

	@Test
	public void testToString() throws Exception {

	}

	@Test
	public void testGetAutocompleteResult() throws Exception {

	}

	@Test
	public void testGetAutocompleteItem() throws Exception {

	}

	@Test
	public void testGetAutocompleteValue() throws Exception {

	}

	@Test
	public void testToJSON() throws Exception {

	}

	@Test
	public void testGetI18nKey() throws Exception {

	}

	@Test
	public void testCompareTo() throws Exception {

	}

	@Test
	public void testEquals() throws Exception {

	}
}
