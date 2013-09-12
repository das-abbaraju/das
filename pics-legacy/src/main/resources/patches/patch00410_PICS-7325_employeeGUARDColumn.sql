ALTER TABLE `operator_tag`
	ADD COLUMN `category` VARCHAR(50) NOT NULL DEFAULT 'None' AFTER `inheritable`;
