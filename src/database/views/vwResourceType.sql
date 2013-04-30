DROP VIEW IF EXISTS	`vwResourceType`
;

CREATE OR REPLACE VIEW	`vwResourceType`
(
	Resrc_tp
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
**	Name:		vwResourceType
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
	tblResourceType.Resrc_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	tblResourceType

;

