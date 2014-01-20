--liquibase formatted sql

--changeset mdo:33
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE invoice_item
SET revenueFinishDate = '2014-10-30'
WHERE invoiceid = 266085