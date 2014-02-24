--liquibase formatted sql

--changeset mdo:46
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
UPDATE email_template
SET body = REPLACE(body,'http:','https:')
WHERE id IN (159,367);

