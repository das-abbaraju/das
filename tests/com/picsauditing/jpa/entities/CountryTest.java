package com.picsauditing.jpa.entities;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CountryTest {
	Country usa;
	Country france;
	Country canada;
	Country uk;
	Country uae;
	Country austria;
	Country lithuania;

	@Before
	public void setUp() throws Exception {
		usa = new Country("US");
		usa.english = "United States";
		// TODO this is barely scratching the surface, e.g. it's not filling in
		// name, which is a TranslatableString, etc.

		france = new Country("FR");
		canada = new Country("CA");
		uk = new Country("GB");
		uae = new Country("AE");

		// See http://tickets.picsauditing.com/browse/PICS-6555?focusedCommentId=34012&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-34012
		austria = new Country("AT");
		lithuania = new Country("LT");
	}

	@Test
	public void testConvertToCode() {
		// FIXME Is this convertToCode() method used anywhere? It's hard-coded
		// for just the two conversions below ("United States" and "Canada").
		assertEquals("US", Country.convertToCode("US"));
		assertEquals("US", Country.convertToCode("United States"));
		assertEquals("CA", Country.convertToCode("Canada"));
		assertEquals("CA", Country.convertToCode("CA"));
	}

	@Test
	public void testToString() {
		// TODO this is barely scratching the surface
		assertEquals("United States", usa.toString());
		usa.name = null;
		assertEquals("United States", usa.toString());
		usa.english = null;
		assertEquals("US", usa.toString());
	}

	@Test
	public void testToJSON() {
		// TODO this is barely scratching the surface
		Country usa = new Country("US");
		assertEquals("{\"isoCode\":\"US\"}", usa.toJSON().toString());
	}

	@Test
	public void testIsParticularCountry() {
		assertTrue(usa.isUS());
		assertFalse(usa.isFrance());
		assertFalse(usa.isCanada());
		assertFalse(usa.isUK());
		assertFalse(usa.isUAE());

		assertTrue(france.isFrance());
		assertFalse(france.isUS());
		assertFalse(france.isCanada());
		assertFalse(france.isUK());
		assertFalse(france.isUAE());

		assertTrue(canada.isCanada());
		assertFalse(canada.isFrance());
		assertFalse(canada.isUS());
		assertFalse(canada.isUK());
		assertFalse(canada.isUAE());

		assertTrue(uk.isUK());
		assertFalse(uk.isFrance());
		assertFalse(uk.isUS());
		assertFalse(uk.isCanada());
		assertFalse(uk.isUAE());

		assertTrue(uae.isUAE());
		assertFalse(uae.isFrance());
		assertFalse(uae.isUS());
		assertFalse(uae.isCanada());
		assertFalse(uae.isUK());
	}

	@Test
	public void testIsEuropeanUnion() {
		// Not at all in the EU
		assertFalse(usa.isEuroZone());
		assertFalse(usa.isEuropeanUnion());
		assertFalse(canada.isEuroZone());
		assertFalse(canada.isEuropeanUnion());

		// In the EuroZone
		assertTrue(france.isEuroZone());
		assertTrue(france.isEuropeanUnion());
		assertTrue(austria.isEuroZone());
		assertTrue(austria.isEuropeanUnion());

		// In the EU, but not in the EuroZone
		assertFalse(uk.isEuroZone());
		assertTrue(uk.isEuropeanUnion());
		assertFalse(lithuania.isEuroZone());
		assertTrue(lithuania.isEuropeanUnion());
	}

	@Test
	public void testIsHasCountrySubdivisions_AcceptedCountry() {
		Country countryWithSubdivisions = new Country(Country.COUNTRIES_WITH_SUBDIVISIONS.get(0));
		List<CountrySubdivision> countrySubdivisionList = new ArrayList<>();
		countrySubdivisionList.add(new CountrySubdivision("XX"));

		countryWithSubdivisions.setCountrySubdivisions(countrySubdivisionList);
		assertTrue(countryWithSubdivisions.isHasCountrySubdivisions());

		countrySubdivisionList.clear();
		countryWithSubdivisions.setCountrySubdivisions(countrySubdivisionList);
		assertFalse(countryWithSubdivisions.isHasCountrySubdivisions());
	}

	@Test
	public void testIsHasCountrySubdivisions_NotAcceptedCountry() {
		Country countryWithSubdivisions = new Country("XX");
		List<CountrySubdivision> countrySubdivisionList = new ArrayList<>();
		countrySubdivisionList.add(new CountrySubdivision("XX"));

		countryWithSubdivisions.setCountrySubdivisions(countrySubdivisionList);
		assertFalse(countryWithSubdivisions.isHasCountrySubdivisions());

		countrySubdivisionList.clear();
		countryWithSubdivisions.setCountrySubdivisions(countrySubdivisionList);
		assertFalse(countryWithSubdivisions.isHasCountrySubdivisions());
	}
}
