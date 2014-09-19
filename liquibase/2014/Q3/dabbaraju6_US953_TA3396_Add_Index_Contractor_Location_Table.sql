--liquibase formatted sql

--changeset dabbaraju:6
--preConditions onFail MARK_RAN

-- Below script to add index on conId to improve query performance.

ALTER TABLE Contractor_location ADD INDEX ix_contractor_location_conId (conId);