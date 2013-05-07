ALTER TABLE `tblItem_Context_Locale`
DROP INDEX `fk2Item_Context_Locale`
;

/*
**	Name:		tblItem_Context_Locale
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblItem_Context_Locale foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblItem_Context_Locale`
ADD	CONSTRAINT	`fk2Item_Context_Locale`	FOREIGN KEY
(
	Item_id		
,	Item_tp		
,	Locale_cd		

)
	REFERENCES	`tblItem_Locale`
(
	Item_id		
,	Item_tp		
,	Locale_cd		

)
;

