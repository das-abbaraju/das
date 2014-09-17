--liquibase formatted sql

--changeset rbeaini:3

INSERT INTO app_properties (`property`, `value`, `description`) VALUES ('AuthServiceHost', 'localhost', 'location of auth rest service, eventually moving to api.piscorganizer');

