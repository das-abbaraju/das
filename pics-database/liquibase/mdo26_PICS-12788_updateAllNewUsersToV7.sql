--liquibase formatted sql

--changeset mdo:23
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
ALTER TABLE `users`
  CHANGE `usingDynamicReports` `usingDynamicReports` TINYINT(4) DEFAULT 1  NOT NULL,
  CHANGE `usingVersion7Menus` `usingVersion7Menus` TINYINT(4) DEFAULT 1  NOT NULL;
