ALTER TABLE `tblReportType`
DROP INDEX `pkReportType`
;

/*
**	Name:		tblReportType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblReportType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblReportType`
ADD CONSTRAINT	`pkReportType`	PRIMARY KEY
(
	Report_tp		

)
;

