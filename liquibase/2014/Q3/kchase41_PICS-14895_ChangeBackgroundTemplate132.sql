--liquibase formatted sql

--changeset kchase:41

-- Update background
update email_template
set body = Replace(body, 'background:#002441', 'background:#e6e6e6')
where id=132;