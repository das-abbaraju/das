--liquibase formatted sql

--sql:
--changeset sshacter:42
ALTER TABLE accounts CONVERT TO CHARACTER SET utf8 COLLATE 'utf8_general_ci';
ALTER TABLE app_translation CONVERT TO CHARACTER SET utf8 COLLATE 'utf8_general_ci';

