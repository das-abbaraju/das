ALTER TABLE `tblUserType`
DROP INDEX `fk1UserType`
;

/*
**	Name:		tblUserType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUserType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUserType`
ADD	CONSTRAINT	`fk1UserType`	FOREIGN KEY
(
	User_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;

