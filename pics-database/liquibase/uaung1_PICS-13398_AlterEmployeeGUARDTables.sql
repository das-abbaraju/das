--liquibase formatted sql

--changeset uaung:1
ALTER TABLE `project`
	ALTER `NAME` DROP DEFAULT;
ALTER TABLE `project`
	CHANGE COLUMN `NAME` `name` VARCHAR(50) NOT NULL AFTER `accountID`,
	CHANGE COLUMN `location` `location` VARCHAR(50) NULL DEFAULT NULL AFTER `name`;

INSERT INTO accountemployeeguard (`accountID`)
VALUES (54578, 55653, 55654);

DROP TABLE IF EXISTS `site_account_skill`;

CREATE TABLE `site_account_skill` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `siteID` int(11) NOT NULL,
  `skillID` int(11) NOT NULL,
  `createdBy` int(11) NOT NULL,
  `updatedBy` int(11) DEFAULT '0',
  `deletedBy` int(11) DEFAULT '0',
  `createdDate` datetime NOT NULL,
  `updatedDate` datetime DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ak1_site_account_skill` (`siteID`,`skillID`),
  KEY `fk2_site_account_skill` (`skillID`),
  CONSTRAINT `fk1_site_account_skill` FOREIGN KEY (`siteID`) REFERENCES `accountemployeeguard` (`accountID`),
  CONSTRAINT `fk2_site_account_skill` FOREIGN KEY (`skillID`) REFERENCES `account_skill` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;