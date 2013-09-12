ALTER TABLE `tblItem_Locale`
DROP INDEX `pkItem_Locale`
;

/*
**	Name:		tblItem_Locale
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblItem_Locale "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblItem_Locale`
ADD CONSTRAINT	`pkItem_Locale`	PRIMARY KEY
(
	Item_id		
,	Item_tp		
,	Locale_cd		

)
;

