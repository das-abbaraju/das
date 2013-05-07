ALTER TABLE `tblContextType`
DROP INDEX `fk1ContextType`
;

/*
**	Name:		tblContextType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblContextType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblContextType`
ADD	CONSTRAINT	`fk1ContextType`	FOREIGN KEY
(
	Context_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;

