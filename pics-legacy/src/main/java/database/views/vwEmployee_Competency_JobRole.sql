
CREATE OR REPLACE VIEW	vwEmployee_Competency_JobRole
(
		employeeID
	,	competencyID
	,	skilled
	,	jobRoleID
	,	accountID
	,	NAME
	,	active
)
AS
/*
**	Name:		vwEmployee_Competency_JobRole
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	03-MAY-2013
**
**	Modified:	03-MAY-2013
**	Modnumber:	00
**	Modification:	Original
**
*/
	SELECT 
		employee_competency.employeeID	employeeID
	,	employee_competency.competencyID	competencyID
	,	employee_competency.skilled	skilled
	,	employee_role.jobRoleID	jobRoleID
	,	job_role.accountID	accountID
	,	job_role.name	NAME
	,	job_role.active	active
	FROM 
		employee_competency
	JOIN 
		employee_role
	ON 	employee_role.employeeID 	= employee_competency.employeeID 
	JOIN
		job_competency
	ON 	job_competency.competencyID	= employee_competency.competencyID 
	AND 	job_competency.jobRoleID 	= employee_role.jobRoleID 
	JOIN
		job_role
	ON	job_role.id		= job_competency.jobRoleID 
	AND 	job_role.active	= 1 

;

