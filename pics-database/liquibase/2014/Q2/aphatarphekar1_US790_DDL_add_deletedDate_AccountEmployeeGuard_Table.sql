--liquibase formatted sql

--changeset aphatarphekar:1
--preConditions onFail MARK_RAN

ALTER TABLE `accountemployeeguard` ADD COLUMN `deletedDate` DATETIME NULL DEFAULT NULL AFTER `accountID`;

