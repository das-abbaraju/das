/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/

-- CAO Status updates
update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'Pending'
where cao.status not in('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Pending'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');

update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'Awaiting'
where cao.status not in ('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Submitted'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');

update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'Verified'
where cao.status not in ('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Active'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');

update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'Awaiting'
where cao.status not in ('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Resubmitted'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');

update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'NotApplicable'
where cao.status not in ('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Exempt'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');
