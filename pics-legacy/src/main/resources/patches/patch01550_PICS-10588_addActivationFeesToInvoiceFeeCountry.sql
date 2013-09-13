INSERT IGNORE INTO invoice_fee_country
            (feeID,
             country,
             subdivision,
             amount,
             ratePercent,
             createdBy,
             updatedBy,
             creationDate,
             updateDate,
             effectiveDate,
             expirationDate)
SELECT
  ifc.feeID,
  rc.isoCode,
  ifc.subdivision,
  ifc.amount,
  ifc.ratePercent,
  37951,
  37951,
  NOW(),
  NOW(),
  ifc.effectiveDate,
  ifc.expirationDate
FROM ref_country rc 
LEFT JOIN invoice_fee_country ifc ON ifc.id = 18043
WHERE rc.currency IN ('CAD','EUR','GBP','USD');
