DROP VIEW IF EXISTS	`vwapp_language`
;

CREATE OR REPLACE VIEW	`vwapp_language`
(
	Locale_cd
,	Language_cd
,	Country_cd
,	Status_nm
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwapp_language
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	app_language.locale
,	app_language.language
,	app_language.country
,	app_language.status
FROM
	app_language

;
DROP VIEW IF EXISTS	`vwContext`
;

CREATE OR REPLACE VIEW	`vwContext`
(
	Context_id
,	Context_tp
,	Context_nm
,	Context_cd
,	Context_tx
,	ContextADD_dm
,	ContextADD_nm
,	ContextUPD_dm
,	ContextUPD_nm
,	ContextDEL_dm
,	ContextDEL_nm
,	ParentContext_tp
,	ContextType_tx
,	ContextTypeLeft_id
,	ContextTypeRight_id
,	ContextTypeLevel_id
,	ContextTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwContext
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblContext.Context_id
,	tblContext.Context_tp
,	tblContext.Context_nm
,	tblContext.Context_cd
,	vwResource.Resrc_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwContextType.ParentContext_tp
,	vwContextType.ContextType_tx
,	vwContextType.ContextTypeLeft_id
,	vwContextType.ContextTypeRight_id
,	vwContextType.ContextTypeLevel_id
,	vwContextType.ContextTypeOrder_id
FROM
	tblContext
JOIN	vwResource
ON	tblContext.Context_id	= vwResource.Resrc_id		-- FK1
AND	tblContext.Context_tp	= vwResource.Resrc_tp
JOIN	vwContextType
ON	tblContext.Context_tp	= vwContextType.Context_tp		-- FK2

;
DROP VIEW IF EXISTS	`vwContextType`
;

CREATE OR REPLACE VIEW	`vwContextType`
(
	Context_tp
,	ParentContext_tp
,	ContextType_tx
,	ContextTypeLeft_id
,	ContextTypeRight_id
,	ContextTypeLevel_id
,	ContextTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwContextType
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblContextType.Context_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	tblContextType
JOIN	tblResourceType
ON	tblContextType.Context_tp	= tblResourceType.Resrc_tp		-- FK1

;
DROP VIEW IF EXISTS	`vwItem`
;

CREATE OR REPLACE VIEW	`vwItem`
(
	Item_id
,	Item_tp
,	Item_nm
,	Item_cd
,	Item_tx
,	ItemADD_dm
,	ItemADD_nm
,	ItemUPD_dm
,	ItemUPD_nm
,	ItemDEL_dm
,	ItemDEL_nm
,	ParentItem_tp
,	ItemType_tx
,	ItemTypeLeft_id
,	ItemTypeRight_id
,	ItemTypeLevel_id
,	ItemTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwItem
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblItem.Item_id
,	tblItem.Item_tp
,	tblItem.Item_nm
,	tblItem.Item_cd
,	vwResource.Resrc_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwItemType.ParentItem_tp
,	vwItemType.ItemType_tx
,	vwItemType.ItemTypeLeft_id
,	vwItemType.ItemTypeRight_id
,	vwItemType.ItemTypeLevel_id
,	vwItemType.ItemTypeOrder_id
FROM
	tblItem
JOIN	vwResource
ON	tblItem.Item_id	= vwResource.Resrc_id		-- FK1
AND	tblItem.Item_tp	= vwResource.Resrc_tp
JOIN	vwItemType
ON	tblItem.Item_tp	= vwItemType.Item_tp		-- FK2

;
DROP VIEW IF EXISTS	`vwItem_Context`
;

CREATE OR REPLACE VIEW	`vwItem_Context`
(
	Item_id
,	Item_tp
,	Context_id
,	Context_tp
,	Order_id
,	Item_nm
,	Item_cd
,	Context_nm
,	Context_cd
,	Item_tx
,	ItemADD_dm
,	ItemADD_nm
,	ItemUPD_dm
,	ItemUPD_nm
,	ItemDEL_dm
,	ItemDEL_nm
,	Context_tx
,	ContextADD_dm
,	ContextADD_nm
,	ContextUPD_dm
,	ContextUPD_nm
,	ContextDEL_dm
,	ContextDEL_nm
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwItem_Context
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblItem_Context.Item_id
,	tblItem_Context.Item_tp
,	tblItem_Context.Context_id
,	tblItem_Context.Context_tp
,	tblItem_Context.Order_id
,	vwItem.Item_nm
,	vwItem.Item_cd
,	vwContext.Context_nm
,	vwContext.Context_cd
,	vwItem.Item_tx
,	vwItem.ItemADD_dm
,	vwItem.ItemADD_nm
,	vwItem.ItemUPD_dm
,	vwItem.ItemUPD_nm
,	vwItem.ItemDEL_dm
,	vwItem.ItemDEL_nm
,	vwContext.Context_tx
,	vwContext.ContextADD_dm
,	vwContext.ContextADD_nm
,	vwContext.ContextUPD_dm
,	vwContext.ContextUPD_nm
,	vwContext.ContextDEL_dm
,	vwContext.ContextDEL_nm
FROM
	tblItem_Context
JOIN	vwItem
ON	tblItem_Context.Item_id	= vwItem.Item_id		-- FK1
AND	tblItem_Context.Item_tp	= vwItem.Item_tp
JOIN	vwContext
ON	tblItem_Context.Context_id	= vwContext.Context_id		-- FK2
AND	tblItem_Context.Context_tp	= vwContext.Context_tp

;
DROP VIEW IF EXISTS	`vwItem_Context_Locale`
;

CREATE OR REPLACE VIEW	`vwItem_Context_Locale`
(
	Item_id
,	Item_tp
,	Context_id
,	Context_tp
,	Locale_cd
,	ItemEntry_tp
,	ItemEntry_tx
,	Item_nm
,	Item_cd
,	Context_nm
,	Context_cd
,	Language_cd
,	Country_cd
,	Status_nm
,	Item_tx
,	EFF_dm
,	USE_dm
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwItem_Context_Locale
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblItem_Context_Locale.Item_id
,	tblItem_Context_Locale.Item_tp
,	tblItem_Context_Locale.Context_id
,	tblItem_Context_Locale.Context_tp
,	tblItem_Context_Locale.Locale_cd
,	vwItem_Locale.Entry_tp
,	tblItem_Context_Locale.ItemEntry_tx
,	vwItem_Context.Item_nm
,	vwItem_Context.Item_cd
,	vwItem_Context.Context_nm
,	vwItem_Context.Context_cd
,	vwItem_Locale.Language_cd
,	vwItem_Locale.Country_cd
,	vwItem_Locale.Status_nm
,	vwItem_Context.Item_tx
,	vwItem_Locale.EFF_dm
,	vwItem_Locale.USE_dm
FROM
	tblItem_Context_Locale
JOIN	vwItem_Context
ON	tblItem_Context_Locale.Item_id	= vwItem_Context.Item_id		-- FK1
AND	tblItem_Context_Locale.Item_tp	= vwItem_Context.Item_tp
AND	tblItem_Context_Locale.Context_id	= vwItem_Context.Context_id
AND	tblItem_Context_Locale.Context_tp	= vwItem_Context.Context_tp
JOIN	vwItem_Locale
ON	tblItem_Context_Locale.Item_id	= vwItem_Locale.Item_id		-- FK2
AND	tblItem_Context_Locale.Item_tp	= vwItem_Locale.Item_tp
AND	tblItem_Context_Locale.Locale_cd	= vwItem_Locale.Locale_cd

;
DROP VIEW IF EXISTS	`vwItem_Locale`
;

CREATE OR REPLACE VIEW	`vwItem_Locale`
(
	Item_id
,	Item_tp
,	Locale_cd
,	Entry_tp
,	Entry_tx
,	EFF_dm
,	USE_dm
,	Item_nm
,	Item_cd
,	Language_cd
,	Country_cd
,	Status_nm
,	Item_tx
,	ParentItem_tp
,	ItemType_tx
,	ItemADD_dm
,	ItemADD_nm
,	ItemUPD_dm
,	ItemUPD_nm
,	ItemDEL_dm
,	ItemDEL_nm
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwItem_Locale
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblItem_Locale.Item_id
,	tblItem_Locale.Item_tp
,	tblItem_Locale.Locale_cd
,	tblItem_Locale.Entry_tp
,	tblItem_Locale.Entry_tx
,	tblItem_Locale.EFF_dm
,	tblItem_Locale.USE_dm
,	vwItem.Item_nm
,	vwItem.Item_cd
,	vwapp_language.Language_cd
,	vwapp_language.Country_cd
,	vwapp_language.Status_nm
,	vwItem.Item_tx
,	vwItem.ParentItem_tp
,	vwItem.ItemType_tx
,	vwItem.ItemADD_dm
,	vwItem.ItemADD_nm
,	vwItem.ItemUPD_dm
,	vwItem.ItemUPD_nm
,	vwItem.ItemDEL_dm
,	vwItem.ItemDEL_nm
FROM
	tblItem_Locale
JOIN	vwItem
ON	tblItem_Locale.Item_id	= vwItem.Item_id		-- FK1
AND	tblItem_Locale.Item_tp	= vwItem.Item_tp
JOIN	vwapp_language
ON	tblItem_Locale.Locale_cd	= vwapp_language.Locale_cd		-- FK2

;
DROP VIEW IF EXISTS	`vwItemType`
;

CREATE OR REPLACE VIEW	`vwItemType`
(
	Item_tp
,	ParentItem_tp
,	ItemType_tx
,	ItemTypeLeft_id
,	ItemTypeRight_id
,	ItemTypeLevel_id
,	ItemTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwItemType
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblItemType.Item_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	tblItemType
JOIN	tblResourceType
ON	tblItemType.Item_tp	= tblResourceType.Resrc_tp		-- FK1

;
DROP VIEW IF EXISTS	`vwResource`
;

CREATE OR REPLACE VIEW	`vwResource`
(
	Resrc_id
,	Resrc_tp
,	Resrc_nm
,	Resrc_tx
,	ADD_dm
,	ADD_nm
,	UPD_dm
,	UPD_nm
,	DEL_dm
,	DEL_nm
,	ParentResrc_tp
,	ResrcType_tx
,	Left_id
,	Right_id
,	Level_id
,	Order_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwResource
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblResource.Resrc_id
,	tblResource.Resrc_tp
,	tblResource.Resrc_nm
,	tblResource.Resrc_tx
,	tblResource.ADD_dm
,	tblResource.ADD_nm
,	tblResource.UPD_dm
,	tblResource.UPD_nm
,	tblResource.DEL_dm
,	tblResource.DEL_nm
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	tblResource
JOIN	tblResourceType
ON	tblResource.Resrc_tp	= tblResourceType.Resrc_tp		-- FK1

;
DROP VIEW IF EXISTS	`vwResourceType`
;

CREATE OR REPLACE VIEW	`vwResourceType`
(
	Resrc_tp
,	ParentResrc_tp
,	ResrcType_tx
,	Left_id
,	Right_id
,	Level_id
,	Order_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwResourceType
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblResourceType.Resrc_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	tblResourceType

;

