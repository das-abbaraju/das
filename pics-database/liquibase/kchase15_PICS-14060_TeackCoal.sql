--liquibase formatted sql

--changeset kchase:15
-- expire bad teck coal quarterlies
UPDATE contractor_audit as bad
join contractor_audit ca on ca.conID = bad.conID and ca.id < bad.id
join accounts a on a.id = bad.conID
set bad.expiresDate = NOW()
where a.status= 'Active'
and bad.auditTypeID=380
and ca.auditTypeID=380
and ca.expiresDate > NOW()
and bad.expiresDate is null;


-- move out expiration of good audits
update
contractor_audit as ca
join accounts a on a.id = ca.conID
set ca.expiresDate = STR_TO_DATE('2014-04-01 23:59:59', '%Y-%m-%d %H:%i:%s')
where a.status= 'Active'
and ca.auditTypeID=380
and ca.expiresDate > NOW();