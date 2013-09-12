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

