--liquibase formatted sql

--changeset mdo:19
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
ALTER TABLE `invoice`
  ADD COLUMN `payingFacilities` INT(11) DEFAULT 0 NOT NULL AFTER `qbSyncWithTax`;

UPDATE invoice i
JOIN (SELECT a.id, a.name, c.payingFacilities, ir.id AS invoiceID FROM
accounts a
JOIN contractor_info c ON a.id = c.id
JOIN invoice ir ON a.id = ir.accountID
AND ir.invoiceType IN ('Renewal','Upgrade','Activation')
AND ir.creationDate = (SELECT MAX(creationDate) FROM invoice i WHERE a.id = i.accountID AND i.invoiceType IN ('Renewal','Upgrade','Activation'))
) t ON i.id = t.invoiceID
SET i.payingFacilities = t.payingFacilities;