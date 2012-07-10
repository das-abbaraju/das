-- Example patch for simply inserting required reference data
-- Note: For this kind of thing, always use INSERT into (rather than 
-- REPLACE into) since we wouldn't want to upset any existing definitions that may already exist.

insert ignore into ref_state (isoCode, countryCode, english, french) values 
	('GB_ZZ','GB','Nosuch Place','');