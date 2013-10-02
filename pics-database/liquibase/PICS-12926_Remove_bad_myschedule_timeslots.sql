-- liquibase formatted sql

-- changeset dalvarado:3

-- Remove the bad My Schedule timeslots for Samuel Duckworth-Essilfie which are not removeable from the UI due to bug in our calendar plugin.
delete from auditor_schedule
where starttime <= 120 or starttime > 1320 and userid = 81503;


-- rollback select 1 from dual;