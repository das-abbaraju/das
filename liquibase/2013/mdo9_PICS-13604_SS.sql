--liquibase formatted sql

--changeset mdo:9
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
ALTER TABLE `contractor_info`
  ADD COLUMN `safetySensitive` TINYINT(4) DEFAULT 0  NOT NULL AFTER `insideSalesPriority`;
