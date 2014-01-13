--liquibase formatted sql

--changeset mdo:30
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE
invoice_fee_country ifc
JOIN ref_country rc ON ifc.country = rc.isoCode
JOIN temp_invoice_pricing_2014 tip ON ifc.feeID = tip.id
SET ifc.updatedBy = 37951, ifc.updateDate = NOW(), ifc.amount = CASE WHEN tip.feeClass = 'DocuGUARD' THEN 99 WHEN tip.minfacilities = 1 AND feeClass = 'InsureGUARD' THEN 49 WHEN tip.minfacilities = 2 AND feeClass = 'InsureGUARD' THEN 69 WHEN tip.minfacilities >= 5 AND feeClass = 'InsureGUARD' THEN 99 WHEN tip.minfacilities = 1 AND feeClass = 'AuditGUARD' THEN 399 WHEN tip.minfacilities = 2 AND feeClass = 'AuditGUARD' THEN 799 WHEN tip.minfacilities >= 5 AND feeClass = 'AuditGUARD' THEN 1199 WHEN tip.minfacilities = 1 AND feeClass = 'EmployeeGUARD' THEN 99 WHEN tip.minfacilities = 2 AND feeClass = 'EmployeeGUARD' THEN 199 WHEN tip.minfacilities >= 5 AND feeClass = 'EmployeeGUARD' THEN 299 ELSE 0 END
WHERE country IN ('AU');

UPDATE
invoice_fee_country ifc
JOIN ref_country rc ON ifc.country = rc.isoCode
JOIN temp_invoice_pricing_2014 tip ON ifc.feeID = tip.id
SET ifc.updatedBy = 37951, ifc.updateDate = NOW(), ifc.amount = CASE WHEN tip.feeClass = 'DocuGUARD' THEN 125 WHEN tip.minfacilities = 1 AND feeClass = 'InsureGUARD' THEN 55 WHEN tip.minfacilities = 2 AND feeClass = 'InsureGUARD' THEN 75 WHEN tip.minfacilities >= 5 AND feeClass = 'InsureGUARD' THEN 110 WHEN tip.minfacilities = 1 AND feeClass = 'AuditGUARD' THEN 450 WHEN tip.minfacilities = 2 AND feeClass = 'AuditGUARD' THEN 875 WHEN tip.minfacilities >= 5 AND feeClass = 'AuditGUARD' THEN 1300 WHEN tip.minfacilities = 1 AND feeClass = 'EmployeeGUARD' THEN 125 WHEN tip.minfacilities = 2 AND feeClass = 'EmployeeGUARD' THEN 225 WHEN tip.minfacilities >= 5 AND feeClass = 'EmployeeGUARD' THEN 325 ELSE 0 END
WHERE country IN ('NZ');
