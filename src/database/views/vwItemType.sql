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

