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
  333,
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
FROM invoice_fee_country ifc
JOIN ref_country rc ON ifc.country = rc.isoCode
WHERE rc.currency IN ('CAD','EUR','GBP','USD');
