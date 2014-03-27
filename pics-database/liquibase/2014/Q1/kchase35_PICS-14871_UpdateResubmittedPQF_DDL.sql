--liquibase formatted sql

--changeset kchase:35

-- Determine appropriate PQFs and CAOs to update
DROP TABLE IF EXISTS temp_caos_to_update;

CREATE TABLE temp_caos_to_update AS
SELECT cao.id as caoID, cao.status as caoStatus from contractor_audit as ca
join contractor_audit_operator as cao on cao.`auditID` = ca.id
where ca.auditTypeID=1
and ca.`expiresDate` > NOW()
and cao.visible = 1
and cao.status in ('Resubmit', 'Resubmitted')
;
