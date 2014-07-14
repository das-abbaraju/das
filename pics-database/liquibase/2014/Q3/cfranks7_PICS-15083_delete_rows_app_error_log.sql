--liquibase formatted sql

--changeset cfranks:7
DELETE FROM  app_error_log;

