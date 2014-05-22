--liquibase formatted sql

--changeset aananighian:9
ALTER TABLE `pics_demo2`.`profiledocument`
CHANGE COLUMN `fileType` `fileType` VARCHAR(128) NULL DEFAULT NULL COMMENT 'The file type (.pdf, .jpg, .doc etc.)' ;