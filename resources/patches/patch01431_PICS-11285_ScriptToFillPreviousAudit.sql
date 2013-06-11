DROP TABLE IF EXISTS temp_previous_audit;

CREATE TEMPORARY TABLE temp_previous_audit AS
SELECT a.`name` accountName, ca.id AS audit1, ca2.id AS audit2, ca.`auditTypeID`, ca.`expiresDate` AS expires1, ca2.`expiresDate` AS expires2 FROM 
accounts a
JOIN contractor_audit ca ON a.id = ca.`conID` AND ca.id = (SELECT MAX(ca3.id) FROM contractor_audit ca3 WHERE ca.`conID` = ca3.`conID` AND ca.`auditTypeID` = ca3.`auditTypeID`)
JOIN contractor_audit ca2 ON ca.`conID` = ca2.`conID` AND ca.`auditTypeID` = ca2.`auditTypeID` AND ca.id > ca2.`id`  AND ca2.id = (SELECT MAX(ca4.id) FROM contractor_audit ca4 WHERE ca.`conID` = ca4.`conID` AND ca.`auditTypeID` = ca4.`auditTypeID` AND ca.id > ca4.`id`)
JOIN audit_type audt ON ca.`auditTypeID` = audt.`id` AND audt.`renewable` = 0 AND audt.`hasMultiple` = 0
ORDER BY a.id, ca.`auditTypeID`, ca.id, ca2.id;

UPDATE contractor_audit ca
JOIN temp_previous_audit tpa ON ca.`id` = tpa.audit1
SET ca.`previousAuditID` = tpa.audit2;
