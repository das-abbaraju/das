package com.picsauditing.jpa.entities;

/**
 * For any object that describes a physical location (e.g. that an auditor can
 * visit or package can be shipped to).
 * 
 * TODO This could work for ContractorAudit as well, except that ContractorAudit
 * would have to change the return types for getCountrySubdivision() and getCountry(), and it
 * would have to add getAddress2() & getAddress3().
 * 
 */
public interface PhysicalAddress {

	public String getAddress();

	public String getAddress2();

	public String getAddress3();

	public String getCity();

	public CountrySubdivision getCountrySubdivision();

	public String getZip();

	public Country getCountry();

}