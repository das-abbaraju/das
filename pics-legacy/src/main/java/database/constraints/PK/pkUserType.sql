ALTER TABLE `tblUserType`
DROP INDEX `pkUserType`
;

/*
**	Name:		tblUserType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblUserType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUserType`
ADD CONSTRAINT	`pkUserType`	PRIMARY KEY
(
	User_tp		

)
;

