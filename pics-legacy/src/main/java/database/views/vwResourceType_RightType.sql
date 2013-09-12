DROP VIEW IF EXISTS	`vwResourceType_RightType`
;

CREATE OR REPLACE VIEW	`vwResourceType_RightType`
(
	Resrc_tp
,	Right_tp
,	ParentResrc_tp
,	ResrcType_tx
,	ParentRight_tp
,	RightType_tx
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwResourceType_RightType
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
	tblResourceType_RightType.Resrc_tp
,	tblResourceType_RightType.Right_tp
,	vwResourceType.ParentResrc_tp
,	vwResourceType.ResrcType_tx
,	vwRightType.ParentRight_tp
,	vwRightType.RightType_tx
FROM
	[pics_alpha1].[tblResourceType_RightType]
INNER
JOIN	[pics_alpha1].[vwResourceType]
ON	tblResourceType_RightType.Resrc_tp	= vwResourceType.Resrc_tp		-- FK1
INNER
JOIN	[pics_alpha1].[vwRightType]
ON	tblResourceType_RightType.Right_tp	= vwRightType.Right_tp		-- FK2

;

