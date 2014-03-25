--liquibase formatted sql

--changeset kchase:35

-- Determine appropriate PQFs and CAOs to update
DROP TABLE IF EXISTS temp_caos_to_update;

CREATE TABLE temp_caos_to_update AS
SELECT cao.id as caoID, cao.status as caoStatus from contractor_audit as ca
join contractor_audit_operator as cao on cao.`auditID` = ca.id
where ca.auditTypeID=1
and ca.`expiresDate` > NOW()
and cao.visible = 1
and cao.status in ('Resubmitted', 'Resubmitted')
;

-- Update cao status
UPDATE contractor_audit_operator cao
JOIN temp_caos_to_update temp ON cao.id = temp.caoID
SET updatedBy = 1, updateDate = NOW(), cao.status = 'Incomplete', cao.statusChangedDate = NOW();

-- Insert workflows
INSERT INTO contractor_audit_operator_workflow (createdBy,updatedBy,creationDate,updateDate,caoID,STATUS,previousStatus,notes)
SELECT DISTINCT 1,1,NOW(),NOW(),caoID,'Incomplete',caoStatus,'Bulk Update of PQF for end of grace period'
FROM temp_caos_to_update;