ALTER TABLE `tblReport`
DROP INDEX `pkReport`
;

/*
**	Name:		tblReport
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblReport "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblReport`
ADD CONSTRAINT	`pkReport`	PRIMARY KEY
(
	Report_id		
,	Report_tp		

)
;

