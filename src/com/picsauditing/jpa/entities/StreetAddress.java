package com.picsauditing.jpa.entities;

/**
 * 
 * This coould work for ContractorAudit as well, except that ContractorAudit
 * would have to change the return types for getState() and getCountry(), and it
 * would have to add getAddress2() & getAddress3().
 * 
 */
public interface StreetAddress {

	public String getAddress();

	public String getAddress2();

	public String getAddress3();

	public String getCity();

	public State getState();

	public String getZip();

	public Country getCountry();

}