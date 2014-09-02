--liquibase formatted sql

--changeset kchase:43
insert ignore into togglz
(FEATURE_NAME,FEATURE_ENABLED) VALUES ('USE_NEW_AUDIT_BUILDER',0);

insert ignore into togglz
(FEATURE_NAME,FEATURE_ENABLED) VALUES ('USE_NEW_AUDIT_PERCENT_CALCULATOR',0);
