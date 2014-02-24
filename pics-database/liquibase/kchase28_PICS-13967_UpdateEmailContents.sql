--liquibase formatted sql

--changeset kchase:28

-- update bad email contents
update email_queue
set body = 'Contents removed by PICS'
where emailID in (3474936, 3293497);