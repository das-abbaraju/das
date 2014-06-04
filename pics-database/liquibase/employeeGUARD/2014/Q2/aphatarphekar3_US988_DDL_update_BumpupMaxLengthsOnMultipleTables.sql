--liquibase formatted sql

--changeset aphatarphekar:3
--preConditions onFail MARK_RAN

ALTER TABLE `account_skill`
CHANGE COLUMN `name` `name` VARCHAR(100) NOT NULL COMMENT 'Skill name for this type and account' ,
CHANGE COLUMN `description` `description` VARCHAR(1500) NULL COMMENT 'Skill description' ;

ALTER TABLE `account_group`
CHANGE COLUMN `name` `name` VARCHAR(100) NOT NULL COMMENT 'Group name for this type and account' ,
CHANGE COLUMN `description` `description` VARCHAR(1500) NULL COMMENT 'Group description' ;

ALTER TABLE `project`
CHANGE COLUMN `name` `name` VARCHAR(100) NOT NULL ,
CHANGE COLUMN `location` `location` VARCHAR(100) NULL ;

ALTER TABLE `account_employee`
CHANGE COLUMN `firstName` `firstName` VARCHAR(100) NOT NULL COMMENT 'Employee first name' ,
CHANGE COLUMN `lastName` `lastName` VARCHAR(100) NOT NULL COMMENT 'Employee last name' ,
CHANGE COLUMN `positionName` `positionName` VARCHAR(100) NULL COMMENT 'The position job title' ,
CHANGE COLUMN `slug` `slug` VARCHAR(100) NULL COMMENT 'Unique employee code' ,
CHANGE COLUMN `email` `email` VARCHAR(100) NOT NULL COMMENT 'The employee email address' ;

ALTER TABLE `profiledocument`
CHANGE COLUMN `name` `name` VARCHAR(100) NOT NULL COMMENT 'Document display (friendly) name' ;
