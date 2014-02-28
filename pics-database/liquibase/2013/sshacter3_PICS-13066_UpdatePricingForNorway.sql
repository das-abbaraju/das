--liquibase formatted sql
--changeset sshacter:3

--	PICS-13066_UpdatePricingForNorway

--	DocuGUARD
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 1000.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "DocuGUARD"
;

--	InsureGUARD
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 500.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "InsureGUARD"
AND	invoice_fee.minFacilities	= 1
AND	invoice_fee.maxFacilities	= 1
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 700.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "InsureGUARD"
AND	invoice_fee.minFacilities	= 2
AND	invoice_fee.maxFacilities	= 4
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 1000.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "InsureGUARD"
AND	invoice_fee.minFacilities	= 5
AND	invoice_fee.maxFacilities	= 8
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 1500.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "InsureGUARD"
AND	invoice_fee.minFacilities	= 9
AND	invoice_fee.maxFacilities	= 12
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 2000.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "InsureGUARD"
AND	invoice_fee.minFacilities	>= 13
;

--	AuditGUARD
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 2950.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "AuditGUARD"
AND	invoice_fee.minFacilities	= 1
AND	invoice_fee.maxFacilities	= 1
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 5850.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "AuditGUARD"
AND	invoice_fee.minFacilities	= 2
AND	invoice_fee.maxFacilities	= 4
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 8850.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "AuditGUARD"
AND	invoice_fee.minFacilities	= 5
AND	invoice_fee.maxFacilities	= 8
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 11750.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "AuditGUARD"
AND	invoice_fee.minFacilities	= 9
AND	invoice_fee.maxFacilities	= 12
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 15700.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "AuditGUARD"
AND	invoice_fee.minFacilities	>= 13
;

--	EmployeeGUARD
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 1000.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "EmployeeGUARD"
AND	invoice_fee.minFacilities	= 1
AND	invoice_fee.maxFacilities	= 1
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 2000.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "EmployeeGUARD"
AND	invoice_fee.minFacilities	= 2
AND	invoice_fee.maxFacilities	= 4
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 2950.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "EmployeeGUARD"
AND	invoice_fee.minFacilities	= 5
AND	invoice_fee.maxFacilities	= 8
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 3900.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "EmployeeGUARD"
AND	invoice_fee.minFacilities	= 9
AND	invoice_fee.maxFacilities	= 12
;
UPDATE	invoice_fee_country
JOIN
	invoice_fee
ON	invoice_fee.id 	= invoice_fee_country.feeID
SET
	invoice_fee_country.amount	= 5900.00
,	invoice_fee_country.updatedBy	= 90574
,	invoice_fee_country.updateDate	= "2013-10-03"
WHERE	1=1
AND	invoice_fee_country.country 	= "NO"
AND	invoice_fee.feeClass	= "EmployeeGUARD"
AND	invoice_fee.minFacilities	>= 13
;

