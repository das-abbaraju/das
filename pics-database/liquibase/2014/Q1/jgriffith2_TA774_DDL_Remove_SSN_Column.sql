--liquibase formatted sql

--changeset jgriffith:2

alter table employee drop column ssn;
