-- Example patch for simply inserting required reference data
-- Note: For this kind of thing, always use INSERT into (rather than 
-- REPLACE into) since we wouldn't want to upset any existing definitions that may already exist.

insert ignore into app_translation (msgKey, locale, msgValue) values 
    ("zzzz.autopatch.000030","en",   "This is a black AutoPatch test message."),
    ("zzzz.autopatch.000030","en_CA","This is a black AutoPatch test message."),
    ("zzzz.autopatch.000030","en_GB","This is a black AutoPatch test message.");
