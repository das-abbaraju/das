--liquibase formatted sql

--changeset blatner:1

ALTER TABLE contractor_fee

  ADD COLUMN currentFacilityCount int(11)
, ADD COLUMN newFacilityCount int(11)
;
