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

