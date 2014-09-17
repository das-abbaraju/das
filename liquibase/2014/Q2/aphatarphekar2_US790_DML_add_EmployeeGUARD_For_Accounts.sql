--liquibase formatted sql

--changeset aphatarphekar:2
insert into accountemployeeguard (accountID)
	(select operators.id from operators
		where operators.requiresEmployeeGuard = 1)
	on duplicate key update accountemployeeguard.deletedDate = null;

insert into accountemployeeguard (accountID)
	(select contractor_info.id from contractor_info
		where contractor_info.hasEmployeeGuard = 1)
	on duplicate key update accountemployeeguard.deletedDate = null;