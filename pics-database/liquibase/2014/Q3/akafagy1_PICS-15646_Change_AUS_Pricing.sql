--liquibase formatted sql

--changeset akafagy:1

-- backup data
CREATE TEMPORARY TABLE temp_invoice_fee_aud_nzd AS
SELECT DISTINCT rc.isoCode, rc.currency, fee.fee, fee.minFacilities, fee.maxFacilities, ifc.amount, ifc.`country`
FROM ref_country rc
JOIN invoice_fee_country ifc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
WHERE rc.isoCode IN ('AU', 'NZ')
AND rc.currency IN ('AUD', 'NZD');

UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 199.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities = 1
AND fee.maxFacilities = 1
AND fee.fee LIKE 'DocuGUARD%';

UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 199.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities = 2
AND fee.maxFacilities = 4
AND fee.fee LIKE 'DocuGUARD%';

UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 199.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities >= 5
AND fee.maxFacilities > 4
AND fee.fee LIKE 'DocuGUARD%';


UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 99.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities = 1
AND fee.maxFacilities = 1
AND fee.fee LIKE 'InsureGUARD%';

UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 199.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities = 2
AND fee.maxFacilities = 4
AND fee.fee LIKE 'InsureGUARD%';

UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 399.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities >= 5
AND fee.maxFacilities > 4
AND fee.fee LIKE 'InsureGUARD%';


UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 499.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities = 1
AND fee.maxFacilities = 1
AND fee.fee LIKE 'AuditGUARD%';

UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 999.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities = 2
AND fee.maxFacilities = 4
AND fee.fee LIKE 'AuditGUARD%';

UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 1999.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities >= 5
AND fee.maxFacilities > 4
AND fee.fee LIKE 'AuditGUARD%';


UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 199.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities = 1
AND fee.maxFacilities = 1
AND fee.fee LIKE 'EmployeeGUARD%';

UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 399.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities = 2
AND fee.maxFacilities = 4
AND fee.fee LIKE 'EmployeeGUARD%';

UPDATE invoice_fee_country ifc
JOIN ref_country rc ON rc.isoCode = ifc.country
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 799.00
WHERE rc.isoCode = 'AU'
AND rc.currency = 'AUD'
AND fee.minFacilities >= 5
AND fee.maxFacilities > 4
AND fee.fee LIKE 'EmployeeGUARD%';



UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 220.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities = 1
      AND fee.maxFacilities = 1
      AND fee.fee LIKE 'DocuGUARD%';

UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 220.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities = 2
      AND fee.maxFacilities = 4
      AND fee.fee LIKE 'DocuGUARD%';

UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 220.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities >= 5
      AND fee.maxFacilities > 4
      AND fee.fee LIKE 'DocuGUARD%';


UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 110.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities = 1
      AND fee.maxFacilities = 1
      AND fee.fee LIKE 'InsureGUARD%';

UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 220.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities = 2
      AND fee.maxFacilities = 4
      AND fee.fee LIKE 'InsureGUARD%';

UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 440.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities >= 5
      AND fee.maxFacilities > 4
      AND fee.fee LIKE 'InsureGUARD%';


UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 550.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities = 1
      AND fee.maxFacilities = 1
      AND fee.fee LIKE 'AuditGUARD%';

UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 1100.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities = 2
      AND fee.maxFacilities = 4
      AND fee.fee LIKE 'AuditGUARD%';

UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 2200.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities >= 5
      AND fee.maxFacilities > 4
      AND fee.fee LIKE 'AuditGUARD%';


UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 220.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities = 1
      AND fee.maxFacilities = 1
      AND fee.fee LIKE 'EmployeeGUARD%';

UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 440.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities = 2
      AND fee.maxFacilities = 4
      AND fee.fee LIKE 'EmployeeGUARD%';

UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 880.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.minFacilities >= 5
      AND fee.maxFacilities > 4
      AND fee.fee LIKE 'EmployeeGUARD%';


UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 220.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.fee LIKE 'Reactivation Fee%';

UPDATE invoice_fee_country ifc
  JOIN ref_country rc ON rc.isoCode = ifc.country
  JOIN invoice_fee fee ON ifc.feeID = fee.id
SET amount = 220.00
WHERE rc.isoCode = 'NZ'
      AND rc.currency = 'NZD'
      AND fee.fee LIKE 'One-time Activation Fee%';