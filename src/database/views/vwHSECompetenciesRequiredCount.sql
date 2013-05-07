
CREATE OR REPLACE VIEW	vwHSECompetenciesRequiredCount
AS
/*
**	Name:		vwHSECompetenciesRequiredCount
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
		vwEmployee_JobRole_JobCompetency.employeeID
	,	vwEmployee_JobRole_JobCompetency.NAME
	,	COUNT(vwEmployee_JobRole_JobCompetency.competencyID) counts 
	FROM
		vwEmployee_JobRole_JobCompetency
	GROUP BY 
		vwEmployee_JobRole_JobCompetency.employeeID
;


