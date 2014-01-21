--liquibase formatted sql

--changeset mdo:28
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE report
SET public = 1
WHERE id IN (105,1093,110,1393,150,433,435,490,619,7,915,100);