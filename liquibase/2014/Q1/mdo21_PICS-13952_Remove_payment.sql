--liquibase formatted sql

--changeset mdo:21
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
DELETE FROM invoice WHERE id = 260514;