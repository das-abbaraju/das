--liquibase formatted sql

--changeset aananighian:9
ALTER TABLE `profiledocument`
CHANGE COLUMN `fileType` `fileType` VARCHAR(128) NULL DEFAULT NULL COMMENT 'The file type (.pdf, .jpg, .doc etc.)' ;