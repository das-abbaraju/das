--liquibase formatted sql

--changeset dalvarado:1

delete from auditor_schedule
where starttime <= 120 or starttime > 1320 and userid = 81503;
