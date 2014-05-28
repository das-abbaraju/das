--liquibase formatted sql

--changeset aananighian:6
CREATE TABLE IF NOT EXISTS `account_skill_profile` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Row Number',
  `skillID` int(11) NOT NULL COMMENT 'Unique Skill ID from account_skill',
  `profileID` int(11) NOT NULL COMMENT 'Unique Profile ID from profile',
  `documentID` int(11) NULL COMMENT 'Document ID from profileDocument table',
  `startDate` datetime NOT NULL COMMENT 'The date an employee started a skill for this account',
  `finishDate` datetime DEFAULT NULL COMMENT 'The date the skill will expire for this employee for this account',
  `createdBy` int(11) NOT NULL COMMENT 'The user ID that created the row',
  `updatedBy` int(11) DEFAULT NULL COMMENT 'The user ID that updated the row',
  `deletedBy` int(11) DEFAULT NULL COMMENT 'The user ID that deleted the row',
  `createdDate` datetime NOT NULL COMMENT 'The date the row was created',
  `updatedDate` datetime DEFAULT NULL COMMENT 'The date the row was updated',
  `deletedDate` datetime DEFAULT NULL COMMENT 'The date the row was deleted',
  PRIMARY KEY (`id`),
  UNIQUE KEY `akAccount_Skill_Profile` (`skillID`,`profileID`),
  KEY `fk2_account_skill_profile` (`profileID`),
  KEY `fk3_account_skill_profile` (`documentID`),
  CONSTRAINT `fk1_account_skill_profile` FOREIGN KEY (`skillID`) REFERENCES `account_skill` (`id`),
  CONSTRAINT `fk2_account_skill_profile` FOREIGN KEY (`profileID`) REFERENCES `profile` (`id`),
  CONSTRAINT `fk3_account_skill_profile` FOREIGN KEY (`documentID`) REFERENCES `profiledocument` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=678 DEFAULT CHARSET=utf8;
