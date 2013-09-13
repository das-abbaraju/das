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

