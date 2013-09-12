ALTER TABLE `tblResource`
DROP INDEX `fk1Resource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblResource foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblResource`
ADD	CONSTRAINT	`fk1Resource`	FOREIGN KEY
(
	Resrc_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;

