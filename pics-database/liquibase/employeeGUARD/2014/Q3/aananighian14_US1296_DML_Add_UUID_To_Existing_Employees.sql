--liquibase formatted sql

--changeset aananighian:14
set sql_safe_updates = 0;


ALTER TABLE `account_employee`
  ADD COLUMN `UUID` VARCHAR(40) NULL AFTER `id`,
ADD UNIQUE INDEX `UUID_UNIQUE_ACCOUNT_EMPLOYEE` (`UUID` ASC);


update account_employee
	set account_employee.UUID = UUID()
	where account_employee.UUID is null;

ALTER TABLE `account_employee`
	CHANGE COLUMN `UUID` `UUID` VARCHAR(40) NOT NULL ;



set sql_safe_updates = 1;