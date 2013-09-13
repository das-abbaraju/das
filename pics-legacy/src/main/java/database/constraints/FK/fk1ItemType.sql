ALTER TABLE `tblItemType`
DROP INDEX `fk1ItemType`
;

/*
**	Name:		tblItemType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblItemType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblItemType`
ADD	CONSTRAINT	`fk1ItemType`	FOREIGN KEY
(
	Item_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;

