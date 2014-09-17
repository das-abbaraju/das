--liquibase formatted sql

--changeset kchase:10
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
-- add columns
Alter table audit_type_rule
add column `safetySensitive` tinyint(4) NULL;

Alter table audit_category_rule
add column `safetySensitive` tinyint(4) NULL;
