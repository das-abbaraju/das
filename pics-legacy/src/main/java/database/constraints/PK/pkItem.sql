ALTER TABLE `tblItem`
DROP INDEX `pkItem`
;

/*
**	Name:		tblItem
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblItem "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblItem`
ADD CONSTRAINT	`pkItem`	PRIMARY KEY
(
	Item_id		
,	Item_tp		

)
;

