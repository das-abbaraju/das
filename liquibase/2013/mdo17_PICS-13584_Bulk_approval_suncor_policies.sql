--liquibase formatted sql

--changeset mdo:17
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
DROP TABLE IF EXISTS temp_bulk_approve_suncor_insurance_policies;

CREATE TABLE temp_bulk_approve_suncor_insurance_policies AS
SELECT a.id AS accountID, a.name, ca.id auditID, ca.auditTypeID, ca.creationDate createdDate, ca.expiresDate, cao.id AS caoID, cao.status auditStatus, cao.statusChangedDate
FROM accounts a
JOIN contractor_audit ca ON ca.conID = a.id
JOIN audit_type atype ON atype.id = ca.auditTypeID
JOIN contractor_audit_operator cao ON cao.auditID = ca.id
LEFT JOIN contractor_audit_operator_workflow caow ON cao.id = caow.caoID
WHERE a.type='Contractor'
AND (ca.expiresDate IS NULL OR ca.expiresDate < '2013-12-01')
AND cao.status IN ('Complete')
AND cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (10566,10850,13266,13267,13265,16574,16575,16576,16577,16578,16714,17065,17066,3716,17886,17887,16808,20186,21985,47720))
AND atype.classType = 'Policy'
AND cao.visible = 1
AND a.status IN ('Active','Demo')
GROUP BY cao.id
ORDER BY cao.status DESC, cao.updateDate ASC;

UPDATE contractor_audit_operator cao
JOIN temp_bulk_approve_suncor_insurance_policies tbasip ON cao.id = tbasip.caoID
SET updatedBy = 1, updateDate = NOW(), cao.status = 'Approved', cao.statusChangedDate = NOW();

INSERT INTO contractor_audit_operator_workflow (createdBy,updatedBy,creationDate,updateDate,caoID,STATUS,previousStatus,notes)
SELECT DISTINCT 1,1,NOW(),NOW(),caoID,'Approved',auditStatus,'Bulk Approval 2013-12-11'
FROM temp_bulk_approve_suncor_insurance_policies;
