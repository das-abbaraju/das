--liquibase formatted sql

--changeset pschlesinger:8
--	Activation invoices
UPDATE	invoice_item
,	invoice
SET
invoice_item.revenueStartDate	= DATE(invoice.creationDate)
,	invoice_item.revenueFinishDate	= DATE(DATE_ADD(invoice.creationDate, INTERVAL 1 YEAR))
WHERE	1=1
AND	invoice.invoiceType = "Activation"
AND	invoice.id = invoice_item.invoiceID
;

--	Renewal invoices
UPDATE	invoice_item
,	invoice
SET
invoice_item.revenueStartDate	= DATE(DATE_ADD(invoice.creationDate, INTERVAL 1 MONTH))
,	invoice_item.revenueFinishDate	= DATE(DATE_ADD(DATE_ADD(invoice.creationDate, INTERVAL 1 MONTH), INTERVAL 1 YEAR))
WHERE	1=1
AND	invoice.invoiceType = "Renewal"
AND	invoice.id = invoice_item.invoiceID
