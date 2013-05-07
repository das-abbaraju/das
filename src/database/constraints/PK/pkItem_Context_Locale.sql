ALTER TABLE `tblItem_Context_Locale`
DROP INDEX `pkItem_Context_Locale`
;

/*
**	Name:		tblItem_Context_Locale
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblItem_Context_Locale "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblItem_Context_Locale`
ADD CONSTRAINT	`pkItem_Context_Locale`	PRIMARY KEY
(
	Item_id		
,	Item_tp		
,	Context_id		
,	Context_tp		
,	Locale_cd		

)
;

