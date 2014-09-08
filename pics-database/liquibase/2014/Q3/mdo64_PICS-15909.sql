--liquibase formatted sql

--changeset mdo:64
INSERT INTO invoice_fee (id,fee,defaultAmount,feeClass,qbFullName,createdBy,updatedBy,creationDate,updateDate)
VALUES (364,'Activation Fee for 0 Operators','119.00','Activation','MRKT-ACT',37951,37951,NOW(),NOW()),
(365,'Reactivation Fee for 0 Operators','119.00','Reactivation','MRKT-REACT',37951,37951,NOW(),NOW());

INSERT INTO invoice_fee_country (feeID,country,amount,createdBy,updatedBy,creationDate,updateDate,effectiveDate,expirationDate)
VALUES
(364,'GB','69.00',37951,37951,NOW(),NOW(),'1970-01-01 00:00:00','4000-01-01 23:59:59'),
(365,'GB','69.00',37951,37951,NOW(),NOW(),'1970-01-01 00:00:00','4000-01-01 23:59:59'),
(364,'US','119.00',37951,37951,NOW(),NOW(),'1970-01-01 00:00:00','4000-01-01 23:59:59'),
(365,'US','119.00',37951,37951,NOW(),NOW(),'1970-01-01 00:00:00','4000-01-01 23:59:59'),
(364,'CA','119.00',37951,37951,NOW(),NOW(),'1970-01-01 00:00:00','4000-01-01 23:59:59'),
(365,'CA','119.00',37951,37951,NOW(),NOW(),'1970-01-01 00:00:00','4000-01-01 23:59:59');

UPDATE invoice_fee
SET minFacilities = -1, maxFacilities = -1
WHERE id = 341;

UPDATE invoice_fee
SET minFacilities = 1
WHERE id = 334;
