ALTER TABLE `tblUser`
DROP INDEX `fk1User`
;

/*
**	Name:		tblUser
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUser foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUser`
ADD	CONSTRAINT	`fk1User`	FOREIGN KEY
(
	User_id		
,	User_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;

