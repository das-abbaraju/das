DROP VIEW IF EXISTS	`vwUser`
;

CREATE OR REPLACE VIEW	`vwUser`
(
	User_id
,	User_tp
,	User_nm
,	Domain_nm
,	Password_cd
,	Email_tx
,	User_tx
,	UserADD_dm
,	UserADD_nm
,	UserUPD_dm
,	UserUPD_nm
,	UserDEL_dm
,	UserDEL_nm
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
**	Name:		vwUser
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
	tblUser.User_id
,	tblUser.User_tp
,	tblUser.User_nm
,	tblUser.Domain_nm
,	tblUser.Password_cd
,	tblUser.Email_tx
,	vwResource.Resrc_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwUserType.ParentUser_tp
,	vwUserType.UserType_tx
,	vwUserType.UserTypeLeft_id
,	vwUserType.UserTypeRight_id
,	vwUserType.UserTypeLevel_id
,	vwUserType.UserTypeOrder_id
FROM
	[pics_alpha1].[tblUser]
INNER
JOIN	[pics_alpha1].[vwResource]
ON	tblUser.User_id	= vwResource.Resrc_id		-- FK1
AND	tblUser.User_tp	= vwResource.Resrc_tp
INNER
JOIN	[pics_alpha1].[vwUserType]
ON	tblUser.User_tp	= vwUserType.User_tp		-- FK2

;

