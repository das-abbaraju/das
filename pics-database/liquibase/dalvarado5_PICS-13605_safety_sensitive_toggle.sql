--liquibase formatted sql

--changeset dalvarado:5

INSERT INTO app_properties
(`property`, `value`, `description`)
VALUES
('Toggle.SafetySensitive.Enabled', 'false', 'Indicates if Safety Sensitive feature is enabled.');

