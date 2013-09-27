--liquibase formatted sql

--changeset sshacter:2
insert into _lbexample (id,name) values (1,"name-1");
insert into _lbexample (id,name) values (2,"name-2");
insert into _lbexample (id,name) values (3,"name-4");

update _lbexample
set name = "name-3"
where id = 3
;
