INSERT IGNORE INTO invoice_fee(fee,defaultAmount,ratePercent,visible,feeClass,minFacilities,maxFacilities,qbFullName,createdBy,updatedBy,creationDate,updateDate,effectiveDate,displayOrder,commissionEligible)
SELECT 'Discount',defaultAmount,ratePercent,visible,feeClass,minFacilities,maxFacilities,qbFullName,37951,37951,NOW(),NOW(),effectiveDate,displayOrder,1
FROM invoice_fee
WHERE fee = 'Other'