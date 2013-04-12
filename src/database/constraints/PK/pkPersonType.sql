ALTER TABLE `tblPersonType`
DROP INDEX `pkPersonType`
;

/*
**	Name:		tblPersonType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblPersonType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblPersonType`
ADD CONSTRAINT	`pkPersonType`	PRIMARY KEY
(
	Person_tp		

)
;

