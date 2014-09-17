--liquibase formatted sql

--changeset kchase:30

-- update answers
update pqfdata
set answer='Corporate'
where questionID=18120
and answer=1506;