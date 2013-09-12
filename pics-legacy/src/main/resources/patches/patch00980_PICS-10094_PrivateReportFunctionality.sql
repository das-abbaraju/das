ALTER TABLE `report`
  ADD COLUMN `private` TINYINT(1) DEFAULT 0  NULL  COMMENT '1 = Private, 0 = Public' AFTER `filterExpression`;