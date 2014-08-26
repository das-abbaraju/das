--liquibase formatted sql

--changeset mdo:60
UPDATE contractor_audit ca
JOIN contractor_audit_operator cao ON ca.id = cao.auditID
JOIN contractor_audit_file caf ON ca.id = caf.auditID
SET reviewed = 1
WHERE caf.reviewed = 0
AND cao.status = 'Complete'
AND caf.creationDate < '2014-07-30'
;