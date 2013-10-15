--liquibase formatted sql

--changeset kchase:4
update users
set shiftStartHour=4, shiftEndHour=12
where id in (70283, 77882, 34568);

update users
set shiftStartHour=0, shiftEndHour=8
where id=62190;