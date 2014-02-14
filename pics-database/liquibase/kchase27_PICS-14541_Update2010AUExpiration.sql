--liquibase formatted sql

--changeset kchase:27

-- update 2010 AUs skipping using a rnage to skip over invalid AUs
update contractor_audit
set expiresDate = '2014-03-15 23:59:59'
where auditTypeID = 11
and auditFor='2010'
and expiresDate > '2014-01-01' and expiresDate < '2014-03-15'
;