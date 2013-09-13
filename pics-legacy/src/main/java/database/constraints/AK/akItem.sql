ALTER TABLE `tblItem`
DROP INDEX `akItem`
;

/*
**	Name:		tblItem
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblItem "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblItem`
ADD CONSTRAINT	`akItem`	UNIQUE 
(
	Item_tp		
,	Item_nm		

)
;

