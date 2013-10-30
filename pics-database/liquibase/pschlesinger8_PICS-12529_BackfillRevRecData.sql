--liquibase formatted sql

--changeset pschlesinger:8
--  SQL provided by sshacter
--  --	Activation invoices
--	startDate := invoice creation date
--	endDate := startDate plus 1 year
UPDATE	invoice_item
,	invoice
SET
	invoice_item.startDate	= DATE(invoice.creationDate)
,	invoice_item.endDate	= DATE(DATE_ADD(invoice.creationDate, INTERVAL 1 YEAR))
WHERE	1=1
AND	invoice.invoiceType 	= "Activation"
AND	invoice.id 	= invoice_item.invoiceID
;
--	Renewal invoices
--	startDate := invoice creation date plus 1 month
--	endDate := startDate plus 1 year
UPDATE	invoice_item
,	invoice
SET
	invoice_item.startDate	= DATE(DATE_ADD(invoice.creationDate, INTERVAL 1 MONTH))
,	invoice_item.endDate	= DATE(DATE_ADD(DATE_ADD(invoice.creationDate, INTERVAL 1 MONTH), INTERVAL 1 YEAR))
WHERE	1=1
AND	invoice.invoiceType 	= "Renewal"
AND	invoice.id 	= invoice_item.invoiceID
;

--	Upgrade, Late Fee, Other Fees invoices
--	startDate := the invoice's creation date
--	endDate := Look for the most recent non-voided Activation or Renewal invoice *prior* to the invoice in question; use a non-null invoice_item endDate from that previous Activation / Renewal invoice as the endDate for the invoice in question
--	Set up temporary table first
DROP TEMPORARY TABLE IF EXISTS tmp;
CREATE TEMPORARY TABLE tmp
AS
SELECT
	invoice.id	invoiceID
,	recentInvoice.invoiceID	recentInvoiceID
,	recentInvoice.itemID	recentItemID
,	DATE(Invoice.creationDate) 	startDate
,	DATE(MAX(recentInvoice.endDate))	endDate
FROM
	invoice
JOIN
(
	SELECT
		invoice.creationDate	startDate
	,	invoice_item.endDate 	endDate
	,	invoice_item.id	itemID
	,	invoice.id	invoiceID
	,	invoice.accountID
	FROM
		invoice
	JOIN	invoice_item
	ON	invoice_item.invoiceID	= invoice.id
	WHERE	1=1
	AND	invoice.invoiceType	IN ("Renewal","Activation")
	ORDER BY
		invoice.creationDate	DESC
)	AS 	recentInvoice
ON 	recentInvoice.accountID 	= invoice.accountID
WHERE	1=1
AND	invoice.invoiceType	IN ("Upgrade","LateFee","OtherFees")
GROUP BY
	invoice.id
;

-- 	Update the start and end dates
UPDATE	invoice_item
,	invoice
,	tmp
SET
	invoice_item.startDate	= DATE(invoice.creationDate)
,	invoice_item.endDate	= DATE(tmp.endDate)
WHERE	1=1
AND	invoice.id 	= invoice_item.invoiceID
AND 	invoice.id	= tmp.invoiceID
AND	invoice.invoiceType	IN ("Upgrade","LateFee","OtherFees")	-- Technically not needed. Used here for clarity.
;
