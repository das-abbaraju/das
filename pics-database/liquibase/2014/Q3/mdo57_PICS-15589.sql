--liquibase formatted sql

--changeset mdo:57
ALTER TABLE report
  ADD COLUMN slug VARCHAR(100) NULL AFTER modelType;
