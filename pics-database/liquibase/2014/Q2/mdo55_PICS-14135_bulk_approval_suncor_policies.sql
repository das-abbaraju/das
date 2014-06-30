--liquibase formatted sql

--changeset mdo:55
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
DROP TABLE IF EXISTS temp_bulk_approve_suncor_insurance_policies;

CREATE TABLE temp_bulk_approve_suncor_insurance_policies AS
SELECT DISTINCT a.id AS accountID, a.name, ca.id auditID, ca.auditTypeID, ca.creationDate createdDate, ca.expiresDate, cao.id AS caoID, cao.status auditStatus, cao.statusChangedDate
FROM accounts a
JOIN contractor_info c ON a.id = c.id
JOIN contractor_audit ca ON ca.conID = a.id
JOIN audit_type atype ON atype.id = ca.auditTypeID
JOIN contractor_operator co ON co.conID = a.id AND co.opID IN (10566)
JOIN operators coo ON coo.id = co.opID
LEFT JOIN users auditor ON auditor.id = ca.auditorID
JOIN users contact ON contact.id = a.contactID
JOIN contractor_audit_operator cao ON cao.auditID = ca.id
JOIN accounts caoAccount ON cao.opID = caoAccount.id
LEFT JOIN contractor_audit_operator_workflow caow ON cao.id = caow.caoID
LEFT JOIN pqfdata d ON d.auditID = ca.id AND d.questionID IN (SELECT aq.id FROM audit_question aq JOIN audit_category_rule acr ON acr.catID = aq.categoryID AND acr.opID IN (17066,10566,17065,13265,16577,16576,13267,13266,16578,17886,17887,20186,16574,16575,3716,21985,47720,10850,16808,16714) WHERE aq.questionType = 'FileCertificate' AND aq.number = 1 AND acr.opID = cao.opID AND acr.effectiveDate < NOW() AND acr.expirationDate > NOW())
WHERE (a.type='Contractor')
 AND (co.conID IN (SELECT conID FROM contractor_operator WHERE opID = 10566))
 AND (a.id IN (SELECT co.conID FROM contractor_operator co WHERE co.opID IN (17066,17065,13265,16577,16576,13267,13266,16578,17886,17887,20186,16574,47720,21985,3716,16575,10850,16808,16714)))
 AND (ca.expiresDate IS NULL OR ca.expiresDate > NOW())
 AND (cao.status IN ('Complete'))
 AND (atype.classType = 'Policy')
 AND (cao.visible = 1)
 AND (a.status IN ('Active'))
 AND (cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (17066,17065,13265,16577,16576,13267,13266,16578,17886,17887,20186,16574,47720,21985,3716,16575,10850,16808,16714)))
GROUP BY cao.id;

UPDATE contractor_audit_operator cao
JOIN temp_bulk_approve_suncor_insurance_policies tbasip ON cao.id = tbasip.caoID
SET updatedBy = 1, updateDate = NOW(), cao.status = 'Approved', cao.statusChangedDate = NOW();

INSERT INTO contractor_audit_operator_workflow (createdBy,updatedBy,creationDate,updateDate,caoID,STATUS,previousStatus,notes)
SELECT DISTINCT 1,1,NOW(),NOW(),caoID,'Approved',auditStatus,'Bulk Approval 2013-12-11'
FROM temp_bulk_approve_suncor_insurance_policies;
