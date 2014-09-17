--liquibase formatted sql

--changeset mdo:28
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE contractor_audit
SET expiresDate = NULL
WHERE auditTypeID = 87 AND expiresDate > NOW()