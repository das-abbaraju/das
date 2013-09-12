ALTER TABLE `app_language`
DROP INDEX `pk_language`
;

/*
**	Name:		app_language
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain app_language "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`app_language`
ADD CONSTRAINT	`pk_language`	PRIMARY KEY
(
	locale		

)
;
ALTER TABLE `tblContext`
DROP INDEX `pkContext`
;

/*
**	Name:		tblContext
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblContext "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblContext`
ADD CONSTRAINT	`pkContext`	PRIMARY KEY
(
	Context_id		
,	Context_tp		

)
;
ALTER TABLE `tblContextType`
DROP INDEX `pkContextType`
;

/*
**	Name:		tblContextType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblContextType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblContextType`
ADD CONSTRAINT	`pkContextType`	PRIMARY KEY
(
	Context_tp		

)
;
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
ALTER TABLE `tblItemType`
DROP INDEX `pkItemType`
;

/*
**	Name:		tblItemType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblItemType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblItemType`
ADD CONSTRAINT	`pkItemType`	PRIMARY KEY
(
	Item_tp		

)
;
ALTER TABLE `tblResource`
DROP INDEX `pkResource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResource "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResource`
ADD CONSTRAINT	`pkResource`	PRIMARY KEY
(
	Resrc_id		
,	Resrc_tp		

)
;
ALTER TABLE `tblResourceType`
DROP INDEX `pkResourceType`
;

/*
**	Name:		tblResourceType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResourceType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResourceType`
ADD CONSTRAINT	`pkResourceType`	PRIMARY KEY
(
	Resrc_tp		

)
;

