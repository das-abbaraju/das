-- Currency
UPDATE ref_country
SET currency = 'DKK'
WHERE isoCode = 'DK';
UPDATE ref_country
SET currency = 'NOK'
WHERE isoCode = 'NO';
UPDATE ref_country
SET currency = 'SEK'
WHERE isoCode = 'SE';

-- DocuGUARD 1-50 Op
UPDATE invoice_fee_country
SET amount = 900
WHERE feeID IN (302,303,304,305,306,307,308) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 950
WHERE feeID IN (302,303,304,305,306,307,308) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 1050
WHERE feeID IN (302,303,304,305,306,307,308) AND country = 'SE';

-- InsureGUARD 1 Op
UPDATE invoice_fee_country
SET amount = 450
WHERE feeID IN (310) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 450
WHERE feeID IN (310) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 500
WHERE feeID IN (310) AND country = 'SE';

-- InsureGUARD 2-4 Op
UPDATE invoice_fee_country
SET amount = 600
WHERE feeID IN (311) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 650
WHERE feeID IN (311) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 750
WHERE feeID IN (311) AND country = 'SE';

-- InsureGUARD 5-8 Op
UPDATE invoice_fee_country
SET amount = 900
WHERE feeID IN (312) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 950
WHERE feeID IN (312) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 1050
WHERE feeID IN (312) AND country = 'SE';

-- InsureGUARD 9-12 Op
UPDATE invoice_fee_country
SET amount = 1350
WHERE feeID IN (313) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 1400
WHERE feeID IN (313) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 1600
WHERE feeID IN (313) AND country = 'SE';

-- InsureGUARD 13+ Op
UPDATE invoice_fee_country
SET amount = 1800
WHERE feeID IN (314,315,316) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 1900
WHERE feeID IN (314,315,316) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 2100
WHERE feeID IN (314,315,316) AND country = 'SE';

-- AuditGUARD 1 Op
UPDATE invoice_fee_country
SET amount = 2700
WHERE feeID IN (318) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 2800
WHERE feeID IN (318) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 3150
WHERE feeID IN (318) AND country = 'SE';

-- AuditGUARD 2-4 Op
UPDATE invoice_fee_country
SET amount = 5400
WHERE feeID IN (319) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 5650
WHERE feeID IN (319) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 6300
WHERE feeID IN (319) AND country = 'SE';

-- AuditGUARD 5-8 Op
UPDATE invoice_fee_country
SET amount = 8100
WHERE feeID IN (320) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 8450
WHERE feeID IN (320) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 9450
WHERE feeID IN (320) AND country = 'SE';

-- AuditGUARD 9-12 Op
UPDATE invoice_fee_country
SET amount = 10800
WHERE feeID IN (321) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 11300
WHERE feeID IN (321) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 12600
WHERE feeID IN (321) AND country = 'SE';

-- AuditGUARD 13+ Op
UPDATE invoice_fee_country
SET amount = 14500
WHERE feeID IN (322,323,324) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 15050
WHERE feeID IN (322,323,324) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 16800
WHERE feeID IN (322,323,324) AND country = 'SE';

-- EmployeeGUARD 1 Op
UPDATE invoice_fee_country
SET amount = 900
WHERE feeID IN (326) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 950
WHERE feeID IN (326) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 1050
WHERE feeID IN (326) AND country = 'SE';

-- EmployeeGUARD 2-4 Op
UPDATE invoice_fee_country
SET amount = 1800
WHERE feeID IN (327) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 1900
WHERE feeID IN (327) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 2100
WHERE feeID IN (327) AND country = 'SE';

-- EmployeeGUARD 5-8 Op
UPDATE invoice_fee_country
SET amount = 2700
WHERE feeID IN (328) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 2800
WHERE feeID IN (328) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 3150
WHERE feeID IN (328) AND country = 'SE';

-- EmployeeGUARD 9-12 Op
UPDATE invoice_fee_country
SET amount = 3600
WHERE feeID IN (329) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 3750
WHERE feeID IN (329) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 4200
WHERE feeID IN (329) AND country = 'SE';

-- EmployeeGUARD 13+ Op
UPDATE invoice_fee_country
SET amount = 5400
WHERE feeID IN (330,331,332) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 5650
WHERE feeID IN (330,331,332) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 6300
WHERE feeID IN (330,331,332) AND country = 'SE';

-- One Time Activation Fee
UPDATE invoice_fee_country
SET amount = 1800
WHERE feeID IN (333) AND country = 'DK';
UPDATE invoice_fee_country
SET amount = 1900
WHERE feeID IN (333) AND country = 'NO';
UPDATE invoice_fee_country
SET amount = 2100
WHERE feeID IN (333) AND country = 'SE';

