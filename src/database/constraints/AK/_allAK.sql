	--    NO ALTERNATE KEY DEFINED FOR app_language
ALTER TABLE `tblContext`
DROP INDEX `akContext`
;

/*
**	Name:		tblContext
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblContext "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblContext`
ADD CONSTRAINT	`akContext`	UNIQUE 
(
	Context_tp		
,	Context_nm		

)
;
	--    NO ALTERNATE KEY DEFINED FOR tblContextType
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
	--    NO ALTERNATE KEY DEFINED FOR tblItem_Context
	--    NO ALTERNATE KEY DEFINED FOR tblItem_Context_Locale
	--    NO ALTERNATE KEY DEFINED FOR tblItem_Locale
	--    NO ALTERNATE KEY DEFINED FOR tblItemType
ALTER TABLE `tblResource`
DROP INDEX `akResource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblResource "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResource`
ADD CONSTRAINT	`akResource`	UNIQUE 
(
	Resrc_nm		
,	Resrc_tp		

)
;
ALTER TABLE `tblResourceType`
DROP INDEX `akResourceType`
;

/*
**	Name:		tblResourceType
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblResourceType "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResourceType`
ADD CONSTRAINT	`akResourceType`	UNIQUE 
(
	Resrc_tp		

)
;

