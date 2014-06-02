--liquibase formatted sql

--changeset mdo:51

ALTER TABLE flag_criteria_operator
  DROP COLUMN affected;
