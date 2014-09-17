--liquibase formatted sql

--changeset aananighian:3
set sql_safe_updates = 0;

update project_account_group_employee
	join account_employee on account_employee.id = project_account_group_employee.employeeID
	set project_account_group_employee.deletedDate = account_employee.deletedDate,
		project_account_group_employee.deletedBy = account_employee.deletedBy
	where account_employee.deletedDate is not null;

update site_assignment
	join account_employee on account_employee.id = site_assignment.employeeID
	set site_assignment.deletedDate = account_employee.deletedDate,
		site_assignment.deletedBy = account_employee.deletedBy
	where account_employee.deletedDate is not null;

delete from account_group_employee
	where account_group_employee.groupID IN
	(select id from account_group where account_group.type = 'Role');

set sql_safe_updates = 1;