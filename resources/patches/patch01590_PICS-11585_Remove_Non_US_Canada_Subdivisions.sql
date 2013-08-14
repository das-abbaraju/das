-- Remove non-US, non-Canadian countrySubdivisions
update accounts set countrySubdivision = null where countrySubdivision not like ("US-%") and countrySubdivision not like ("CA-%");
update contractor_info set billingCountrySubdivision = null where billingCountrySubdivision not like ("US-%") and billingCountrySubdivision not like ("CA-%");

update contractor_registration_request set countrySubdivision = null where countrySubdivision not like "US-%" and countrySubdivision not like "CA-%";

update contractor_audit set countrySubdivision = null where countrySubdivision not like "US-%" and countrySubdivision not like "CA-%";

update email_queue_missing_contractors set billingCountrySubdivision = null where billingCountrySubdivision not like "US-%" and billingCountrySubdivision not like "CA-%";

update invoice_fee_country set subdivision = null where subdivision not like "US-%" and subdivision not like "CA-%";

update job_site set countrySubdivision = null where countrySubdivision not like "US-%" and countrySubdivision not like "CA-%";

update user_assignment  set countrySubdivision = null where countrySubdivision not like "US-%" and countrySubdivision not like "CA-%";


delete from ref_country_subdivision where countryCode not in ("US","CA");

