DROP VIEW IF EXISTS	`vwRoleType`
;

CREATE OR REPLACE VIEW	`vwRoleType`
(
	Role_tp
,	ParentRole_tp
,	RoleType_tx
,	RoleTypeLeft_id
,	RoleTypeRight_id
,	RoleTypeLevel_id
,	RoleTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwRoleType
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
	tblRoleType.Role_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	[pics_alpha1].[tblRoleType]
INNER
JOIN	[pics_alpha1].[tblResourceType]
ON	tblRoleType.Role_tp	= tblResourceType.Resrc_tp		-- FK1

;

