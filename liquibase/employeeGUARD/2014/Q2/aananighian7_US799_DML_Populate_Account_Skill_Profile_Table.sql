--liquibase formatted sql

--changeset aananighian:7
insert into account_skill_profile (skillID, profileID, documentID, startDate, finishDate, createdBy,
								   updatedBy, deletedBy, createdDate, updatedDate, deletedDate)

(select
		account_skill_employee.skillID,
		profile.id `profileID`,
		account_skill_employee.documentID,
		account_skill_employee.startDate,
		max(account_skill_employee.finishDate) `finishDate`,
		account_skill_employee.createdBy,
		account_skill_employee.updatedBy,
		account_skill_employee.deletedBy,
		account_skill_employee.createdDate,
		account_skill_employee.updatedDate,
		account_skill_employee.deletedDate

from account_skill_employee
	join account_employee on account_employee.id = account_skill_employee.employeeID
	join profile on profile.id = account_employee.profileId

where
	account_skill_employee.finishDate is not null
	and account_skill_employee.deletedDate is null

group by skillid, profileID)
on duplicate key update account_skill_profile.createdBy = account_skill_profile.createdBy;