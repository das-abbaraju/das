ALTER TABLE `tblItem_Context`
DROP INDEX `fk1Item_Context`
;

/*
**	Name:		tblItem_Context
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblItem_Context foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblItem_Context`
ADD	CONSTRAINT	`fk1Item_Context`	FOREIGN KEY
(
	Item_id		
,	Item_tp		

)
	REFERENCES	`tblItem`
(
	Item_id		
,	Item_tp		

)
;

