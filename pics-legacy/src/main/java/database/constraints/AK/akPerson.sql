ALTER TABLE `tblPerson`
DROP INDEX `akPerson`
;

/*
**	Name:		tblPerson
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblPerson "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblPerson`
ADD CONSTRAINT	`akPerson`	UNIQUE 
(
	Person_tp		
,	Person_nm		

)
;

