--liquibase formatted sql

--changeset mdo:5
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
-- Remove the bad My Schedule timeslots for Samuel Duckworth-Essilfie which are not removeable from the UI due to bug in our calendar plugin.
ALTER TABLE `ref_country`
  ADD COLUMN `csrAddress2` VARCHAR(50) NULL AFTER `csrAddress`,
  ADD COLUMN `csrCountry` VARCHAR(10) NULL AFTER `csrCity`;
