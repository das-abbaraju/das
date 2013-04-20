ALTER TABLE `report_permission_account`
  ADD COLUMN `editable` TINYINT(1) NOT NULL AFTER `accountID`;
