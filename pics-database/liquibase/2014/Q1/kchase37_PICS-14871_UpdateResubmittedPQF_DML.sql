--liquibase formatted sql

--changeset kchase:37

-- Update cao status
UPDATE contractor_audit_operator cao
JOIN temp_caos_to_update temp ON cao.id = temp.caoID
SET updatedBy = 1, updateDate = NOW(), cao.status = 'Incomplete', cao.statusChangedDate = NOW();

-- Insert workflows
INSERT INTO contractor_audit_operator_workflow (createdBy,updatedBy,creationDate,updateDate,caoID,STATUS,previousStatus,notes)
SELECT DISTINCT 1,1,NOW(),NOW(),caoID,'Incomplete',caoStatus,'Bulk Update of PQF for end of grace period'
FROM temp_caos_to_update;
