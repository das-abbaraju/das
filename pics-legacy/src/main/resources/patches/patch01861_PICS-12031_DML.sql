CREATE TEMPORARY TABLE temp_commission_calc AS
SELECT i.id, i.accountID, GROUP_CONCAT(ii.amount SEPARATOR ' + ') AS commissionMath, SUM(ii.amount) AS commissionSum, GROUP_CONCAT(f.fee) AS fees, f.commissionEligible FROM invoice i
JOIN invoice_item ii ON i.id = ii.invoiceID
JOIN invoice_fee f ON ii.feeID = f.id 
WHERE f.commissionEligible = 1
GROUP BY i.id
HAVING commissionSum > 0;

CREATE TEMPORARY TABLE temp_invoice_type_calc AS
SELECT i.id, i.accountID, 
CASE 
	WHEN GROUP_CONCAT(f.fee) LIKE '%Activation%' OR GROUP_CONCAT(f.fee) LIKE '%List Only%' OR GROUP_CONCAT(f.fee) LIKE '%Bid Only%' THEN 'Activation' 
	WHEN GROUP_CONCAT(ii.description) LIKE '%Prorated%' OR GROUP_CONCAT(ii.description) LIKE '%Membership Upgrade%' THEN 'Upgrade' 
	WHEN GROUP_CONCAT(f.fee) LIKE '%GUARD%' OR GROUP_CONCAT(f.fee) LIKE '%PICS Membership%' THEN 'Renewal'
	ELSE 'OtherFees' 
END AS invoiceType, 
GROUP_CONCAT(f.fee) AS fees FROM 
invoice i
JOIN invoice_item ii ON i.id = ii.invoiceID
JOIN invoice_fee f ON ii.feeID = f.id 
GROUP BY i.id DESC;


UPDATE IGNORE invoice i
JOIN temp_commission_calc tcc ON i.id = tcc.id
SET i.commissionableAmount = tcc.commissionSum;

UPDATE IGNORE invoice i
JOIN temp_invoice_type_calc titc ON i.id = titc.id
SET i.invoiceType = titc.invoiceType;