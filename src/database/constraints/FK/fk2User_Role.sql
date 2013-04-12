ALTER TABLE `tblUser_Role`
DROP INDEX `fk2User_Role`
;

/*
**	Name:		tblUser_Role
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUser_Role foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUser_Role`
ADD	CONSTRAINT	`fk2User_Role`	FOREIGN KEY
(
	Role_id		
,	Role_tp		

)
	REFERENCES	`tblRole`
(
	Role_id		
,	Role_tp		

)
;

