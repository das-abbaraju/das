DROP TABLE IF EXISTS temp_invoice_type_calc;
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
LEFT JOIN invoice_item ii ON i.id = ii.invoiceID
LEFT JOIN invoice_fee f ON ii.feeID = f.id
WHERE tableType = 'I'
GROUP BY i.id DESC;

UPDATE invoice i
JOIN temp_invoice_type_calc titc ON i.id = titc.id
SET i.invoiceType = titc.invoiceType;
