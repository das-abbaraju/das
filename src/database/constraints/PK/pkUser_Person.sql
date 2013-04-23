ALTER TABLE `tblUser_Person`
DROP INDEX `pkUser_Person`
;

/*
**	Name:		tblUser_Person
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblUser_Person "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUser_Person`
ADD CONSTRAINT	`pkUser_Person`	PRIMARY KEY
(
	User_id		
,	User_tp		
,	Person_id		
,	Person_tp		

)
;

