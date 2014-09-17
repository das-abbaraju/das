--liquibase formatted sql

--changeset kchase:6
Delete from contractor_tag
where conID = 21041;