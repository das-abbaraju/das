/* Alter table in target */
ALTER TABLE `report_filter` 
	ADD COLUMN `negateOperator` tinyint(4)   NOT NULL DEFAULT 0 after `name`;
