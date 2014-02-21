--liquibase formatted sql

--changeset lkam:6
--preConditions onFail MARK_RAN

ALTER TABLE pics_alpha1.contractor_info ADD COLUMN logoForSingleOperatorContractor INT(11) DEFAULT NULL NULL AFTER autoAddClientSite;
