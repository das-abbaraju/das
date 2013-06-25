-- Remove non-US, non-Canadian countrySubdivisions
update accounts set countrySubdivision = null where country not in ("US","CA");
update contractor_info set billingCountrySubdivision = null where billingCountry not in ("US","CA");
delete from ref_country_subdivision where countryCode not in ("US","CA");

