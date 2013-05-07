
CREATE OR REPLACE VIEW	vwHSECompetenciesSkilledCount
AS
/*
**	Name:		vwHSECompetenciesSkilledCount
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
		vwEmployee_Competency_JobRole.employeeID
	,	COUNT(vwEmployee_Competency_JobRole.competencyID) counts
	FROM
		vwEmployee_Competency_JobRole
	WHERE 
	 	vwEmployee_Competency_JobRole.active	= 1 
	GROUP BY
		vwEmployee_Competency_JobRole.employeeID
;

