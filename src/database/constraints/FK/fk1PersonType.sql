ALTER TABLE `tblPersonType`
DROP INDEX `fk1PersonType`
;

/*
**	Name:		tblPersonType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblPersonType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblPersonType`
ADD	CONSTRAINT	`fk1PersonType`	FOREIGN KEY
(
	Person_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;

