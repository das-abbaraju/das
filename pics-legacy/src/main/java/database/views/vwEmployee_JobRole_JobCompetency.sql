
CREATE OR REPLACE VIEW	vwEmployee_JobRole_JobCompetency
(
		employeeID
	,	jobRoleID
	,	NAME
	,	accountID
	,	active
	,	competencyID
)
AS
/*
**	Name:		vwEmployee_JobRole_JobCompetency
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
		employee_role.employeeID	employeeID
	,	employee_role.jobRoleID	jobRoleID
	,	job_role.name	NAME
	,	job_role.accountID	accountID
	,	job_role.active	active
	,	job_competency.competencyID	competencyID
	FROM
		employee_role
	JOIN
		job_role
	ON	job_role.id	= employee_role.jobRoleID 
	LEFT JOIN 
		job_competency
	ON 	job_competency.jobRoleID	= employee_role.jobRoleID 
;


