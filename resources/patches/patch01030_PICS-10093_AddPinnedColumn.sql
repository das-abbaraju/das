ALTER TABLE `pics_alpha1`.`report_user`
  ADD COLUMN `pinned` TINYINT(1) DEFAULT 0  NOT NULL AFTER `hidden`;
