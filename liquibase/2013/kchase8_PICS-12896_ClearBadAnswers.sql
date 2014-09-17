--liquibase formatted sql

--changeset kchase:8
update pqfdata
set answer=NULL
where id in (15325892, 15520579, 15620841);