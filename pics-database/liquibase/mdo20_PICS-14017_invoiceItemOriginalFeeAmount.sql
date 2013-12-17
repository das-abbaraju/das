--liquibase formatted sql

--changeset mdo:20
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
ALTER TABLE `pics_alpha1`.`invoice_item`
  ADD COLUMN `originalAmount` DECIMAL(9,2) DEFAULT 0.00  NOT NULL AFTER `amount`;

update accounts a
JOIN invoice i ON a.id = i.accountID
JOIN invoice_item ii ON i.id = ii.invoiceID
JOIN invoice_fee f ON ii.feeID = f.id
LEFT JOIN invoice_fee_country fc ON f.id = fc.feeID AND a.country = fc.country
SET ii.originalAmount = CASE WHEN fc.amount IS NULL THEN f.defaultAmount ELSE fc.amount END;