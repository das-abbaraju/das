-- Remove non-US, non-Canadian countrySubdivisions from accounts and contractor_info
update accounts set countrySubdivision = null where country not in ("US","CA");
update contractor_info set billingCountrySubdivision = null where billingCountry not in ("US","CA");

-- This breaks things, so need to hold off on this until another ticket
-- delete from ref_country_subdivision where countryCode not in ("US","CA");

