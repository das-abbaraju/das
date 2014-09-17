--liquibase formatted sql

--changeset mdo:6
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
ALTER TABLE `audit_type`
  ADD COLUMN `slug` VARCHAR(64) NOT NULL  COMMENT 'Unique Audit Type Code' AFTER `id`;

ALTER TABLE `audit_question`
  ADD COLUMN `slug` VARCHAR(64) NOT NULL  COMMENT 'Unique Audit Question Code' AFTER `id`;

