--liquibase formatted sql

--changeset kchase:14
-- extend expiration date
update
contractor_audit as ca
set ca.expiresDate = date_add(ca.effectiveDate, INTERVAL 36 month)
where ca.auditTypeID = 32
and ca.effectiveDate is not null and ca.expiresDate is not null
and ca.effectiveDate > '2013-01-01 00:00:00'
and date_sub(ca.expiresDate, interval 36 month) < ca.effectiveDate;

-- expire generated audits
update
contractor_audit as ca
set ca.expiresDate = NOW()
where ca.id = 1170407;
