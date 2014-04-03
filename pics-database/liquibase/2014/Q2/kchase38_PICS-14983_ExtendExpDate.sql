--liquibase formatted sql

--changeset kchase:38

-- update expiration dates of good quarters
update contractor_audit
set expiresDate = '2014-09-30 23:59:59'
where audittypeid=665
and auditFor ='2013:3'
;

update contractor_audit
set expiresDate = '2014-12-31 23:59:59'
where audittypeid=665
and auditFor ='2013:4'
;

update contractor_audit
set expiresDate = '2015-03-31 23:59:59'
where audittypeid=665
and auditFor ='2014:1'
;

-- delete bad audit
delete from contractor_audit
where audittypeid=665
and expiresDate='2014-03-31 23:59:59'
;

-- update months to expire
update audit_type
set monthsToExpire=15
where id=665;