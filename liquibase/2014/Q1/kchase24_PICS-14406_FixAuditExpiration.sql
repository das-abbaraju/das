--liquibase formatted sql

--changeset kchase:24
-- update exp date
Update contractor_audit as ca
set ca.expiresDate = '2015-03-15 23:59:59'
where ca.auditTypeID=242
and ca.expiresDate = '2015-03-01 23:59:59';