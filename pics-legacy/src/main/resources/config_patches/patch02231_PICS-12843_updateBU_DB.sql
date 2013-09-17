UPDATE IGNORE ref_sap_business_unit
SET testDatabase = 'Pics_Undefined', liveDatabase = 'Pics_Undefined'
WHERE businessUnit != 'US';
