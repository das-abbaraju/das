ALTER TABLE `tblRoleType`
DROP INDEX `pkRoleType`
;

/*
**	Name:		tblRoleType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblRoleType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRoleType`
ADD CONSTRAINT	`pkRoleType`	PRIMARY KEY
(
	Role_tp		

)
;

