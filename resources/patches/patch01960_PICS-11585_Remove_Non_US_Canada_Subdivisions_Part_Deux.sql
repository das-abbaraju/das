BEGIN;
-- Remove non-US, non-Canadian countrySubdivisions from accounts and contractor_info
update
  accounts
set
  countrySubdivision = null
where
  (countrySubdivision not like ("US-%")
   and countrySubdivision not like ("CA-%"))
  or
  country not in ('US','CA');

update
  contractor_info
set
  billingCountrySubdivision = null
where
  (billingCountrySubdivision not like ("US-%")
  and billingCountrySubdivision not like ("CA-%"))
  or
  billingCountry not in ('US','CA');

update
  contractor_registration_request
set
  countrySubdivision = null
where
  (countrySubdivision not like "US-%"
  and countrySubdivision not like "CA-%")
  or
  country not in ('US','CA');

update
  contractor_audit
set
  countrySubdivision = null
where
  (countrySubdivision not like "US-%"
  and countrySubdivision not like "CA-%")
  or
  country not in ('US','CA');

update
  email_queue_missing_contractors
set
  billingCountrySubdivision = null
where
  (billingCountrySubdivision not like "US-%"
   and billingCountrySubdivision not like "CA-%")
   or
   billingCountry not in ('US','CA');

update
  invoice_fee_country
set
  subdivision = null
where
  (subdivision not like "US-%"
  and
  subdivision not like "CA-%")
or country not in ('US','CA');

update
  job_site
set
  countrySubdivision = null
where
  (countrySubdivision not like "US-%"
  and countrySubdivision not like "CA-%")
or
country not in ('US','CA');

update
  user_assignment
set
  countrySubdivision = null
where
  (countrySubdivision not like "US-%"
   and countrySubdivision not like "CA-%")
or
country not in ('US','CA');

-- now fix US subdivisions which need to be countries
update
  accounts
set
  country = substr(countrySubdivision,4),countrySubdivision = null
where
countrySubdivision in ('US-AS','US-GU','US-MP','US-PR','US-UM','US-VI');

update
  contractor_info
set
  billingCountry = substr(billingCountrySubdivision,4),billingCountrySubdivision = null
where
  billingCountrySubdivision in ('US-AS','US-GU','US-MP','US-PR','US-UM','US-VI');

update
  contractor_registration_request
set
  country = substr(countrySubdivision,4),countrySubdivision = null
where
  countrySubdivision in ('US-AS','US-GU','US-MP','US-PR','US-UM','US-VI');

update
  contractor_audit
set
  country = substr(countrySubdivision,4),countrySubdivision = null
where
  countrySubdivision in ('US-AS','US-GU','US-MP','US-PR','US-UM','US-VI');

update
  email_queue_missing_contractors
set
  billingCountry = substr(billingCountrySubdivision,4),billingCountrySubdivision = null
where
  billingCountrySubdivision in ('US-AS','US-GU','US-MP','US-PR','US-UM','US-VI');

update
  invoice_fee_country
set
  country = substr(subdivision,4),subdivision = null
where
  subdivision in ('US-AS','US-GU','US-MP','US-PR','US-UM','US-VI');

update
  job_site
set
  country = substr(countrySubdivision,4),countrySubdivision = null
where
  countrySubdivision in ('US-AS','US-GU','US-MP','US-PR','US-UM','US-VI');

update
  user_assignment
set
  country = substr(countrySubdivision,4),countrySubdivision = null
where
  countrySubdivision in ('US-AS','US-GU','US-MP','US-PR','US-UM','US-VI');


delete from ref_country_subdivision where countryCode not in ("US","CA");

COMMIT;
