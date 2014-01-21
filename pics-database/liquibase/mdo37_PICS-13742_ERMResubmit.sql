--liquibase formatted sql

--changeset mdo:37
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
INSERT into contractor_audit_operator_workflow (caoID, status, previousStatus, notes, createdBy, creationDate, updateDate, updatedBy)
SELECT cao.id, 'Resubmit' newStatus, cao.status oldStatus, '2013 Year end changes' note, 1 createdBy, NOW() creationDate, NOW() updateDate, 1 updatedBy
FROM contractor_audit_operator cao
JOIN contractor_audit ca ON ca.id = cao.auditID
JOIN audit_type audt ON ca.auditTypeID = audt.id AND audt.id = 339
WHERE cao.status IN('Resubmitted','Complete')
;

UPDATE contractor_audit_operator cao
JOIN contractor_audit ca ON ca.id = cao.auditID
JOIN audit_type audt ON ca.auditTypeID = audt.id AND audt.id = 339
SET cao.status = 'Resubmit', cao.statusChangedDate = '2014-01-01', cao.updateDate = '2013-12-31 23:59:59', cao.updatedBy = 1, ca.expiresDate = '2014-03-15 12:59:59'
WHERE cao.status IN ('Resubmitted','Complete') ;
