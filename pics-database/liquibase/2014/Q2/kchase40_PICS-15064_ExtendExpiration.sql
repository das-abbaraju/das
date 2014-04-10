--liquibase formatted sql

--changeset kchase:40

-- Extend expiration date
update contractor_audit
set expiresDate=timestamp(date(adddate(effectiveDate, interval 36 month)), '23:59:59')
where auditTypeID=455
and expiresDate is not null
;