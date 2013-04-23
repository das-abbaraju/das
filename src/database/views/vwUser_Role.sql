DROP VIEW IF EXISTS	`vwUser_Role`
;

CREATE OR REPLACE VIEW	`vwUser_Role`
(
	User_id
,	User_tp
,	User_nm
,	Domain_nm
,	Password_cd
,	Email_tx
,	Role_id
,	Role_tp
,	Role_nm
,	Role_cd
,	User_tx
,	Role_tx
,	UserADD_dm
,	UserADD_nm
,	UserUPD_dm
,	UserUPD_nm
,	UserDEL_dm
,	UserDEL_nm
,	ParentUser_tp
,	UserType_tx
,	RoleADD_dm
,	RoleADD_nm
,	RoleUPD_dm
,	RoleUPD_nm
,	RoleDEL_dm
,	RoleDEL_nm
,	ParentRole_tp
,	RoleType_tx
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwUser_Role
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
	tblUser_Role.User_id
,	tblUser_Role.User_tp
,	vwUser.User_nm
,	vwUser.Domain_nm
,	vwUser.Password_cd
,	vwUser.Email_tx
,	tblUser_Role.Role_id
,	tblUser_Role.Role_tp
,	vwRole.Role_nm
,	vwRole.Role_cd
,	vwUser.User_tx
,	vwRole.Role_tx
,	vwUser.UserADD_dm
,	vwUser.UserADD_nm
,	vwUser.UserUPD_dm
,	vwUser.UserUPD_nm
,	vwUser.UserDEL_dm
,	vwUser.UserDEL_nm
,	vwUser.ParentUser_tp
,	vwUser.UserType_tx
,	vwRole.RoleADD_dm
,	vwRole.RoleADD_nm
,	vwRole.RoleUPD_dm
,	vwRole.RoleUPD_nm
,	vwRole.RoleDEL_dm
,	vwRole.RoleDEL_nm
,	vwRole.ParentRole_tp
,	vwRole.RoleType_tx
FROM
	[pics_alpha1].[tblUser_Role]
INNER
JOIN	[pics_alpha1].[vwUser]
ON	tblUser_Role.User_id	= vwUser.User_id		-- FK1
AND	tblUser_Role.User_tp	= vwUser.User_tp
INNER
JOIN	[pics_alpha1].[vwRole]
ON	tblUser_Role.Role_id	= vwRole.Role_id		-- FK2
AND	tblUser_Role.Role_tp	= vwRole.Role_tp

;

