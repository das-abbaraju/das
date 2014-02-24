--liquibase formatted sql

--changeset kchase:12
-- update open tasks email
update email_queue as q
set q.subjectViewableBy=1, q.bodyViewableBy=1100
where q.templateID=168
and q.subjectViewableBy is null;