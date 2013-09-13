ALTER TABLE `audit_category`
ADD COLUMN `effectiveDate` DATE NOT NULL DEFAULT '2000-01-01';

ALTER TABLE `audit_category`
ADD COLUMN `expirationDate` DATE NOT NULL DEFAULT '4000-01-01';
