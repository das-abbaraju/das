ALTER TABLE `tblItem_Context`
DROP INDEX `pkItem_Context`
;

/*
**	Name:		tblItem_Context
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblItem_Context "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblItem_Context`
ADD CONSTRAINT	`pkItem_Context`	PRIMARY KEY
(
	Item_id		
,	Item_tp		
,	Context_id		
,	Context_tp		

)
;

