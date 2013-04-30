ALTER TABLE `tblResource`
DROP INDEX `fk1Resource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblResource foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblResource`
ADD	CONSTRAINT	`fk1Resource`	FOREIGN KEY
(
	Resrc_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
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
ALTER TABLE `tblItem_Locale`
DROP INDEX `fk1Item_Locale`
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
ADD	CONSTRAINT	`fk1Item_Locale`	FOREIGN KEY
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
ALTER TABLE `tblItem_Context_Locale`
DROP INDEX `fk1Item_Context_Locale`
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
ADD	CONSTRAINT	`fk1Item_Context_Locale`	FOREIGN KEY
(
	Item_id		
,	Item_tp		
,	Context_id		
,	Context_tp		

)
	REFERENCES	`tblItem_Context`
(
	Item_id		
,	Item_tp		
,	Context_id		
,	Context_tp		

)
;
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
ALTER TABLE `tblItem`
DROP INDEX `fk1Item`
;

/*
**	Name:		tblItem
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblItem foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblItem`
ADD	CONSTRAINT	`fk1Item`	FOREIGN KEY
(
	Item_id		
,	Item_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;
ALTER TABLE `tblContextType`
DROP INDEX `fk1ContextType`
;

/*
**	Name:		tblContextType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblContextType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblContextType`
ADD	CONSTRAINT	`fk1ContextType`	FOREIGN KEY
(
	Context_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
ALTER TABLE `tblContext`
DROP INDEX `fk1Context`
;

/*
**	Name:		tblContext
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblContext foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblContext`
ADD	CONSTRAINT	`fk1Context`	FOREIGN KEY
(
	Context_id		
,	Context_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;
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
ALTER TABLE `tblItem_Context`
DROP INDEX `fk2Item_Context`
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
ADD	CONSTRAINT	`fk2Item_Context`	FOREIGN KEY
(
	Context_id		
,	Context_tp		

)
	REFERENCES	`tblContext`
(
	Context_id		
,	Context_tp		

)
;
ALTER TABLE `tblItem`
DROP INDEX `fk2Item`
;

/*
**	Name:		tblItem
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblItem foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblItem`
ADD	CONSTRAINT	`fk2Item`	FOREIGN KEY
(
	Item_tp		

)
	REFERENCES	`tblItemType`
(
	Item_tp		

)
;
ALTER TABLE `tblContext`
DROP INDEX `fk2Context`
;

/*
**	Name:		tblContext
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblContext foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblContext`
ADD	CONSTRAINT	`fk2Context`	FOREIGN KEY
(
	Context_tp		

)
	REFERENCES	`tblContextType`
(
	Context_tp		

)
;

