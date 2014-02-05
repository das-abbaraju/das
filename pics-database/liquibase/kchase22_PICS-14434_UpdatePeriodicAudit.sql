--liquibase formatted sql

--changeset kchase:22
update contractor_audit
set creationDate = date_add(creationDate, interval 1 month), effectiveDate = date_add(effectiveDate, interval 1 month), expiresDate = date_add(expiresDate, interval 1 month), auditFor ='2014-01'
where auditTypeID in (657, 658, 660, 661, 662)
and creationDate>='2013-12-01'
;

