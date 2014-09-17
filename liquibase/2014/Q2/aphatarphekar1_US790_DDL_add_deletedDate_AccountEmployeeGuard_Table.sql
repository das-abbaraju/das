--liquibase formatted sql

--changeset aphatarphekar:1
--preConditions onFail MARK_RAN

ALTER TABLE `accountemployeeguard`
ADD COLUMN `deletedDate` DATETIME NULL DEFAULT NULL AFTER `accountID`,
ADD COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT FIRST,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`),
ADD UNIQUE INDEX `accountID_UNIQUE` (`accountID` ASC);