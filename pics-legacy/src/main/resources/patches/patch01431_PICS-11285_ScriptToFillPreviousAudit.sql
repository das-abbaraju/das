DROP TABLE IF EXISTS temp_previous_audit;

CREATE TEMPORARY TABLE temp_previous_audit AS
SELECT ca.id AS audit1, pre.id AS audit2, ca.`auditTypeID`, ca.`expiresDate` AS expires1, pre.`expiresDate` AS expires2 FROM contractor_audit ca
JOIN audit_type a ON a.id = ca.`auditTypeID` AND a.`renewable` = 0 AND (a.`hasMultiple` = 0 OR a.id = 11)
JOIN contractor_audit pre ON pre.id = (SELECT t.id FROM contractor_audit t WHERE ca.`conID` = t.`conID` AND ca.`auditTypeID` = t.`auditTypeID` AND t.`creationDate` < ca.`creationDate` ORDER BY t.`creationDate` DESC LIMIT 1);

UPDATE contractor_audit ca
JOIN temp_previous_audit tpa ON ca.`id` = tpa.audit1
SET ca.`previousAuditID` = tpa.audit2;
