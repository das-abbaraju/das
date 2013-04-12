ALTER TABLE `tblRightType`
DROP INDEX `pkRightType`
;

/*
**	Name:		tblRightType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblRightType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRightType`
ADD CONSTRAINT	`pkRightType`	PRIMARY KEY
(
	Right_tp		

)
;

