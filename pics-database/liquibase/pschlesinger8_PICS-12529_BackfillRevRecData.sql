--liquibase formatted sql

--changeset pschlesinger:8
--  SQL provided by sshacter
--  --	Activation invoices
--	startDate := invoice creation date
--	endDate := startDate plus 1 year
UPDATE	invoice_item,	invoice,	invoice_fee
SET invoice_item.revenueStartDate	= NULL,	invoice_item.revenueFinishDate	= NULL
WHERE	invoice.id 	= invoice_item.invoiceID
AND	invoice_item.feeID	= invoice_fee.id
AND invoice.creationDate >= '2013-12-01'
;

--	*********************************************************************************
--	Activation invoices
--	startDate := invoice creation date
--	endDate := startDate plus 1 year minus 1 day
--	*********************************************************************************
SET	@rsd	:= "0000-00-00";
UPDATE	invoice_item,	invoice,	invoice_fee
SET invoice_item.revenueStartDate	= @rsd	:= IFNULL(DATE(invoice.dueDate),DATE(invoice.creationDate))
,	invoice_item.revenueFinishDate	= DATE(DATE_ADD(DATE_ADD(@rsd, INTERVAL 1 YEAR), INTERVAL -1 DAY))
WHERE	invoice.invoiceType 	= "Activation"
AND	invoice_fee.feeClass	NOT IN ('GST','VAT','CanadianTax','Free')
AND	invoice.id 	= invoice_item.invoiceID
AND	invoice_item.feeID	= invoice_fee.id
AND invoice.creationDate >= '2013-12-01'
;

--	*********************************************************************************
--	Renewal invoices
--	startDate := invoice creation date plus 1 month
--	endDate := startDate plus 1 year minus 1 day
--	*********************************************************************************
SET	@rsd	:= "0000-00-00";
UPDATE	invoice_item,	invoice,	invoice_fee
SET invoice_item.revenueStartDate	= @rsd	:= IFNULL(DATE(invoice.dueDate),DATE(DATE_ADD(invoice.creationDate, INTERVAL 1 MONTH)))
,	invoice_item.revenueFinishDate	= DATE(DATE_ADD(DATE_ADD(@rsd, INTERVAL 1 YEAR), INTERVAL -1 DAY))
WHERE	invoice.invoiceType 	= "Renewal"
AND	invoice_fee.feeClass	NOT IN ('GST','VAT','CanadianTax','Free')
AND	invoice.id 	= invoice_item.invoiceID
AND	invoice_item.feeID	= invoice_fee.id
AND invoice.creationDate >= '2013-12-01'
;

--	*********************************************************************************
--	Upgrade, Late Fee, Other Fees invoices:
--	startDate := the invoice's creation date
--	endDate := Look for the most recent non-voided Activation or Renewal invoice *prior* to the invoice in question;
--	use a non-null invoice_item endDate from that previous Activation / Renewal invoice as the endDate for the invoice in question
--	Set up temporary table first
--	*********************************************************************************
DROP TABLE IF EXISTS	primo
;

CREATE TABLE IF NOT EXISTS	primo
AS
SELECT invoice.accountID
,	invoice.id	invoiceID
,	invoice.invoiceType
,	invoice_fee.feeClass
,	invoice.status	invoiceStatus
,	DATE(invoice.creationDate)	invoiceCreateDate
,	invoice_item.id	invoiceItemID
,	invoice_item.revenueStartDate 	revenueStartDate
,	invoice_item.revenueFinishDate 	revenueFinishDate
FROM invoice
JOIN invoice_item ON	invoice_item.invoiceID	= invoice.id
JOIN invoice_fee ON	invoice_fee.id	= invoice_item.feeID
WHERE	invoice.id
AND	invoice.invoiceType	IN ("Renewal","Activation")
AND	invoice.status	NOT IN ("Void")
AND invoice.creationDate >= '2013-12-01'
;

--
--	update DML cannot contain a group by clause so create an (in memory) table for use in the update
--
DROP TEMPORARY TABLE IF EXISTS	secondo
;

CREATE TEMPORARY TABLE IF NOT EXISTS	secondo
AS
SELECT invoice.accountID
,	invoice.id	invoiceID
,	invoice_item.id	invoiceItemID
,	invoice_item.creationDate
,	prior.revenueFinishDate	priorRevenueFinishDate
FROM invoice
JOIN invoice_item ON	invoice_item.invoiceID	= invoice.id
JOIN invoice_fee ON	invoice_fee.id	= invoice_item.feeID
JOIN (
	SELECT
		primo.accountID
	,	primo.invoiceID
	,	MAX(primo.invoiceCreateDate)	invoiceCreateDate
	,	MAX(primo.revenueFinishDate)	revenueFinishDate
	FROM
		primo
	GROUP BY
		primo.invoiceID
)	prior
ON	invoice.accountID	= prior.accountID
WHERE	1=1
AND	invoice.invoiceType	IN ("Upgrade","LateFee","OtherFees")
AND	invoice_fee.feeClass	NOT IN ('GST','VAT','CanadianTax','Free')
AND	invoice_item.creationDate	>= prior.invoiceCreateDate
AND	invoice_item.creationDate	<= prior.revenueFinishDate
AND invoice.creationDate >= '2013-12-01'
GROUP BY
	invoice.id	ASC
,	invoice_item.id
;

--	---------------------------------------------------------------------------------
--	update the Upgrade, OtherFee or LateFees invoices
--	---------------------------------------------------------------------------------
UPDATE
	invoice_item
JOIN
	secondo
ON	secondo.invoiceID	= invoice_item.invoiceID
AND	secondo.invoiceItemID	= invoice_item.id
SET
	invoice_item.revenueStartDate	= DATE(secondo.creationDate)
,	invoice_item.revenueFinishDate	= DATE(secondo.priorRevenueFinishDate)
WHERE	1=1
;

--	*********************************************************************************
--	Check some invoices...
--	*********************************************************************************
DELIMITER	|
;
SELECT
	invoice.accountID
,	accounts.name	accountName
,	invoice_item.invoiceID
,	invoice.invoiceType
,	invoice.status	invoiceStatus
,	invoice.creationDate	invoiceCreateDate
,	invoice_fee.feeClass
,	invoice_item.id	invoiceItemID
,	invoice_item.revenueStartDate
,	invoice_item.revenueFinishDate
FROM
	invoice
JOIN
	accounts
ON	accounts.id	= invoice.accountID
JOIN
	invoice_item
ON	invoice_item.invoiceID	= invoice.id
JOIN
	invoice_fee
ON	invoice_fee.id	= invoice_item.feeID
WHERE	1=1
AND	invoice.accountID	IN
(
	22540
,	24090
,	15133
,	15230
,	17151
,	17042
,	17161
,	17261
,	17658
,	17899
,	18619
,	18829
,	18845
,	18940
,	19382
,	19638
,	19800
,	19803
,	20055
,	21079
,	21105
,	21373
,	21909
,	22930
,	22932
,	23410
,	23877
,	23976
,	18940
,	21838
,	23187
,	17328
,	23289
)
ORDER BY
	invoice.accountID	ASC
,	invoice.creationDate	ASC
;

--	double check
SELECT
	invoice.accountID
,	invoice.invoiceType
,	invoice.status
,	invoice.creationDate	invoiceCreate
,	invoice_fee.feeClass
,	invoice_item.id	invoiceItemID
,	invoice_item.invoiceID
,	invoice_item.revenueStartDate
,	invoice_item.revenueFinishDate, DATEDIFF(revenueFinishDate, revenueStartDate)
FROM
	invoice
JOIN
	invoice_item
ON	invoice_item.invoiceID	= invoice.id
JOIN
	invoice_fee
ON	invoice_fee.id	= invoice_item.feeID
WHERE	1=1
AND	invoice.invoiceType	IN ("Renewal","Activation","Upgrade","LateFee","OtherFees")
AND	invoice.status	NOT IN ("Void")
AND	invoice_fee.feeClass	NOT IN ('GST','VAT','CanadianTax','Free')
AND invoice.creationDate >= '2013-12-01'
ORDER BY DATEDIFF(revenueFinishDate, revenueStartDate) DESC
;
