ALTER TABLE `tblItem_Locale`
DROP INDEX `fk2Item_Locale`
;

/*
**	Name:		tblItem_Locale
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblItem_Locale foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblItem_Locale`
ADD	CONSTRAINT	`fk2Item_Locale`	FOREIGN KEY
(
	Locale_cd		

)
	REFERENCES	`app_language`
(
	locale		

)
;

