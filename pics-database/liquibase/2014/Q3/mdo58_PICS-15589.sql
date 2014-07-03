--liquibase formatted sql

--changeset mdo:58
UPDATE report
SET slug = 'ContractorList'
WHERE id = 1;

