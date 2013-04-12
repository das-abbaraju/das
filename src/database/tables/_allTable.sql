DROP TABLE IF EXISTS	`tblPerson`
;

/*
**	Name:		tblPerson
**	Type:		Database Table
**	Purpose:	To hold tblPerson data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblPerson`
(
	Person_id		int signed		NOT NULL
,	Person_tp		varchar(80)		NOT NULL
,	Person_nm		varchar(128)		NOT NULL
,	First_nm		varchar(128)		NULL
,	Last_nm		varchar(128)		NULL
,	Middle_nm		varchar(128)		NULL
,	Gender_cd		varchar(48)		NULL
,	FirstSNDX_cd		varchar(48)		NULL
,	LastSNDX_cd		varchar(48)		NULL
,	Birth_dm		datetime		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblPersonType`
;

/*
**	Name:		tblPersonType
**	Type:		Database Table
**	Purpose:	To hold tblPersonType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblPersonType`
(
	Person_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblReport`
;

/*
**	Name:		tblReport
**	Type:		Database Table
**	Purpose:	To hold tblReport data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblReport`
(
	Report_id		int signed		NOT NULL
,	Report_tp		varchar(80)		NOT NULL
,	Report_nm		varchar(128)		NOT NULL
,	Report_cd		varchar(48)		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblReportType`
;

/*
**	Name:		tblReportType
**	Type:		Database Table
**	Purpose:	To hold tblReportType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblReportType`
(
	Report_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblResource`
;

/*
**	Name:		tblResource
**	Type:		Database Table
**	Purpose:	To hold tblResource data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblResource`
(
	Resrc_id		int signed		IDENTITY
,	Resrc_tp		varchar(80)		NOT NULL
,	Resrc_nm		varchar(128)		NOT NULL
,	Resrc_tx		mediumtext		NULL
,	ADD_dm		datetime		NOT NULL
,	ADD_nm		varchar(128)		NOT NULL
,	UPD_dm		datetime		NULL
,	UPD_nm		varchar(128)		NULL
,	DEL_dm		datetime		NULL
,	DEL_nm		varchar(128)		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblResourceType`
;

/*
**	Name:		tblResourceType
**	Type:		Database Table
**	Purpose:	To hold tblResourceType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblResourceType`
(
	Resrc_tp		varchar(80)		NOT NULL
,	ParentResrc_tp		varchar(80)		NULL
,	ResrcType_tx		mediumtext		NULL
,	Left_id		int signed		NULL
,	Right_id		int signed		NULL
,	Level_id		int signed		NULL
,	Order_id		int signed		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblRightType`
;

/*
**	Name:		tblRightType
**	Type:		Database Table
**	Purpose:	To hold tblRightType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblRightType`
(
	Right_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblRole`
;

/*
**	Name:		tblRole
**	Type:		Database Table
**	Purpose:	To hold tblRole data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblRole`
(
	Role_id		int signed		NOT NULL
,	Role_tp		varchar(80)		NOT NULL
,	Role_nm		varchar(128)		NOT NULL
,	Role_cd		varchar(48)		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblRoleType`
;

/*
**	Name:		tblRoleType
**	Type:		Database Table
**	Purpose:	To hold tblRoleType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblRoleType`
(
	Role_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblUser`
;

/*
**	Name:		tblUser
**	Type:		Database Table
**	Purpose:	To hold tblUser data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblUser`
(
	User_id		int signed		NOT NULL
,	User_tp		varchar(80)		NOT NULL
,	User_nm		varchar(128)		NOT NULL
,	Password_cd		varchar(48)		NULL
,	Domain_nm		varchar(128)		NULL
,	Email_tx		mediumtext		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblUserType`
;

/*
**	Name:		tblUserType
**	Type:		Database Table
**	Purpose:	To hold tblUserType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblUserType`
(
	User_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblResource_RightType`
;

/*
**	Name:		tblResource_RightType
**	Type:		Database Table
**	Purpose:	To hold tblResource_RightType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblResource_RightType`
(
	Resrc_id		int signed		NOT NULL
,	Resrc_tp		varchar(80)		NOT NULL
,	Right_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblResourceType_RightType`
;

/*
**	Name:		tblResourceType_RightType
**	Type:		Database Table
**	Purpose:	To hold tblResourceType_RightType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblResourceType_RightType`
(
	Resrc_tp		varchar(80)		NOT NULL
,	Right_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblRole_Resource_RightType`
;

/*
**	Name:		tblRole_Resource_RightType
**	Type:		Database Table
**	Purpose:	To hold tblRole_Resource_RightType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblRole_Resource_RightType`
(
	Role_id		int signed		NOT NULL
,	Role_tp		varchar(80)		NOT NULL
,	Resrc_id		int signed		NOT NULL
,	Resrc_tp		varchar(80)		NOT NULL
,	Right_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblRole_ResourceType_RightType`
;

/*
**	Name:		tblRole_ResourceType_RightType
**	Type:		Database Table
**	Purpose:	To hold tblRole_ResourceType_RightType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblRole_ResourceType_RightType`
(
	Role_id		int signed		NOT NULL
,	Role_tp		varchar(80)		NOT NULL
,	Resrc_tp		varchar(80)		NOT NULL
,	Right_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblUser_Person`
;

/*
**	Name:		tblUser_Person
**	Type:		Database Table
**	Purpose:	To hold tblUser_Person data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblUser_Person`
(
	User_id		int signed		NOT NULL
,	User_tp		varchar(80)		NOT NULL
,	Person_id		int signed		NOT NULL
,	Person_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;
DROP TABLE IF EXISTS	`tblUser_Role`
;

/*
**	Name:		tblUser_Role
**	Type:		Database Table
**	Purpose:	To hold tblUser_Role data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblUser_Role`
(
	User_id		int signed		NOT NULL
,	User_tp		varchar(80)		NOT NULL
,	Role_id		int signed		NOT NULL
,	Role_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

