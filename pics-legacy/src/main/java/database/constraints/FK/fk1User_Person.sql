ALTER TABLE `tblUser_Person`
DROP INDEX `fk1User_Person`
;

/*
**	Name:		tblUser_Person
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUser_Person foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUser_Person`
ADD	CONSTRAINT	`fk1User_Person`	FOREIGN KEY
(
	User_id		
,	User_tp		

)
	REFERENCES	`tblUser`
(
	User_id		
,	User_tp		

)
;

