update contractor_audit_operator cao
  join contractor_audit_operator_workflow caow on caow.caoID = cao.id
set cao.statusChangedDate = caow.creationDate
where caow.notes = 'Auto-submitted by IGAutoSubmissionCron.' and cao.statusChangedDate is null;

update contractor_audit_operator_workflow
set updatedBy = 1, updateDate = creationDate
where notes = 'Auto-submitted by IGAutoSubmissionCron.' and updatedBy is null and updateDate is null;

