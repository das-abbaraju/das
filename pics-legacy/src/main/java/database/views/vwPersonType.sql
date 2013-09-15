DROP VIEW IF EXISTS	`vwPersonType`
;

CREATE OR REPLACE VIEW	`vwPersonType`
(
	Person_tp
,	ParentPerson_tp
,	PersonType_tx
,	PersonTypeLeft_id
,	PersonTypeRight_id
,	PersonTypeLevel_id
,	PersonTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwPersonType
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
	tblPersonType.Person_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	[pics_alpha1].[tblPersonType]
INNER
JOIN	[pics_alpha1].[tblResourceType]
ON	tblPersonType.Person_tp	= tblResourceType.Resrc_tp		-- FK1

;
