DROP VIEW IF EXISTS	`vwRole_Resource_RightType`
;

CREATE OR REPLACE VIEW	`vwRole_Resource_RightType`
(
	Role_id
,	Role_tp
,	Role_nm
,	Role_cd
,	Resrc_id
,	Resrc_tp
,	Resrc_nm
,	Right_tp
,	Role_tx
,	Resrc_tx
,	ParentResrc_tp
,	ResrcType_tx
,	ParentRight_tp
,	RightType_tx
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwRole_Resource_RightType
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
	tblRole_Resource_RightType.Role_id
,	tblRole_Resource_RightType.Role_tp
,	vwRole.Role_nm
,	vwRole.Role_cd
,	tblRole_Resource_RightType.Resrc_id
,	tblRole_Resource_RightType.Resrc_tp
,	vwResource.Resrc_nm
,	tblRole_Resource_RightType.Right_tp
,	vwRole.Role_tx
,	vwResource.Resrc_tx
,	vwResource_RightType.ParentResrc_tp
,	vwResource_RightType.ResrcType_tx
,	vwResource_RightType.ParentRight_tp
,	vwResource_RightType.RightType_tx
FROM
	[pics_alpha1].[tblRole_Resource_RightType]
INNER
JOIN	[pics_alpha1].[vwRole]
ON	tblRole_Resource_RightType.Role_id	= vwRole.Role_id		-- FK1
AND	tblRole_Resource_RightType.Role_tp	= vwRole.Role_tp
INNER
JOIN	[pics_alpha1].[vwResource]
ON	tblRole_Resource_RightType.Resrc_id	= vwResource.Resrc_id		-- FK2
AND	tblRole_Resource_RightType.Resrc_tp	= vwResource.Resrc_tp
INNER
JOIN	[pics_alpha1].[vwResource_RightType]
ON	tblRole_Resource_RightType.Resrc_id	= vwResource_RightType.Resrc_id		-- FK3
AND	tblRole_Resource_RightType.Resrc_tp	= vwResource_RightType.Resrc_tp
AND	tblRole_Resource_RightType.Right_tp	= vwResource_RightType.Right_tp

;

