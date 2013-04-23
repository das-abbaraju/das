ALTER TABLE `tblPerson`
DROP INDEX `pkPerson`
;

/*
**	Name:		tblPerson
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblPerson "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblPerson`
ADD CONSTRAINT	`pkPerson`	PRIMARY KEY
(
	Person_id		
,	Person_tp		

)
;
ALTER TABLE `tblPersonType`
DROP INDEX `pkPersonType`
;

/*
**	Name:		tblPersonType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblPersonType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblPersonType`
ADD CONSTRAINT	`pkPersonType`	PRIMARY KEY
(
	Person_tp		

)
;
ALTER TABLE `tblReport`
DROP INDEX `pkReport`
;

/*
**	Name:		tblReport
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblReport "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblReport`
ADD CONSTRAINT	`pkReport`	PRIMARY KEY
(
	Report_id		
,	Report_tp		

)
;
ALTER TABLE `tblReportType`
DROP INDEX `pkReportType`
;

/*
**	Name:		tblReportType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblReportType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblReportType`
ADD CONSTRAINT	`pkReportType`	PRIMARY KEY
(
	Report_tp		

)
;
ALTER TABLE `tblResource`
DROP INDEX `pkResource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResource "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResource`
ADD CONSTRAINT	`pkResource`	PRIMARY KEY
(
	Resrc_id		
,	Resrc_tp		

)
;
ALTER TABLE `tblResourceType`
DROP INDEX `pkResourceType`
;

/*
**	Name:		tblResourceType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResourceType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResourceType`
ADD CONSTRAINT	`pkResourceType`	PRIMARY KEY
(
	Resrc_tp		

)
;
ALTER TABLE `tblRightType`
DROP INDEX `pkRightType`
;

/*
**	Name:		tblRightType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblRightType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRightType`
ADD CONSTRAINT	`pkRightType`	PRIMARY KEY
(
	Right_tp		

)
;
ALTER TABLE `tblRole`
DROP INDEX `pkRole`
;

/*
**	Name:		tblRole
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblRole "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRole`
ADD CONSTRAINT	`pkRole`	PRIMARY KEY
(
	Role_id		
,	Role_tp		

)
;
ALTER TABLE `tblRoleType`
DROP INDEX `pkRoleType`
;

/*
**	Name:		tblRoleType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblRoleType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRoleType`
ADD CONSTRAINT	`pkRoleType`	PRIMARY KEY
(
	Role_tp		

)
;
ALTER TABLE `tblUser`
DROP INDEX `pkUser`
;

/*
**	Name:		tblUser
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblUser "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUser`
ADD CONSTRAINT	`pkUser`	PRIMARY KEY
(
	User_id		
,	User_tp		

)
;
ALTER TABLE `tblUserType`
DROP INDEX `pkUserType`
;

/*
**	Name:		tblUserType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblUserType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUserType`
ADD CONSTRAINT	`pkUserType`	PRIMARY KEY
(
	User_tp		

)
;
ALTER TABLE `tblResource_RightType`
DROP INDEX `pkResource_RightType`
;

/*
**	Name:		tblResource_RightType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResource_RightType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResource_RightType`
ADD CONSTRAINT	`pkResource_RightType`	PRIMARY KEY
(
	Resrc_id		
,	Resrc_tp		
,	Right_tp		

)
;
ALTER TABLE `tblResourceType_RightType`
DROP INDEX `pkResourceType_RightType`
;

/*
**	Name:		tblResourceType_RightType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResourceType_RightType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResourceType_RightType`
ADD CONSTRAINT	`pkResourceType_RightType`	PRIMARY KEY
(
	Resrc_tp		
,	Right_tp		

)
;
ALTER TABLE `tblRole_Resource_RightType`
DROP INDEX `pkRole_Resource_RightType`
;

/*
**	Name:		tblRole_Resource_RightType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblRole_Resource_RightType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRole_Resource_RightType`
ADD CONSTRAINT	`pkRole_Resource_RightType`	PRIMARY KEY
(
	Role_id		
,	Role_tp		
,	Resrc_id		
,	Resrc_tp		
,	Right_tp		

)
;
ALTER TABLE `tblRole_ResourceType_RightType`
DROP INDEX `pkRole_ResourceType_RightType`
;

/*
**	Name:		tblRole_ResourceType_RightType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblRole_ResourceType_RightType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRole_ResourceType_RightType`
ADD CONSTRAINT	`pkRole_ResourceType_RightType`	PRIMARY KEY
(
	Role_id		
,	Role_tp		
,	Resrc_tp		
,	Right_tp		

)
;
ALTER TABLE `tblUser_Person`
DROP INDEX `pkUser_Person`
;

/*
**	Name:		tblUser_Person
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblUser_Person "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUser_Person`
ADD CONSTRAINT	`pkUser_Person`	PRIMARY KEY
(
	User_id		
,	User_tp		
,	Person_id		
,	Person_tp		

)
;
ALTER TABLE `tblUser_Role`
DROP INDEX `pkUser_Role`
;

/*
**	Name:		tblUser_Role
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblUser_Role "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUser_Role`
ADD CONSTRAINT	`pkUser_Role`	PRIMARY KEY
(
	User_id		
,	User_tp		
,	Role_id		
,	Role_tp		

)
;

