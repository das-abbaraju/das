ALTER TABLE `tblItemType`
DROP INDEX `pkItemType`
;

/*
**	Name:		tblItemType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblItemType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblItemType`
ADD CONSTRAINT	`pkItemType`	PRIMARY KEY
(
	Item_tp		

)
;

