-- Remove non-US states and non-Canadian provinces from ref_country_subdivision
DELETE FROM
ref_country_subdivision
where
isoCode not like "CA-%"
and
    isoCode not like "US-%";


DELETE FROM
ref_country_subdivision
WHERE
isoCode in ('US-AS','US-GU','US-MP','US-PR','US-UM','US-VI');
