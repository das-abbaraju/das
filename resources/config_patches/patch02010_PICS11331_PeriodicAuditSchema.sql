-- Add schema support for monthly, quarterly, annual updates
alter table `audit_type` add column `period` varchar(25) DEFAULT 'None' NOT NULL;
alter table `audit_type` add column `anchorDay` tinyint(3) DEFAULT '1' NOT NULL;
alter table `audit_type` add column `anchorMonth` tinyint(3) DEFAULT '1' NOT NULL;
alter table `audit_type` add column `advanceDays` SMALLINT(4) DEFAULT '0' NOT NULL;
alter table `audit_type` add column `maximumActive` tinyint(3) DEFAULT '1' NOT NULL;
