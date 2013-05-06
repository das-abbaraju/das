-- Better values for null
update invoice_fee_country
	set effectiveDate = '1970-01-01'
	where effectiveDate is null;
	
-- Encourage better dates
alter table `invoice_fee_country` 
	change `effectiveDate` `effectiveDate` 
	datetime default '1970-01-01 00:00:00' NOT NULL;

-- add expiration date
alter table `invoice_fee_country` 
	add column `expirationDate` 
	datetime DEFAULT '4000-01-01 23:59:59' NOT NULL after `effectiveDate`;
