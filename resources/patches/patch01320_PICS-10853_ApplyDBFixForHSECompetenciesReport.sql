-- Remove duplicate records from employee_competency first
ALTER TABLE	employee_competency
ADD UNIQUE INDEX 	idxEmployee_Competency
(
	employeeID
,	competencyID
)
;

DELETE t1
FROM
	employee_competency	AS t1
JOIN
	employee_competency	AS t2
WHERE
	t1.employeeID	= t2.employeeID
AND	t1.competencyID	= t2.competencyID
AND	t1.id	< t2.id
;

-- Remove duplicate records from employee_role first
ALTER TABLE	employee_role
ADD UNIQUE INDEX	idxEmployee_Role
(
	employeeID
,	jobRoleID
)
;

DELETE t1
FROM
	employee_role	AS t1
JOIN
	employee_role	AS t2
WHERE
	t1.employeeID	= t2.employeeID
AND	t1.jobRoleID	= t2.jobRoleID
AND	t1.id	< t2.id
;

