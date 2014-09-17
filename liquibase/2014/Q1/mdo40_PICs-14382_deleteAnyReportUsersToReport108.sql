--liquibase formatted sql

--changeset mdo:40
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
DELETE FROM report_user WHERE reportID = 108