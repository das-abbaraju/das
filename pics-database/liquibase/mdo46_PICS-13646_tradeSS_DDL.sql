--liquibase formatted sql

--changeset mdo:46
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
DROP VIEW IF EXISTS vwaccounttradesafetysensitivity;

ALTER TABLE contractor_info
  ADD COLUMN tradeSafetySensitive TINYINT(4) DEFAULT 0  NOT NULL AFTER insideSalesPriority,
  ADD COLUMN tradeSafetyRisk TINYINT(3) DEFAULT 0  NULL AFTER tradeSafetySensitive;
