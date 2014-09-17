--liquibase formatted sql

--changeset mdo:54
ALTER TABLE flag_criteria
  DROP COLUMN label,
  DROP COLUMN description;
