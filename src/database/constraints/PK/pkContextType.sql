ALTER TABLE `tblContextType`
DROP INDEX `pkContextType`
;

/*
**	Name:		tblContextType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblContextType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblContextType`
ADD CONSTRAINT	`pkContextType`	PRIMARY KEY
(
	Context_tp		

)
;

