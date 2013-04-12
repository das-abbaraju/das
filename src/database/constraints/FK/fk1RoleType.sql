ALTER TABLE `tblRoleType`
DROP INDEX `fk1RoleType`
;

/*
**	Name:		tblRoleType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRoleType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRoleType`
ADD	CONSTRAINT	`fk1RoleType`	FOREIGN KEY
(
	Role_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;

