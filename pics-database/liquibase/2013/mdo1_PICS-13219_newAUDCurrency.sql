--liquibase formatted sql

--changeset mdo:1
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
-- Currency
UPDATE ref_country SET currency = 'AUD' WHERE isoCode IN ('AU','NZ');

-- DocuGUARD 1-50 Op
UPDATE invoice_fee_country SET amount = 99 WHERE feeID IN (302,303,304,305,306,307,308) AND country IN ('AU','NZ');

-- InsureGUARD 1 Op
UPDATE invoice_fee_country SET amount = 49 WHERE feeID IN (310) AND country IN ('AU','NZ');

-- InsureGUARD 2-4 Op
UPDATE invoice_fee_country SET amount = 69 WHERE feeID IN (311) AND country IN ('AU','NZ');

-- InsureGUARD 5-8 Op
UPDATE invoice_fee_country SET amount = 99 WHERE feeID IN (312) AND country IN ('AU','NZ');

-- InsureGUARD 9-12 Op
UPDATE invoice_fee_country SET amount = 149 WHERE feeID IN (313) AND country IN ('AU','NZ');

-- InsureGUARD 13+ Op
UPDATE invoice_fee_country SET amount = 199 WHERE feeID IN (314,315,316) AND country IN ('AU','NZ');

-- AuditGUARD 1 Op
UPDATE invoice_fee_country SET amount = 399 WHERE feeID IN (318) AND country IN ('AU','NZ');

-- AuditGUARD 2-4 Op
UPDATE invoice_fee_country SET amount = 799 WHERE feeID IN (319) AND country IN ('AU','NZ');

-- AuditGUARD 5-8 Op
UPDATE invoice_fee_country SET amount = 1199 WHERE feeID IN (320) AND country IN ('AU','NZ');

-- AuditGUARD 9-12 Op
UPDATE invoice_fee_country SET amount = 1499 WHERE feeID IN (321) AND country IN ('AU','NZ');

-- AuditGUARD 13+ Op
UPDATE invoice_fee_country SET amount = 1999 WHERE feeID IN (322,323,324) AND country IN ('AU','NZ');

-- EmployeeGUARD 1 Op
UPDATE invoice_fee_country SET amount = 99 WHERE feeID IN (326) AND country IN ('AU','NZ');

-- EmployeeGUARD 2-4 Op
UPDATE invoice_fee_country SET amount = 199 WHERE feeID IN (327) AND country IN ('AU','NZ');

-- EmployeeGUARD 5-8 Op
UPDATE invoice_fee_country SET amount = 299 WHERE feeID IN (328) AND country IN ('AU','NZ');

-- EmployeeGUARD 9-12 Op
UPDATE invoice_fee_country SET amount = 399 WHERE feeID IN (329) AND country IN ('AU','NZ');

-- EmployeeGUARD 13+ Op
UPDATE invoice_fee_country SET amount = 599 WHERE feeID IN (330,331,332) AND country IN ('AU','NZ');

-- One Time Activation Fee
UPDATE invoice_fee_country SET amount = 199 WHERE feeID IN (333) AND country IN ('AU','NZ');
