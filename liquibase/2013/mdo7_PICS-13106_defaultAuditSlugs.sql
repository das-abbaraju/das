--liquibase formatted sql

--changeset mdo:7
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE audit_type
SET slug = MD5(id);

UPDATE audit_question
SET slug = MD5(id);
