-- Modify the accounts table
alter table `accounts` add column `sapLastSync` datetime NULL after `qbSync`;

-- Create new table to reference database for application
create table `ref_sap_business_unit`( `id` int(11) NOT NULL AUTO_INCREMENT , `businessUnit` varchar(25) NOT NULL , `testDatabase` varchar(25) NOT NULL , `liveDatabase` varchar(25) NOT NULL , PRIMARY KEY (`id`))  ;

-- Pre-populate some of the data in the reference table
insert into `ref_sap_business_unit`(`id`,`businessUnit`,`testDatabase`,`liveDatabase`)values('1','Undefined','Pics_Undefined','Pics_Undefined');
insert into `ref_sap_business_unit`(`id`,`businessUnit`,`testDatabase`,`liveDatabase`)values('2','US','Pics_Sandbox','Pics_Production');
insert into `ref_sap_business_unit`(`id`,`businessUnit`,`testDatabase`,`liveDatabase`)values('3','Canada','Pics_Sandbox_Canada','Pics_Production_Canada');
insert into `ref_sap_business_unit`(`id`,`businessUnit`,`testDatabase`,`liveDatabase`)values('4','UK_EU','Pics_Sandbox_UK_EU','Pics_Production_UK_EU');
insert into `ref_sap_business_unit`(`id`,`businessUnit`,`testDatabase`,`liveDatabase`)values('5','Brazil','Pics_Sandbox_Brazil','Pics_Production_Brazil');
insert into `ref_sap_business_unit`(`id`,`businessUnit`,`testDatabase`,`liveDatabase`)values('6','Singapore','Pics_Sandbox_Singapore','Pics_Production_Singapore');

-- Add in business unit id into ref_country
alter table `ref_country` add column `businessUnitID` int(8) DEFAULT '1' NOT NULL after `fax`;

-- Set the countries that should be syncing to SAP
update ref_country set businessUnitID = 2 where currency = 'USD';

-- update the invoice table
alter table `invoice` add column `sapLastSync` datetime NULL after `qbSync`;
alter table `invoice` add column `sapID` varchar(25) NULL after `sapLastSync`;