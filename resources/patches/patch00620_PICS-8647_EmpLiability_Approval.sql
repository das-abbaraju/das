-- set cao that were split off if prev cao was approved
UPDATE 
contractor_audit_operator cao
join contractor_audit ca on ca.id=cao.auditID and ca.auditTypeID = 23
join contractor_audit_operator_permission caop on caop.caoID = cao.id
join contractor_audit_operator cao2 on caop.previousCaoID = cao2.id
join contractor_audit ca2 on ca2.id = cao2.auditID
set cao.status='Approved'
where cao.visible=1
and cao.status not in ('Approved')
and ca2.auditTypeID = 23
and cao2.visible=0 
and cao2.status in ('Approved');