--liquibase formatted sql

--changeset mdo:15
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE invoice
JOIN invoice_item ON invoice_item.invoiceID = invoice.id
JOIN accounts ON accounts.id = invoice.accountID
JOIN invoice_fee ON invoice_item.feeID = invoice_fee.id
SET invoice.updatedBy = 37951, invoice.updateDate = NOW(), invoice.invoiceType = 'Activation'
WHERE accounts.status NOT IN ('Demo','Declined')
AND invoice.status != 'Void'
AND invoice.invoiceType = 'Renewal'
AND invoice_fee.feeClass = 'Activation'
AND invoice.creationDate > '2011-11-27';