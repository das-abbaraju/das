UPDATE IGNORE ref_country
SET businessUnitID = CASE WHEN currency = 'CAD' THEN 3 WHEN currency IN ('GBP','EUR','DKK','NOK','SEK','ZAR') THEN 4 END
WHERE businessUnitID != 2;
