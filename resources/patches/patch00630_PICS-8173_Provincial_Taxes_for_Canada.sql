-- Add ratePercent and effectiveDate columns to invoice_fee table
ALTER TABLE `invoice_fee`
ADD COLUMN `ratePercent` decimal(6,3) NULL default 0 after `defaultAmount`,
ADD COLUMN `effectiveDate` DATETIME NULL after `updateDate`;

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

-- Add an invoice_fee for each type of Canadian province tax.  Do not modify the exising row for GST fee(id=200).
insert into `invoice_fee`
(`id`, `fee`, `defaultAmount`, `ratePercent`, `visible`, `feeClass`, `minFacilities`, `maxFacilities`, `qbFullName`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `effectiveDate`, `displayOrder`, `commissionEligible`)
 values
('210','GST','0','5','1','CanadianTax','-1','-1','GST','79689','79689',now(),now(),'2012-04-01 00:00:00','100','0'),
('211','GST Plus PST','0','5','1','CanadianTax','-1','-1','GST Plus PST','79689','79689',now(),now(),null,'100','0'),
('212','GST Plus QST','0','5','1','CanadianTax','-1','-1','GST Plus QST','79689','79689',now(),now(),null,'100','0'),
('213','HST','0','5','1','CanadianTax','-1','-1','HST','79689','79689',now(),now(),NULL,'100','0');

-- inserts into app_translation have already been run on config

-- Add an invoice_fee_country row for each Canadian province pointing to the applicable invoice_fee.
-- Note that British Columbia and Prince Edward Island have an additional row for changes effective Apr 1, 2013.
insert into `invoice_fee_country`
(`feeID`, `country`, `subdivision`, `amount`, `ratePercent`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`, `effectiveDate`)
values
('210','CA','CA-AB','0','0','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-BC','0','7','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('211','CA','CA-BC','0','7','20952','20952',now(),NOW(),'2013-04-01 00:00:00'),
('211','CA','CA-MB','0','7','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-NB','0','8','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-NL','0','8','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('210','CA','CA-NT','0','0','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-NS','0','10','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('210','CA','CA-NU','0','0','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-ON','0','8','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('211','CA','CA-PE','0','10.5','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('213','CA','CA-PE','0','9','20952','20952',now(),NOW(),'2013-04-01 00:00:00'),
('212','CA','CA-QC','0','9.975','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('211','CA','CA-SK','0','5','20952','20952',now(),NOW(),'2012-04-01 00:00:00'),
('210','CA','CA-YT','0','0','20952','20952',now(),NOW(),'2012-04-01 00:00:00');

