--liquibase formatted sql

--changeset kchase:39

-- mark JS translations
update app_translation
set js=1
where msgKey like 'Report.execute.%';