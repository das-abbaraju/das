DROP VIEW IF EXISTS	`vwRightType`
;

CREATE OR REPLACE VIEW	`vwRightType`
(
	Right_tp
,	ParentRight_tp
,	RightType_tx
,	RightTypeLeft_id
,	RightTypeRight_id
,	RightTypeLevel_id
,	RightTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwRightType
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
	tblRightType.Right_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	[pics_alpha1].[tblRightType]
INNER
JOIN	[pics_alpha1].[tblResourceType]
ON	tblRightType.Right_tp	= tblResourceType.Resrc_tp		-- FK1

;

