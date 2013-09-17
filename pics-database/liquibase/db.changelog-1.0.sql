--liquibase formatted sql

--changeset sshacter:2
create table test1 (
    id int primary key,
    name varchar(255)
);
--rollback drop table test1;

--changeset sshacter:2
insert into test1 (id, name) values (1, ‘name 1′);
insert into test1 (id, name) values (2, ‘name 2′);
