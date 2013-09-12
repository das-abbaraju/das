DROP VIEW IF EXISTS	`vwRole_ResourceType_RightType`
;

CREATE OR REPLACE VIEW	`vwRole_ResourceType_RightType`
(
	Role_id
,	Role_tp
,	Role_nm
,	Role_cd
,	Resrc_tp
,	Right_tp
,	Role_tx
,	ParentResrc_tp
,	ResrcType_tx
,	ParentRight_tp
,	RightType_tx
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwRole_ResourceType_RightType
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
	tblRole_ResourceType_RightType.Role_id
,	tblRole_ResourceType_RightType.Role_tp
,	vwRole.Role_nm
,	vwRole.Role_cd
,	tblRole_ResourceType_RightType.Resrc_tp
,	tblRole_ResourceType_RightType.Right_tp
,	vwRole.Role_tx
,	vwResourceType.ParentResrc_tp
,	vwResourceType.ResrcType_tx
,	vwResourceType_RightType.ParentRight_tp
,	vwResourceType_RightType.RightType_tx
FROM
	[pics_alpha1].[tblRole_ResourceType_RightType]
INNER
JOIN	[pics_alpha1].[vwRole]
ON	tblRole_ResourceType_RightType.Role_id	= vwRole.Role_id		-- FK1
AND	tblRole_ResourceType_RightType.Role_tp	= vwRole.Role_tp
INNER
JOIN	[pics_alpha1].[vwResourceType]
ON	tblRole_ResourceType_RightType.Resrc_tp	= vwResourceType.Resrc_tp		-- FK2
INNER
JOIN	[pics_alpha1].[vwResourceType_RightType]
ON	tblRole_ResourceType_RightType.Resrc_tp	= vwResourceType_RightType.Resrc_tp		-- FK3
AND	tblRole_ResourceType_RightType.Right_tp	= vwResourceType_RightType.Right_tp

;

