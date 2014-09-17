--liquibase formatted sql

--changeset aphatarphekar:1
--preConditions onFail MARK_RAN

ALTER TABLE `profile`
ADD COLUMN `locale` VARCHAR(30) NOT NULL DEFAULT 'en_GB' AFTER `phone`;

