ALTER TABLE	employee_role
ADD UNIQUE INDEX	idxEmployee_Role
(
	employeeID
,	jobRoleID
)
;
