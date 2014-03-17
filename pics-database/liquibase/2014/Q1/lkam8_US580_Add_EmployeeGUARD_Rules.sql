--liquibase formatted sql

--changeset lkam:8
--preConditions onFail MARK_RAN
ALTER TABLE contractor_info
ADD COLUMN hasEmployeeGuard TINYINT(4) NOT NULL DEFAULT '0' AFTER logoForSingleOperatorContractor;

ALTER TABLE operators
ADD COLUMN requiresEmployeeGuard TINYINT(4) NOT NULL DEFAULT '0' AFTER salesForceID;
