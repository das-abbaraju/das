-- liquibase formatted sql

-- changeset mdo:4

-- Flip a toggle from inclusive to exclusive to exclude BASF
UPDATE app_properties
SET VALUE = '!(permissions.primaryCorporateAccountID in [6115])'
WHERE property = 'Toggle.v7Menus';

-- rollback select 1 from dual;