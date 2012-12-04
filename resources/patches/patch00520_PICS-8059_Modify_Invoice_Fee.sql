ALTER TABLE `invoice_fee`
ADD COLUMN `commissionEligible` bool DEFAULT 0 NOT NULL;

-- Update the invoice_fee table so we know which fees are considered
-- for Sales Commission.
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 53;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 302;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 303;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 304;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 305;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 306;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 307;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 308;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 310;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 311;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 312;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 313;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 314;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 315;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 316;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 318;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 319;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 320;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 321;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 322;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 323;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 324;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 326;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 327;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 328;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 329;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 330;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 331;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 332;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 333;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 341;
UPDATE invoice_fee SET commissionEligible = 1 WHERE id = 342;
