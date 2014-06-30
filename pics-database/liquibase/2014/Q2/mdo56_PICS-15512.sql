--liquibase formatted sql

--changeset mdo:56
DELETE t.* FROM app_translation t JOIN email_template et ON t.msgKey LIKE CONCAT('EmailTemplate.',et.id,'.translated%')
WHERE et.id IN (185,186,187,195,196,197,201,202,203);

DELETE et.* FROM email_template et WHERE et.id IN (185,186,187,195,196,197,201,202,203);