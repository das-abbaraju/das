ALTER TABLE `tblContext`
DROP INDEX `fk2Context`
;

/*
**	Name:		tblContext
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblContext foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblContext`
ADD	CONSTRAINT	`fk2Context`	FOREIGN KEY
(
	Context_tp		

)
	REFERENCES	`tblContextType`
(
	Context_tp		

)
;

