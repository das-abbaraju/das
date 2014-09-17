--liquibase formatted sql

--changeset mdo:11
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
ALTER TABLE `ref_trade`
  ADD COLUMN `safetySensitive` TINYINT(4) DEFAULT 0  NOT NULL AFTER `transportation`;

UPDATE ref_trade
SET safetySensitive = CASE WHEN safetyRisk > 1 THEN 1 ELSE 0 END;
