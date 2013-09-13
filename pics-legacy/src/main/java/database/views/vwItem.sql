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

