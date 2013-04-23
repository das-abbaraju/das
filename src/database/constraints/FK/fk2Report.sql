ALTER TABLE `tblReport`
DROP INDEX `fk2Report`
;

/*
**	Name:		tblReport
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblReport foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblReport`
ADD	CONSTRAINT	`fk2Report`	FOREIGN KEY
(
	Report_tp		

)
	REFERENCES	`tblReportType`
(
	Report_tp		

)
;

