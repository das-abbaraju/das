ALTER TABLE `tblUser_Person`
DROP INDEX `fk2User_Person`
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
ADD	CONSTRAINT	`fk2User_Person`	FOREIGN KEY
(
	Person_id		
,	Person_tp		

)
	REFERENCES	`tblPerson`
(
	Person_id		
,	Person_tp		

)
;

