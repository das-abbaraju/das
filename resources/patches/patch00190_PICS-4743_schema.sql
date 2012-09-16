ALTER TABLE `contractor_info`
	CHANGE COLUMN `priority` `insideSalesPriority` VARCHAR(5) NULL DEFAULT NULL AFTER `welcomeAuditor_id`,
	CHANGE COLUMN `closedOnDate` `expiresOnDate` DATE NULL DEFAULT NULL AFTER `reasonForDecline`,
	ADD COLUMN `registrationHash` VARCHAR(100) NULL DEFAULT NULL AFTER `languageID`;

ALTER TABLE `contractor_info`
	DROP COLUMN `reasonForDecline`;