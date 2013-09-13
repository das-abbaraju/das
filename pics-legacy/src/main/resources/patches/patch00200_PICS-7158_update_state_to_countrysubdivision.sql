update audit_question a join ref_country_subdivision b on substr(b.isoCode, 4) = a.uniqueCode
set uniqueCode = b.isoCode