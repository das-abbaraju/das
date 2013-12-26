--liquibase formatted sql

--changeset blatner:1

ALTER TABLE contractor_fee

  ADD COLUMN currentFacilityCount int(11) default 0 not null
, ADD COLUMN newFacilityCount int(11) default 0 not null
;
