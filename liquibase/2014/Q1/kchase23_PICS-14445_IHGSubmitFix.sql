--liquibase formatted sql

--changeset kchase:23
INSERT INTO contractor_audit_operator_workflow (createdBy,updatedBy,creationDate,updateDate,caoID,STATUS,previousStatus,notes)
SELECT DISTINCT 1,1,NOW(),NOW(),cao.id ,'Complete',cao.status,'Bulk move to Complete from Resubmit'
from contractor_audit_operator as cao
join contractor_audit as ca on ca.id = cao.auditID
where ca.auditTypeId=509
and cao.status='Resubmit'
;

Update contractor_audit_operator as cao
join contractor_audit as ca on ca.id = cao.auditID
set cao.status='Complete'
where ca.auditTypeId=509
and cao.status='Resubmit'
;

