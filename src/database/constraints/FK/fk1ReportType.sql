ALTER TABLE `tblReportType`
DROP INDEX `fk1ReportType`
;

/*
**	Name:		tblReportType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblReportType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblReportType`
ADD	CONSTRAINT	`fk1ReportType`	FOREIGN KEY
(
	Report_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;

