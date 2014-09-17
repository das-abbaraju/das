--liquibase formatted sql

--changeset kchase:36

-- reset audit to be recalculated
update
contractor_audit
set lastRecalculation = NULL
where auditTypeId=456;

-- reset contractor to be recalculated
update
contractor_info
join contractor_audit on contractor_audit.conId = contractor_info.id
set contractor_info.needsRecalculation = null
where auditTypeId=456;
