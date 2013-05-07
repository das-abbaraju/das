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
LEFT JOIN	vwItem_Locale
ON	tblItem_Context_Locale.Item_id	= vwItem_Locale.Item_id		-- FK2
AND	tblItem_Context_Locale.Item_tp	= vwItem_Locale.Item_tp
AND	tblItem_Context_Locale.Locale_cd	= vwItem_Locale.Locale_cd

;

