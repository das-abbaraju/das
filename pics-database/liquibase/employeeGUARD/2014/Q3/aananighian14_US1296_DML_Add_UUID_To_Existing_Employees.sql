--liquibase formatted sql

--changeset aananighian:14
set sql_safe_updates = 0;

update account_employee
	set account_employee.UUID = UUID()
	where account_employee.UUID is null;

set sql_safe_updates = 1;