--liquibase formatted sql
--changeset gmeurer:2

alter table translation_usage add column pageOrder varchar(20) after pageName;