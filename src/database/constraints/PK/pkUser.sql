ALTER TABLE `tblUser`
DROP INDEX `pkUser`
;

/*
**	Name:		tblUser
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblUser "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUser`
ADD CONSTRAINT	`pkUser`	PRIMARY KEY
(
	User_id		
,	User_tp		

)
;

