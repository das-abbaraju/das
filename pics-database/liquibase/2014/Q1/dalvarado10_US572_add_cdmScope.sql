--liquibase formatted sql

--changeset dalvarado:10
--preConditions onFail MARK_RAN

ALTER TABLE contractor_certificate ADD COLUMN cdmScope VARCHAR(200) NULL AFTER certificationMethod;
