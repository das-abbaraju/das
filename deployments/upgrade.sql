-- PICS-2926
update contractor_info c
join contractor_audit ca on ca.conID = c.id and ca.auditTypeID = 1
join pqfdata d on d.auditID = ca.id and d.questionID = 2444 and d.dateVerified is not null
set c.safetyRiskVerified = d.dateVerified;

update contractor_info c
join (select ca.conID, min(d.dateVerified) dateVerified from contractor_audit ca
join pqfdata d on d.auditID = ca.id and d.questionID in (7678, 7679) and d.dateVerified is not null
where ca.auditTypeID = 1
group by ca.conID) d2 on d2.conID = c.id
set c.productRiskVerified = d2.dateVerified;
-- END

-- PICS-3021
-- delete all invisible caos
delete from contractor_audit_operator where id in (select id from (
select cao.id from contractor_audit ca
join accounts a on ca.conID = a.id and a.type = 'Contractor' and a.status = 'Active'
join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 0
where ca.auditTypeID in (2)
) aa);

-- insert new pics global cao with data from later on workflow
insert into contractor_audit_operator (auditID, opID, createdBy, updatedBy, creationDate, updateDate, status, visible, percentVerified, percentComplete, statusChangedDate)
select distinct ca.id, 4, 37951, 37951, now(), now(), cao.status, 1, max(cao.percentVerified), max(cao.percentComplete), now()
from contractor_audit ca
join accounts a on ca.conID = a.id and a.type = 'Contractor' and a.status = 'Active'
join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 1
where ca.auditTypeID in (2)
group by ca.id;

-- delete all other caos
delete from contractor_audit_operator where id in (select id from (
select cao.id from contractor_audit ca
join accounts a on ca.conID = a.id and a.type = 'Contractor' and a.status = 'Active'
join contractor_audit_operator cao on ca.id = cao.auditID and cao.opID != 4 and cao.visible = 1
where ca.auditTypeID in (2)
) aa);

-- Script to reset the cron for all those contractors who have a new CAO. 
update contractor_info ci
join accounts a on ci.id = a.id
join contractor_audit ca on ci.id = ca.conID and ca.auditTypeID = 2
join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 1 and cao.opID = 4 and cao.creationDate > curdate()
set ci.needsRecalculation = ci.needsRecalculation + 2, ci.lastRecalculation = null
where a.type = 'Contractor' and a.status = 'Active'
-- END