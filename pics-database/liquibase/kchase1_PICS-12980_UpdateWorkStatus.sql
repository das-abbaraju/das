--liquibase formatted sql

--changeset kchase:1
-- update Johnson & Johnson contractors work status
update generalcontractors gc
set gc.workStatus='Y'
where gc.workStatus='P'
and gc.genID=41063;
