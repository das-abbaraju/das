DROP VIEW IF EXISTS	`vwRole`
;

CREATE OR REPLACE VIEW	`vwRole`
(
	Role_id
,	Role_tp
,	Role_nm
,	Role_cd
,	Role_tx
,	RoleADD_dm
,	RoleADD_nm
,	RoleUPD_dm
,	RoleUPD_nm
,	RoleDEL_dm
,	RoleDEL_nm
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
**	Name:		vwRole
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
	tblRole.Role_id
,	tblRole.Role_tp
,	tblRole.Role_nm
,	tblRole.Role_cd
,	vwResource.Resrc_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwRoleType.ParentRole_tp
,	vwRoleType.RoleType_tx
,	vwRoleType.RoleTypeLeft_id
,	vwRoleType.RoleTypeRight_id
,	vwRoleType.RoleTypeLevel_id
,	vwRoleType.RoleTypeOrder_id
FROM
	[pics_alpha1].[tblRole]
INNER
JOIN	[pics_alpha1].[vwResource]
ON	tblRole.Role_id	= vwResource.Resrc_id		-- FK1
AND	tblRole.Role_tp	= vwResource.Resrc_tp
INNER
JOIN	[pics_alpha1].[vwRoleType]
ON	tblRole.Role_tp	= vwRoleType.Role_tp		-- FK2

;

