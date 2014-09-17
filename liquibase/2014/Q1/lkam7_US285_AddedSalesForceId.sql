--liquibase formatted sql

--changeset lkam:7
--preConditions onFail MARK_RAN

ALTER TABLE contractor_info ADD COLUMN salesRepSalesForceID VARCHAR(50) NULL AFTER autoAddClientSite;
