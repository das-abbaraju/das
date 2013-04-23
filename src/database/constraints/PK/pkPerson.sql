ALTER TABLE `tblPerson`
DROP INDEX `pkPerson`
;

/*
**	Name:		tblPerson
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblPerson "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblPerson`
ADD CONSTRAINT	`pkPerson`	PRIMARY KEY
(
	Person_id		
,	Person_tp		

)
;

