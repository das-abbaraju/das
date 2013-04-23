ALTER TABLE `tblRightType`
DROP INDEX `fk1RightType`
;

/*
**	Name:		tblRightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRightType`
ADD	CONSTRAINT	`fk1RightType`	FOREIGN KEY
(
	Right_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;

