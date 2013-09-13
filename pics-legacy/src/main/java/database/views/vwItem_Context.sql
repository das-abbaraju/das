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

