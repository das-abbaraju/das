--liquibase formatted sql

--changeset mdo:14
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE
invoice i
JOIN accounts a ON i.accountID = a.id
JOIN ref_country rc ON a.country = rc.isoCode
SET i.currency = rc.currency, i.updatedBy = 37951, i.updateDate = NOW(), i.qbSync = 1
WHERE i.invoiceType = 'LateFee'
AND a.status NOT IN ('Demo','Declined')
AND i.status != 'Void'
AND rc.currency != i.currency
AND a.type = 'Contractor'
AND i.id NOT IN (252584,253880,251326,253054,249574);

UPDATE
invoice i
SET i.currency = 'CAD', updatedBy = 37951, updateDate = NOW(), qbSync = 1
WHERE i.id IN (255409,255130,259656);
