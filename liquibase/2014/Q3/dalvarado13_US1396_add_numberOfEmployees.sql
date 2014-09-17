--liquibase formatted sql

--changeset dalvarado:13
--preConditions onFail MARK_RAN

-- Add numberOfEmployees for the contractor. Null indicates that we don't know the number, probably because the
-- contractor has not yet provided this information.

ALTER TABLE contractor_info
ADD COLUMN numberOfEmployees INT DEFAULT NULL AFTER hasEmployeeGuard;
