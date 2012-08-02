package com.picsauditing.util;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.PhysicalAddress;
import com.picsauditing.model.general.PhysicalAddressBean;

public class PhysicalAddressUtilsTest {

	Map<String, Country> countries;
	Country unitedStates;
	Map<String, CountrySubdivision> countrySubdivisions;
	CountrySubdivision california;

	@Before
	public void setUp() throws Exception {
		countries = EntityFactory.mostCommonCountries();
		unitedStates = countries.get("US");
		countrySubdivisions = EntityFactory.someExampleCountrySubdivisions();
		california = countrySubdivisions.get("CA");
	}

	@Test
	public void testAssumptions() throws Exception {
		assertEquals("CA", california.getIsoCode());
		// assertEquals("CA", california.getI18nKey());
		assertEquals("California, United States", california.getName());
		assertEquals("California", california.getSimpleName());
	}

	@Test
	public void testFullAddress_UnitedStates_Minimal() throws Exception {
		PhysicalAddress a = new PhysicalAddressBean("1234 S. Main Street", null, null, "Anytown", california, "99999",
				unitedStates);

		// default base Country = US
		assertEquals("1234 S. Main Street<br/>Anytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "<br/>"));
		assertEquals("1234 S. Main Street\nAnytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "\n"));
		assertEquals("1234 S. Main Street; Anytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "; "));

		// explicit base Country = US
		assertEquals("1234 S. Main Street<br/>Anytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "<br/>", "US"));
		assertEquals("1234 S. Main Street\nAnytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "\n", "US"));
		assertEquals("1234 S. Main Street; Anytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "; ", "US"));

		// base Country = UK, so show that country is US
		assertEquals("1234 S. Main Street<br/>Anytown, CA 99999<br/>United States",
				PhysicalAddressUtils.fullAddress(a, "<br/>", "UK"));
		assertEquals("1234 S. Main Street\nAnytown, CA 99999\nUnited States",
				PhysicalAddressUtils.fullAddress(a, "\n", "UK"));
		assertEquals("1234 S. Main Street; Anytown, CA 99999; United States",
				PhysicalAddressUtils.fullAddress(a, "; ", "UK"));

	}

	@Test
	public void testFullAddress_UnitedStates_WithSuiteLine() throws Exception {
		PhysicalAddress a = new PhysicalAddressBean("1234 S. Main Street", "Ste 567", null, "Anytown", california,
				"99999", unitedStates);

		// default base Country = US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>Anytown, CA 99999",
				PhysicalAddressUtils.fullAddress(a, "<br/>"));
		assertEquals("1234 S. Main Street\nSte 567\nAnytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "\n"));
		assertEquals("1234 S. Main Street; Ste 567; Anytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "; "));

		// explicit base Country = US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>Anytown, CA 99999",
				PhysicalAddressUtils.fullAddress(a, "<br/>", "US"));
		assertEquals("1234 S. Main Street\nSte 567\nAnytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "\n", "US"));
		assertEquals("1234 S. Main Street; Ste 567; Anytown, CA 99999", PhysicalAddressUtils.fullAddress(a, "; ", "US"));

		// base Country = UK, so show that country is US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>Anytown, CA 99999<br/>United States",
				PhysicalAddressUtils.fullAddress(a, "<br/>", "UK"));
		assertEquals("1234 S. Main Street\nSte 567\nAnytown, CA 99999\nUnited States",
				PhysicalAddressUtils.fullAddress(a, "\n", "UK"));
		assertEquals("1234 S. Main Street; Ste 567; Anytown, CA 99999; United States",
				PhysicalAddressUtils.fullAddress(a, "; ", "UK"));

	}

	@Test
	public void testFullAddress_UnitedStates_WithSuiteLineAndPoBoxLine() throws Exception {
		PhysicalAddress a = new PhysicalAddressBean("1234 S. Main Street", "Ste 567", "P.O. Box 8888", "Anytown",
				california, "99999", unitedStates);

		// default base Country = US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>P.O. Box 8888<br/>Anytown, CA 99999",
				PhysicalAddressUtils.fullAddress(a, "<br/>"));
		assertEquals("1234 S. Main Street\nSte 567\nP.O. Box 8888\nAnytown, CA 99999",
				PhysicalAddressUtils.fullAddress(a, "\n"));
		assertEquals("1234 S. Main Street; Ste 567; P.O. Box 8888; Anytown, CA 99999",
				PhysicalAddressUtils.fullAddress(a, "; "));

		// explicit base Country = US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>P.O. Box 8888<br/>Anytown, CA 99999",
				PhysicalAddressUtils.fullAddress(a, "<br/>", "US"));
		assertEquals("1234 S. Main Street\nSte 567\nP.O. Box 8888\nAnytown, CA 99999",
				PhysicalAddressUtils.fullAddress(a, "\n", "US"));
		assertEquals("1234 S. Main Street; Ste 567; P.O. Box 8888; Anytown, CA 99999",
				PhysicalAddressUtils.fullAddress(a, "; ", "US"));

		// base Country = UK, so show that country is US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>P.O. Box 8888<br/>Anytown, CA 99999<br/>United States",
				PhysicalAddressUtils.fullAddress(a, "<br/>", "UK"));
		assertEquals("1234 S. Main Street\nSte 567\nP.O. Box 8888\nAnytown, CA 99999\nUnited States",
				PhysicalAddressUtils.fullAddress(a, "\n", "UK"));
		assertEquals("1234 S. Main Street; Ste 567; P.O. Box 8888; Anytown, CA 99999; United States",
				PhysicalAddressUtils.fullAddress(a, "; ", "UK"));

	}

	@Test
	public void testShortAddress_UnitedStates_Minimal() throws Exception {
		unitedStates.setEnglish("United States");

		PhysicalAddress a = new PhysicalAddressBean(null, null, null, "Anytown", california, "99999", unitedStates);

		// default base Country = US
		assertEquals("Anytown, California", PhysicalAddressUtils.shortAddress(a, ", "));
		assertEquals("Anytown; California", PhysicalAddressUtils.shortAddress(a, "; "));

		// explicit base Country = US
		assertEquals("Anytown, California", PhysicalAddressUtils.shortAddress(a, ", ", "US"));
		assertEquals("Anytown; California", PhysicalAddressUtils.shortAddress(a, "; ", "US"));

		// base Country = UK, so show that country is US
		assertEquals("Anytown, California, United States", PhysicalAddressUtils.shortAddress(a, ", ", "UK"));
		assertEquals("Anytown; California; United States", PhysicalAddressUtils.shortAddress(a, "; ", "UK"));

	}

	// FIXME We need more tests for other countries (especially UAE, which uses
	// neither States nor Zip codes).

	// FIXME For the UAE test, the State field needs to be left null (not
	// blank). See PICS-6193 (and reaffirm PICS-1030).

	// FIXME We need more tests for other combinations of missing fields, like
	// when the zip code is null or blank.

	// FIXME There are probably lots of edge cases that ought to be tested, like
	// sending a package from one country in Europe to another country in
	// Europe, from Europe to the US, from US to China, etc. This is the kind of
	// thing that we need to consider every time we start to work in another
	// country
}
