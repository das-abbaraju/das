--liquibase formatted sql

--changeset mdo:16
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE ref_country
SET currency = 'NZD'
WHERE isoCode = 'NZ';

UPDATE
invoice_fee_country ifc
JOIN invoice_fee fee ON ifc.feeID = fee.id
SET ifc.updatedBy = 37951, ifc.updateDate = NOW(), ifc.amount = CASE WHEN fee.feeClass = 'DocuGUARD' THEN 225 WHEN fee.feeClass = 'InsureGUARD' AND fee.minFacilities = 1 THEN 125 WHEN fee.feeClass = 'InsureGUARD' AND fee.minFacilities = 2 THEN 225 WHEN fee.feeClass = 'InsureGUARD' THEN 450 WHEN fee.feeClass = 'AuditGUARD' AND fee.minFacilities = 1 THEN 575 WHEN fee.feeClass = 'AuditGUARD' AND fee.minFacilities = 2 THEN 1125 WHEN fee.feeClass = 'AuditGUARD' THEN 2250 WHEN fee.feeClass = 'EmployeeGUARD' AND fee.minFacilities = 1 THEN 225 WHEN fee.feeClass = 'EmployeeGUARD' AND fee.minFacilities = 2 THEN 450 WHEN fee.feeClass = 'EmployeeGUARD' THEN 900 WHEN fee.feeClass = 'Activation' THEN 225 ELSE 0 END
WHERE country = 'NZ';

INSERT IGNORE INTO app_properties (property, VALUE, description)
VALUES ('brainTree.processor_id.nzd','picsnzd','New Zealand Braintree Code');
