--liquibase formatted sql

--changeset kchase:43

-- Update toggle
update togglz
set feature_enabled = 1
where feature_name="USE_NEW_CONTRACTOR_ETL";