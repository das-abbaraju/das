--liquibase formatted sql

--changeset mdo:22
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
INSERT INTO invoice_fee_country (feeID,country,subdivision,amount,ratePercent,createdBy,updatedBy,creationDate,updateDate,effectiveDate,expirationDate)
SELECT 334,country,subdivision,amount,ratePercent,37951,37951,NOW(),NOW(),effectiveDate,expirationDate
FROM invoice_fee_country
WHERE feeID = 333;