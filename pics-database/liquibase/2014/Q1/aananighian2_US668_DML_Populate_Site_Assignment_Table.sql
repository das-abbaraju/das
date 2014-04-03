--liquibase formatted sql

--changeset aananighian:2
insert into site_assignment (roleID, siteID, employeeID, createdBy, createdDate)
(select corporate_role.id, site_role.accountID, site_role_employee.employeeID, site_role.createdBy, site_role.createdDate from account_group corporate_role
	join account_group site_role on corporate_role.name = site_role.name
	join accounts a on corporate_role.accountID = a.id and a.type = 'Corporate'
	join account_group_employee site_role_employee on site_role_employee.groupID = site_role.id
	where corporate_role.accountID != site_role.accountID)
on duplicate key update site_assignment.createdBy = site_role.createdBy;

insert into site_assignment (roleID, siteID, employeeID, createdBy, createdDate)
(select ag.id, p.accountID, page.employeeID, page.createdBy, page.createdDate from project_account_group_employee page
	join project_account_group pag on pag.id = page.projectGroupID
	join account_group ag on ag.id = pag.groupID
	join project p on p.id = pag.projectID)
on duplicate key update site_assignment.createdBy = page.createdBy;