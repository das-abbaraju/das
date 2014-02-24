--liquibase formatted sql

--changeset jgriffith:1

INSERT INTO contractor_audit_operator_workflow
            (createdBy,
             updatedBy,
             creationDate,
             updateDate,
             caoID,
             STATUS,
             previousStatus,
             notes)
SELECT 38586, 38586, NOW(), NOW(), cao.id, 'Resubmit', 'Pending', '2013 Year end changes'
FROM contractor_audit_operator cao
JOIN contractor_audit ca ON cao.`auditID` = ca.`id`
WHERE 1 
AND cao.opID = 54071
AND ca.auditTypeID = 1
AND cao.status = 'Pending';

UPDATE contractor_audit_operator cao
JOIN contractor_audit ca ON cao.auditID = ca.id
SET cao.status = 'Resubmit'
WHERE 1 
AND cao.opID = 54071
AND ca.auditTypeID = 1
AND cao.status = 'Pending';
