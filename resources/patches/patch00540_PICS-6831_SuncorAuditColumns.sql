-- add columns to audit type for audit assigners and auditor groups
Alter table audit_type add assignAudit int(11);
Alter table audit_type add editAudit int(11);

-- fill in these columns
Update audit_type
set assignAudit=10;  -- PICS Admin

UPDATE audit_type
set editAudit=11; -- PICS Auditor