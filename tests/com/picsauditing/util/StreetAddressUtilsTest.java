package com.picsauditing.util;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.StreetAddress;

public class StreetAddressUtilsTest {

	class BasicAddress implements StreetAddress {
		public String address;
		public String address2;
		public String address3;
		public String city;
		public State  state;
		public String zip;
		public Country country;

		@Override
		public String getAddress() {
			return address;
		}

		@Override
		public String getAddress2() {
			return address2;
		}

		@Override
		public String getAddress3() {
			return address3;
		}

		@Override
		public String getCity() {
			return city;
		}

		@Override
		public State getState() {
			return state;
		}

		@Override
		public String getZip() {
			return zip;
		}

		@Override
		public Country getCountry() {
			return country;
		}
		
	}
	@Before
	public void setUp() throws Exception {
	}
	@Test
	public void testFullAddress_UnitedStates_Minimal() throws Exception {
		State california = new State("CA");
		Country usa = new Country("US");
		usa.setEnglish("United States");
		
		BasicAddress a = new BasicAddress();
		a.address = "1234 S. Main Street";
		a.city = "Anytown";
		a.state = california;
		a.zip = "99999";
		a.country = usa;
		
		// default base Country = US
		assertEquals("1234 S. Main Street<br/>Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"<br/>"));
		assertEquals("1234 S. Main Street\nAnytown, CA 99999", StreetAddressUtils.fullAddress(a,"\n"));
		assertEquals("1234 S. Main Street; Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"; "));
		
		// explicit base Country = US
		assertEquals("1234 S. Main Street<br/>Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"<br/>","US"));
		assertEquals("1234 S. Main Street\nAnytown, CA 99999", StreetAddressUtils.fullAddress(a,"\n","US"));
		assertEquals("1234 S. Main Street; Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"; ","US"));
		
		// base Country = UK, so show that country is US
		assertEquals("1234 S. Main Street<br/>Anytown, CA 99999<br/>United States", StreetAddressUtils.fullAddress(a,"<br/>","UK"));
		assertEquals("1234 S. Main Street\nAnytown, CA 99999\nUnited States", StreetAddressUtils.fullAddress(a,"\n","UK"));
		assertEquals("1234 S. Main Street; Anytown, CA 99999; United States", StreetAddressUtils.fullAddress(a,"; ","UK"));
		
	}
	@Test
	public void testFullAddress_UnitedStates_WithSuiteLine() throws Exception {
		State california = new State("CA");
		Country usa = new Country("US");
		usa.setEnglish("United States");
		
		BasicAddress a = new BasicAddress();
		a.address = "1234 S. Main Street";
		a.address2 = "Ste 567";
		a.city = "Anytown";
		a.state = california;
		a.zip = "99999";
		a.country = usa;
		
		// default base Country = US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"<br/>"));
		assertEquals("1234 S. Main Street\nSte 567\nAnytown, CA 99999", StreetAddressUtils.fullAddress(a,"\n"));
		assertEquals("1234 S. Main Street; Ste 567; Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"; "));
		
		// explicit base Country = US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"<br/>","US"));
		assertEquals("1234 S. Main Street\nSte 567\nAnytown, CA 99999", StreetAddressUtils.fullAddress(a,"\n","US"));
		assertEquals("1234 S. Main Street; Ste 567; Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"; ","US"));
		
		// base Country = UK, so show that country is US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>Anytown, CA 99999<br/>United States", StreetAddressUtils.fullAddress(a,"<br/>","UK"));
		assertEquals("1234 S. Main Street\nSte 567\nAnytown, CA 99999\nUnited States", StreetAddressUtils.fullAddress(a,"\n","UK"));
		assertEquals("1234 S. Main Street; Ste 567; Anytown, CA 99999; United States", StreetAddressUtils.fullAddress(a,"; ","UK"));
		
	}
	@Test
	public void testFullAddress_UnitedStates_WithSuiteLineAndPoBoxLine() throws Exception {
		State california = new State("CA");
		Country usa = new Country("US");
		usa.setEnglish("United States");
		
		BasicAddress a = new BasicAddress();
		a.address = "1234 S. Main Street";
		a.address2 = "Ste 567";
		a.address3 = "P.O. Box 8888";
		a.city = "Anytown";
		a.state = california;
		a.zip = "99999";
		a.country = usa;
		
		// default base Country = US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>P.O. Box 8888<br/>Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"<br/>"));
		assertEquals("1234 S. Main Street\nSte 567\nP.O. Box 8888\nAnytown, CA 99999", StreetAddressUtils.fullAddress(a,"\n"));
		assertEquals("1234 S. Main Street; Ste 567; P.O. Box 8888; Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"; "));
		
		// explicit base Country = US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>P.O. Box 8888<br/>Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"<br/>","US"));
		assertEquals("1234 S. Main Street\nSte 567\nP.O. Box 8888\nAnytown, CA 99999", StreetAddressUtils.fullAddress(a,"\n","US"));
		assertEquals("1234 S. Main Street; Ste 567; P.O. Box 8888; Anytown, CA 99999", StreetAddressUtils.fullAddress(a,"; ","US"));
		
		// base Country = UK, so show that country is US
		assertEquals("1234 S. Main Street<br/>Ste 567<br/>P.O. Box 8888<br/>Anytown, CA 99999<br/>United States", StreetAddressUtils.fullAddress(a,"<br/>","UK"));
		assertEquals("1234 S. Main Street\nSte 567\nP.O. Box 8888\nAnytown, CA 99999\nUnited States", StreetAddressUtils.fullAddress(a,"\n","UK"));
		assertEquals("1234 S. Main Street; Ste 567; P.O. Box 8888; Anytown, CA 99999; United States", StreetAddressUtils.fullAddress(a,"; ","UK"));
		
	}
	// FIXME We need more tests for other countries (especially UK, Canada, and UAE).  
	// FIXME For the UAE test, the State field needs to be left null.  See PICS-6193 (and reaffirm PICS-1030).
	// FIXME We need more tests for other combinations of missing fields, like when the zip code is null or blank.
}
