--liquibase formatted sql

--changeset aananighian:10

set sql_safe_updates = 0;

update project_account_group_employee
	set project_account_group_employee.deletedDate = now()
	where project_account_group_employee.projectGroupID in
		(select id from
			(select distinct page.projectGroupID id from project_account pa
				join project p on p.id = pa.projectId
				join project_account_group pag on pag.projectId = p.id
				join project_account_group_employee page on page.projectGroupID = pag.id
				join account_employee ae on ae.id = page.employeeID
				where pa.deletedDate is not null
				and page.deletedDate is null)
		t1)
	and employeeId in
		(select id from
			(select distinct ae2.id from project_account pa2
				join project p2 on p2.id = pa2.projectId
				join project_account_group pag2 on pag2.projectId = p2.id
				join project_account_group_employee page2 on page2.projectGroupID = pag2.id
				join account_employee ae2 on ae2.id = page2.employeeID
				where pa2.deletedDate is not null
				and page2.deletedDate is null)
		t2);

delete from account_skill_profile
	where id in
	(select id from
		(select asp.* from account_skill_profile asp
			join account_skill on account_skill.id = asp.skillid
			where account_skill.skillType = 'Certification'
			and asp.documentid is null)
	t);

set sql_safe_updates = 1;