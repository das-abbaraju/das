--liquibase formatted sql

--changeset mdo:43
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE invoice i
JOIN invoice_item ii ON i.id = ii.invoiceID
SET revenueFinishDate = revenueStartDate
WHERE revenueStartDate > revenueFinishDate
AND tableType = 'C';
