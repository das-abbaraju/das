package com.picsauditing.model.general;

import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.PhysicalAddress;

public class PhysicalAddressBean implements PhysicalAddress {
	public String address;
	public String address2;
	public String address3;
	public String city;
	public CountrySubdivision  countrySubdivision;
	public String zip;
	public Country country;
	
	public PhysicalAddressBean(String address, String address2, String address3, String city, CountrySubdivision countrySubdivision, String zip,
			Country country) {
		super();
		this.address = address;
		this.address2 = address2;
		this.address3 = address3;
		this.city = city;
		this.countrySubdivision = countrySubdivision;
		this.zip = zip;
		this.country = country;
	}

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
	public CountrySubdivision getCountrySubdivision() {
		return countrySubdivision;
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
