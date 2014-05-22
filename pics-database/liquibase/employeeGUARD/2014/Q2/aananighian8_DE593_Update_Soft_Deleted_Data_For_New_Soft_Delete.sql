--liquibase formatted sql

--changeset aananighian:8
set sql_safe_updates = 0;

update account_employee
	set email = concat(email ,'_deleted_', now()),
		slug = concat(slug ,'_deleted_', now())
	where account_employee.deletedDate is not null
	and account_employee.email not like '%_deleted_%'
	and account_employee.slug not like '%_deleted_%';

update account_skill
	set account_skill.name = concat(name ,'_deleted_', now())
	where account_skill.deletedDate is not null
	and account_skill.name not like '%_deleted_%';

update account_group
	set account_group.name = concat(name ,'_deleted_', now())
	where account_group.deletedDate is not null
	and account_group.name not like '%_deleted_%';

update project
	set project.name = concat(name ,'_deleted_', now())
	where project.deletedDate is not null
	and project.name not like '%_deleted_%';

update profiledocument
	set profiledocument.name = concat(name ,'_deleted_', now())
	where profiledocument.deletedDate is not null
	and profiledocument.name not like '%_deleted_%';

set sql_safe_updates = 1;