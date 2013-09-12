-- PICS-6873
-- change wcb to use WCB workflow
UPDATE
audit_type a
set workflowID=21
where a.id in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144);

-- update all complete to approve
UPDATE
contractor_audit_operator cao
join contractor_audit ca on ca.id = cao.auditID
set cao.status='Approved'
where cao.status='Complete'
and ca.auditTypeID in (145, 146, 143, 170, 261, 168, 148, 147, 169, 166, 167, 144);
