--liquibase formatted sql

--changeset mdo:32
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
DELETE FROM invoice WHERE id IN (267025,266479)