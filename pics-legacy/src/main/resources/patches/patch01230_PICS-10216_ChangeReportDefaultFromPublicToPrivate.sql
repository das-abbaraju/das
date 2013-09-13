ALTER TABLE `report`
  ADD COLUMN `public` TINYINT(1) DEFAULT 0  NULL  COMMENT '1 = Public, 0 = Private' AFTER `private`;