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
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
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
	[pics_alpha1].[tblResource]
INNER
JOIN	[pics_alpha1].[tblResourceType]
ON	tblResource.Resrc_tp	= tblResourceType.Resrc_tp		-- FK1

;

