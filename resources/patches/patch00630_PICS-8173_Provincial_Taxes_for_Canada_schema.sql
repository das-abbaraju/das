-- Fix the size of the invoice_fee_country.country column or adding a FK later on will fail. country is a FK to ref_country.isoCode and should be varchar(10), not varchar(25).
alter table `invoice_fee_country`
modify `country` varchar(10) NOT NULL;

-- Add subdivision, ratePercent and effectiveDate columns to invoice_fee table
ALTER TABLE `invoice_fee_country`
ADD COLUMN `subdivision` varchar(10) NULL after `country`,
ADD COLUMN `ratePercent` decimal(6,3) NULL default 0 after `amount`,
ADD COLUMN `effectiveDate` DATETIME NULL after `updateDate`;

-- Update the composite unique index to include subdivision and effectiveDate.
ALTER TABLE `invoice_fee_country`
DROP INDEX countryFee,
ADD UNIQUE INDEX feeCountrySubdivision (feeID, country, subdivision, effectiveDate);
