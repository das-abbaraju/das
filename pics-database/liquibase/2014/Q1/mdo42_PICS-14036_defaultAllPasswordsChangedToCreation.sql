--liquibase formatted sql

--changeset mdo:42
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE users
SET passwordChanged = creationDate
WHERE creationDate IS NOT NULL
AND passwordChanged IS NULL;
