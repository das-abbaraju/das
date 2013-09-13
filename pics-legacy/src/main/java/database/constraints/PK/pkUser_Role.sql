ALTER TABLE `tblUser_Role`
DROP INDEX `pkUser_Role`
;

/*
**	Name:		tblUser_Role
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblUser_Role "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUser_Role`
ADD CONSTRAINT	`pkUser_Role`	PRIMARY KEY
(
	User_id		
,	User_tp		
,	Role_id		
,	Role_tp		

)
;

