-- alter the tables
ALTER TABLE `accounts` 
	ADD COLUMN `deactivationDate` datetime NULL AFTER `updateDate`,
	ADD COLUMN `deactivatedBy` int(11) NULL AFTER `deactivationDate`;
