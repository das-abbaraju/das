--liquibase formatted sql

--changeset mdo:12
--preConditions onFail MARK_RAN
--onUpdateSQL IGNORE
ALTER TABLE `ref_trade`
  CHANGE `safetySensitive` `safetySensitive` TINYINT(4) DEFAULT 0  NULL;
