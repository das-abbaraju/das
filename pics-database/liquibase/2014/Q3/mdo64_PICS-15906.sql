--liquibase formatted sql

--changeset mdo:64
UPDATE contractor_operator
SET forceFlag = NULL, forceBegin = NULL, forceEnd = NULL
WHERE id = 604708