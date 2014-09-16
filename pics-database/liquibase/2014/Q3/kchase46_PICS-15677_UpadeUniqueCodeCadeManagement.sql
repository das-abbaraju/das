--liquibase formatted sql

--changeset kchase:46
-- update case management questions
update audit_question
set uniqueCode = 'CaseManagementPlan'
where id in (12641, 3477);