--liquibase formatted sql

--changeset mdo:60
UPDATE contractor_audit_file
SET reviewed = 1
WHERE reviewed = 0
AND creationDate < '2014-07-08'
;