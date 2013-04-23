ALTER TABLE `tblPerson`
DROP INDEX `fk2Person`
;

/*
**	Name:		tblPerson
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblPerson foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblPerson`
ADD	CONSTRAINT	`fk2Person`	FOREIGN KEY
(
	Person_tp		

)
	REFERENCES	`tblPersonType`
(
	Person_tp		

)
;

