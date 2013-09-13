DROP VIEW IF EXISTS	`vwResource_RightType`
;

CREATE OR REPLACE VIEW	`vwResource_RightType`
(
	Resrc_id
,	Resrc_tp
,	Right_tp
,	Resrc_nm
,	Resrc_tx
,	ParentResrc_tp
,	ResrcType_tx
,	ADD_dm
,	ADD_nm
,	UPD_dm
,	UPD_nm
,	DEL_dm
,	DEL_nm
,	ParentRight_tp
,	RightType_tx
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwResource_RightType
**	Type:		View
**	Purpose:	To return materialized data from one or more tables.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
SELECT
	tblResource_RightType.Resrc_id
,	tblResource_RightType.Resrc_tp
,	tblResource_RightType.Right_tp
,	vwResource.Resrc_nm
,	vwResource.Resrc_tx
,	vwResource.ParentResrc_tp
,	vwResource.ResrcType_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwRightType.ParentRight_tp
,	vwRightType.RightType_tx
FROM
	[pics_alpha1].[tblResource_RightType]
INNER
JOIN	[pics_alpha1].[vwResource]
ON	tblResource_RightType.Resrc_id	= vwResource.Resrc_id		-- FK1
AND	tblResource_RightType.Resrc_tp	= vwResource.Resrc_tp
INNER
JOIN	[pics_alpha1].[vwRightType]
ON	tblResource_RightType.Right_tp	= vwRightType.Right_tp		-- FK2

;

