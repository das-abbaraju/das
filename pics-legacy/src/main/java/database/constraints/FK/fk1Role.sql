ALTER TABLE `tblRole`
DROP INDEX `fk1Role`
;

/*
**	Name:		tblRole
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRole foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRole`
ADD	CONSTRAINT	`fk1Role`	FOREIGN KEY
(
	Role_id		
,	Role_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;

