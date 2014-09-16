--liquibase formatted sql

--changeset aananighian:14
set sql_safe_updates = 0;

ALTER TABLE `account_employee`
  ADD COLUMN `guid` VARCHAR(40) NULL AFTER `id`,
ADD UNIQUE INDEX `UUID_UNIQUE_ACCOUNT_EMPLOYEE` (`guid` ASC);


update account_employee
	set account_employee.guid = UUID()
	where account_employee.guid is null;

ALTER TABLE `account_employee`
	CHANGE COLUMN `guid` `guid` VARCHAR(40) NOT NULL ;


set sql_safe_updates = 1;