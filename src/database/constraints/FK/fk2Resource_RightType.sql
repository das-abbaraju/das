ALTER TABLE `tblResource_RightType`
DROP INDEX `fk2Resource_RightType`
;

/*
**	Name:		tblResource_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblResource_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblResource_RightType`
ADD	CONSTRAINT	`fk2Resource_RightType`	FOREIGN KEY
(
	Right_tp		

)
	REFERENCES	`tblRightType`
(
	Right_tp		

)
;

