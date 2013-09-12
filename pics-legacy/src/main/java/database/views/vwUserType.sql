DROP VIEW IF EXISTS	`vwUserType`
;

CREATE OR REPLACE VIEW	`vwUserType`
(
	User_tp
,	ParentUser_tp
,	UserType_tx
,	UserTypeLeft_id
,	UserTypeRight_id
,	UserTypeLevel_id
,	UserTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwUserType
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
	tblUserType.User_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	[pics_alpha1].[tblUserType]
INNER
JOIN	[pics_alpha1].[tblResourceType]
ON	tblUserType.User_tp	= tblResourceType.Resrc_tp		-- FK1

;

