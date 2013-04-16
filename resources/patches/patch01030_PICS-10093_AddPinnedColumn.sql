ALTER TABLE `report_user`
  ADD COLUMN `pinnedIndex` INT(11) DEFAULT -1 NOT NULL AFTER `hidden`;
