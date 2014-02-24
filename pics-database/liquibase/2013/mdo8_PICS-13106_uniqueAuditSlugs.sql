--liquibase formatted sql

--changeset mdo:8
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
ALTER TABLE `audit_type`
  ADD  UNIQUE INDEX `akAudit_Type` (`slug`);

ALTER TABLE `audit_question`
  ADD  UNIQUE INDEX `akAudit_Question` (`slug`);
