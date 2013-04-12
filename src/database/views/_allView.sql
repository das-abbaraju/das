DROP VIEW IF EXISTS	`vwPerson`
;

CREATE OR REPLACE VIEW	`vwPerson`
(
	Person_id
,	Person_tp
,	Person_nm
,	First_nm
,	Middle_nm
,	Last_nm
,	FirstSNDX_cd
,	LastSNDX_cd
,	Birth_dm
,	Gender_cd
,	Person_tx
,	PersonADD_dm
,	PersonADD_nm
,	PersonUPD_dm
,	PersonUPD_nm
,	PersonDEL_dm
,	PersonDEL_nm
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
**	Name:		vwPerson
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
	tblPerson.Person_id
,	tblPerson.Person_tp
,	tblPerson.Person_nm
,	tblPerson.First_nm
,	tblPerson.Middle_nm
,	tblPerson.Last_nm
,	tblPerson.FirstSNDX_cd
,	tblPerson.LastSNDX_cd
,	tblPerson.Birth_dm
,	tblPerson.Gender_cd
,	vwResource.Resrc_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwPersonType.ParentPerson_tp
,	vwPersonType.PersonType_tx
,	vwPersonType.PersonTypeLeft_id
,	vwPersonType.PersonTypeRight_id
,	vwPersonType.PersonTypeLevel_id
,	vwPersonType.PersonTypeOrder_id
FROM
	[pics_alpha1].[tblPerson]
INNER
JOIN	[pics_alpha1].[vwResource]
ON	tblPerson.Person_id	= vwResource.Resrc_id		-- FK1
AND	tblPerson.Person_tp	= vwResource.Resrc_tp
INNER
JOIN	[pics_alpha1].[vwPersonType]
ON	tblPerson.Person_tp	= vwPersonType.Person_tp		-- FK2

;
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
DROP VIEW IF EXISTS	`vwReport`
;

CREATE OR REPLACE VIEW	`vwReport`
(
	Report_id
,	Report_tp
,	Report_nm
,	Report_cd
,	Report_tx
,	ReportADD_dm
,	ReportADD_nm
,	ReportUPD_dm
,	ReportUPD_nm
,	ReportDEL_dm
,	ReportDEL_nm
,	ParentReport_tp
,	ReportType_tx
,	ReportTypeLeft_id
,	ReportTypeRight_id
,	ReportTypeLevel_id
,	ReportTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwReport
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
	tblReport.Report_id
,	tblReport.Report_tp
,	tblReport.Report_nm
,	tblReport.Report_cd
,	vwResource.Resrc_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwReportType.ParentReport_tp
,	vwReportType.ReportType_tx
,	vwReportType.ReportTypeLeft_id
,	vwReportType.ReportTypeRight_id
,	vwReportType.ReportTypeLevel_id
,	vwReportType.ReportTypeOrder_id
FROM
	[pics_alpha1].[tblReport]
INNER
JOIN	[pics_alpha1].[vwResource]
ON	tblReport.Report_id	= vwResource.Resrc_id		-- FK1
AND	tblReport.Report_tp	= vwResource.Resrc_tp
INNER
JOIN	[pics_alpha1].[vwReportType]
ON	tblReport.Report_tp	= vwReportType.Report_tp		-- FK2

;
DROP VIEW IF EXISTS	`vwReportType`
;

CREATE OR REPLACE VIEW	`vwReportType`
(
	Report_tp
,	ParentReport_tp
,	ReportType_tx
,	ReportTypeLeft_id
,	ReportTypeRight_id
,	ReportTypeLevel_id
,	ReportTypeOrder_id
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwReportType
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
	tblReportType.Report_tp
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	[pics_alpha1].[tblReportType]
INNER
JOIN	[pics_alpha1].[tblResourceType]
ON	tblReportType.Report_tp	= tblResourceType.Resrc_tp		-- FK1

;
DROP VIEW IF EXISTS	`vwResource`
;

CREATE OR REPLACE VIEW	`vwResource`
(
	Resrc_id
,	Resrc_tp
,	Resrc_nm
,	Resrc_tx
,	ADD_dm
,	ADD_nm
,	UPD_dm
,	UPD_nm
,	DEL_dm
,	DEL_nm
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
**	Name:		vwResource
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
	tblResource.Resrc_id
,	tblResource.Resrc_tp
,	tblResource.Resrc_nm
,	tblResource.Resrc_tx
,	tblResource.ADD_dm
,	tblResource.ADD_nm
,	tblResource.UPD_dm
,	tblResource.UPD_nm
,	tblResource.DEL_dm
,	tblResource.DEL_nm
,	tblResourceType.ParentResrc_tp
,	tblResourceType.ResrcType_tx
,	tblResourceType.Left_id
,	tblResourceType.Right_id
,	tblResourceType.Level_id
,	tblResourceType.Order_id
FROM
	[pics_alpha1].[tblResource]
INNER
JOIN	[pics_alpha1].[tblResourceType]
ON	tblResource.Resrc_tp	= tblResourceType.Resrc_tp		-- FK1

;
DROP VIEW IF EXISTS	`vwResource_RightType`
;

CREATE OR REPLACE VIEW	`vwResource_RightType`
(
	Resrc_id
,	Resrc_tp
,	Right_tp
,	Resrc_nm
,	Resrc_tx
,	ParentResrc_tp
,	ResrcType_tx
,	ADD_dm
,	ADD_nm
,	UPD_dm
,	UPD_nm
,	DEL_dm
,	DEL_nm
,	ParentRight_tp
,	RightType_tx
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwResource_RightType
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
	tblResource_RightType.Resrc_id
,	tblResource_RightType.Resrc_tp
,	tblResource_RightType.Right_tp
,	vwResource.Resrc_nm
,	vwResource.Resrc_tx
,	vwResource.ParentResrc_tp
,	vwResource.ResrcType_tx
,	vwResource.ADD_dm
,	vwResource.ADD_nm
,	vwResource.UPD_dm
,	vwResource.UPD_nm
,	vwResource.DEL_dm
,	vwResource.DEL_nm
,	vwRightType.ParentRight_tp
,	vwRightType.RightType_tx
FROM
	[pics_alpha1].[tblResource_RightType]
INNER
JOIN	[pics_alpha1].[vwResource]
ON	tblResource_RightType.Resrc_id	= vwResource.Resrc_id		-- FK1
AND	tblResource_RightType.Resrc_tp	= vwResource.Resrc_tp
INNER
JOIN	[pics_alpha1].[vwRightType]
ON	tblResource_RightType.Right_tp	= vwRightType.Right_tp		-- FK2

;
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
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
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
	[pics_alpha1].[tblResourceType]

;
DROP VIEW IF EXISTS	`vwResourceType_RightType`
;

CREATE OR REPLACE VIEW	`vwResourceType_RightType`
(
	Resrc_tp
,	Right_tp
,	ParentResrc_tp
,	ResrcType_tx
,	ParentRight_tp
,	RightType_tx
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwResourceType_RightType
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
	tblResourceType_RightType.Resrc_tp
,	tblResourceType_RightType.Right_tp
,	vwResourceType.ParentResrc_tp
,	vwResourceType.ResrcType_tx
,	vwRightType.ParentRight_tp
,	vwRightType.RightType_tx
FROM
	[pics_alpha1].[tblResourceType_RightType]
INNER
JOIN	[pics_alpha1].[vwResourceType]
ON	tblResourceType_RightType.Resrc_tp	= vwResourceType.Resrc_tp		-- FK1
INNER
JOIN	[pics_alpha1].[vwRightType]
ON	tblResourceType_RightType.Right_tp	= vwRightType.Right_tp		-- FK2

;
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
DROP VIEW IF EXISTS	`vwUser_Person`
;

CREATE OR REPLACE VIEW	`vwUser_Person`
(
	User_id
,	User_tp
,	User_nm
,	Domain_nm
,	Password_cd
,	Email_tx
,	Person_id
,	Person_tp
,	Person_nm
,	First_nm
,	Middle_nm
,	Last_nm
,	FirstSNDX_cd
,	LastSNDX_cd
,	Birth_dm
,	Gender_cd
,	User_tx
,	Person_tx
,	UserADD_dm
,	UserADD_nm
,	UserUPD_dm
,	UserUPD_nm
,	UserDEL_dm
,	UserDEL_nm
,	ParentUser_tp
,	UserType_tx
,	PersonADD_dm
,	PersonADD_nm
,	PersonUPD_dm
,	PersonUPD_nm
,	PersonDEL_dm
,	PersonDEL_nm
,	ParentPerson_tp
,	PersonType_tx
)
-- WITH ENCRYPTION
AS
/*
**	Name:		vwUser_Person
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
	tblUser_Person.User_id
,	tblUser_Person.User_tp
,	vwUser.User_nm
,	vwUser.Domain_nm
,	vwUser.Password_cd
,	vwUser.Email_tx
,	tblUser_Person.Person_id
,	tblUser_Person.Person_tp
,	vwPerson.Person_nm
,	vwPerson.First_nm
,	vwPerson.Middle_nm
,	vwPerson.Last_nm
,	vwPerson.FirstSNDX_cd
,	vwPerson.LastSNDX_cd
,	vwPerson.Birth_dm
,	vwPerson.Gender_cd
,	vwUser.User_tx
,	vwPerson.Person_tx
,	vwUser.UserADD_dm
,	vwUser.UserADD_nm
,	vwUser.UserUPD_dm
,	vwUser.UserUPD_nm
,	vwUser.UserDEL_dm
,	vwUser.UserDEL_nm
,	vwUser.ParentUser_tp
,	vwUser.UserType_tx
,	vwPerson.PersonADD_dm
,	vwPerson.PersonADD_nm
,	vwPerson.PersonUPD_dm
,	vwPerson.PersonUPD_nm
,	vwPerson.PersonDEL_dm
,	vwPerson.PersonDEL_nm
,	vwPerson.ParentPerson_tp
,	vwPerson.PersonType_tx
FROM
	[pics_alpha1].[tblUser_Person]
INNER
JOIN	[pics_alpha1].[vwUser]
ON	tblUser_Person.User_id	= vwUser.User_id		-- FK1
AND	tblUser_Person.User_tp	= vwUser.User_tp
INNER
JOIN	[pics_alpha1].[vwPerson]
ON	tblUser_Person.Person_id	= vwPerson.Person_id		-- FK2
AND	tblUser_Person.Person_tp	= vwPerson.Person_tp

;
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

