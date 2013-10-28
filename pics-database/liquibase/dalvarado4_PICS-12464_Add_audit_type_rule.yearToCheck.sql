--liquibase formatted sql

--changeset dalvarado:4
--preConditions onFail MARK_RAN

-- Add the yearToCheck column to enable a rule to check a specific year in the past.
ALTER TABLE `audit_type_rule`
ADD COLUMN `yearToCheck` TINYINT NOT NULL DEFAULT 0 AFTER `questionAnswer`;

ALTER TABLE `audit_category_rule`
ADD COLUMN `yearToCheck` TINYINT NOT NULL DEFAULT 0 AFTER `questionAnswer`;
