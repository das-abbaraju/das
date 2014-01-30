--liquibase formatted sql

--changeset mdo:41
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE operators CONVERT TO CHARACTER SET utf8 COLLATE 'utf8_general_ci';
ALTER TABLE ref_country CONVERT TO CHARACTER SET utf8 COLLATE 'utf8_general_ci';
ALTER TABLE ref_country_subdivision CONVERT TO CHARACTER SET utf8 COLLATE 'utf8_general_ci';
SET FOREIGN_KEY_CHECKS = 1;
