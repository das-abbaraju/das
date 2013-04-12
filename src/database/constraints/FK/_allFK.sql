ALTER TABLE `tblRole_Resource_RightType`
DROP INDEX `fk1Role_Resource_RightType`
;

/*
**	Name:		tblRole_Resource_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRole_Resource_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRole_Resource_RightType`
ADD	CONSTRAINT	`fk1Role_Resource_RightType`	FOREIGN KEY
(
	Role_id		
,	Role_tp		

)
	REFERENCES	`tblRole`
(
	Role_id		
,	Role_tp		

)
;
ALTER TABLE `tblPersonType`
DROP INDEX `fk1PersonType`
;

/*
**	Name:		tblPersonType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblPersonType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblPersonType`
ADD	CONSTRAINT	`fk1PersonType`	FOREIGN KEY
(
	Person_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
ALTER TABLE `tblReport`
DROP INDEX `fk1Report`
;

/*
**	Name:		tblReport
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblReport foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblReport`
ADD	CONSTRAINT	`fk1Report`	FOREIGN KEY
(
	Report_id		
,	Report_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;
ALTER TABLE `tblReportType`
DROP INDEX `fk1ReportType`
;

/*
**	Name:		tblReportType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblReportType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblReportType`
ADD	CONSTRAINT	`fk1ReportType`	FOREIGN KEY
(
	Report_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
ALTER TABLE `tblResource`
DROP INDEX `fk1Resource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblResource foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblResource`
ADD	CONSTRAINT	`fk1Resource`	FOREIGN KEY
(
	Resrc_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
ALTER TABLE `tblResource_RightType`
DROP INDEX `fk1Resource_RightType`
;

/*
**	Name:		tblResource_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblResource_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblResource_RightType`
ADD	CONSTRAINT	`fk1Resource_RightType`	FOREIGN KEY
(
	Resrc_id		
,	Resrc_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;
ALTER TABLE `tblResourceType_RightType`
DROP INDEX `fk1ResourceType_RightType`
;

/*
**	Name:		tblResourceType_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblResourceType_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblResourceType_RightType`
ADD	CONSTRAINT	`fk1ResourceType_RightType`	FOREIGN KEY
(
	Resrc_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
ALTER TABLE `tblRightType`
DROP INDEX `fk1RightType`
;

/*
**	Name:		tblRightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRightType`
ADD	CONSTRAINT	`fk1RightType`	FOREIGN KEY
(
	Right_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
ALTER TABLE `tblPerson`
DROP INDEX `fk1Person`
;

/*
**	Name:		tblPerson
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblPerson foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblPerson`
ADD	CONSTRAINT	`fk1Person`	FOREIGN KEY
(
	Person_id		
,	Person_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;
ALTER TABLE `tblRole`
DROP INDEX `fk1Role`
;

/*
**	Name:		tblRole
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRole foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRole`
ADD	CONSTRAINT	`fk1Role`	FOREIGN KEY
(
	Role_id		
,	Role_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;
ALTER TABLE `tblUserType`
DROP INDEX `fk1UserType`
;

/*
**	Name:		tblUserType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUserType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUserType`
ADD	CONSTRAINT	`fk1UserType`	FOREIGN KEY
(
	User_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
ALTER TABLE `tblUser`
DROP INDEX `fk1User`
;

/*
**	Name:		tblUser
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUser foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUser`
ADD	CONSTRAINT	`fk1User`	FOREIGN KEY
(
	User_id		
,	User_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;
ALTER TABLE `tblRole_ResourceType_RightType`
DROP INDEX `fk1Role_ResourceType_RightType`
;

/*
**	Name:		tblRole_ResourceType_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRole_ResourceType_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRole_ResourceType_RightType`
ADD	CONSTRAINT	`fk1Role_ResourceType_RightType`	FOREIGN KEY
(
	Role_id		
,	Role_tp		

)
	REFERENCES	`tblRole`
(
	Role_id		
,	Role_tp		

)
;
ALTER TABLE `tblUser_Role`
DROP INDEX `fk1User_Role`
;

/*
**	Name:		tblUser_Role
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUser_Role foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUser_Role`
ADD	CONSTRAINT	`fk1User_Role`	FOREIGN KEY
(
	User_id		
,	User_tp		

)
	REFERENCES	`tblUser`
(
	User_id		
,	User_tp		

)
;
ALTER TABLE `tblRoleType`
DROP INDEX `fk1RoleType`
;

/*
**	Name:		tblRoleType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRoleType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRoleType`
ADD	CONSTRAINT	`fk1RoleType`	FOREIGN KEY
(
	Role_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
ALTER TABLE `tblUser_Person`
DROP INDEX `fk1User_Person`
;

/*
**	Name:		tblUser_Person
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUser_Person foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUser_Person`
ADD	CONSTRAINT	`fk1User_Person`	FOREIGN KEY
(
	User_id		
,	User_tp		

)
	REFERENCES	`tblUser`
(
	User_id		
,	User_tp		

)
;
ALTER TABLE `tblPerson`
DROP INDEX `fk2Person`
;

/*
**	Name:		tblPerson
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblPerson foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblPerson`
ADD	CONSTRAINT	`fk2Person`	FOREIGN KEY
(
	Person_tp		

)
	REFERENCES	`tblPersonType`
(
	Person_tp		

)
;
ALTER TABLE `tblUser_Role`
DROP INDEX `fk2User_Role`
;

/*
**	Name:		tblUser_Role
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUser_Role foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUser_Role`
ADD	CONSTRAINT	`fk2User_Role`	FOREIGN KEY
(
	Role_id		
,	Role_tp		

)
	REFERENCES	`tblRole`
(
	Role_id		
,	Role_tp		

)
;
ALTER TABLE `tblReport`
DROP INDEX `fk2Report`
;

/*
**	Name:		tblReport
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblReport foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblReport`
ADD	CONSTRAINT	`fk2Report`	FOREIGN KEY
(
	Report_tp		

)
	REFERENCES	`tblReportType`
(
	Report_tp		

)
;
ALTER TABLE `tblUser_Person`
DROP INDEX `fk2User_Person`
;

/*
**	Name:		tblUser_Person
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUser_Person foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUser_Person`
ADD	CONSTRAINT	`fk2User_Person`	FOREIGN KEY
(
	Person_id		
,	Person_tp		

)
	REFERENCES	`tblPerson`
(
	Person_id		
,	Person_tp		

)
;
ALTER TABLE `tblRole`
DROP INDEX `fk2Role`
;

/*
**	Name:		tblRole
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRole foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRole`
ADD	CONSTRAINT	`fk2Role`	FOREIGN KEY
(
	Role_tp		

)
	REFERENCES	`tblRoleType`
(
	Role_tp		

)
;
ALTER TABLE `tblResource_RightType`
DROP INDEX `fk2Resource_RightType`
;

/*
**	Name:		tblResource_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblResource_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblResource_RightType`
ADD	CONSTRAINT	`fk2Resource_RightType`	FOREIGN KEY
(
	Right_tp		

)
	REFERENCES	`tblRightType`
(
	Right_tp		

)
;
ALTER TABLE `tblResourceType_RightType`
DROP INDEX `fk2ResourceType_RightType`
;

/*
**	Name:		tblResourceType_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblResourceType_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblResourceType_RightType`
ADD	CONSTRAINT	`fk2ResourceType_RightType`	FOREIGN KEY
(
	Right_tp		

)
	REFERENCES	`tblRightType`
(
	Right_tp		

)
;
ALTER TABLE `tblRole_ResourceType_RightType`
DROP INDEX `fk2Role_ResourceType_RightType`
;

/*
**	Name:		tblRole_ResourceType_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRole_ResourceType_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRole_ResourceType_RightType`
ADD	CONSTRAINT	`fk2Role_ResourceType_RightType`	FOREIGN KEY
(
	Resrc_tp		

)
	REFERENCES	`tblResourceType`
(
	Resrc_tp		

)
;
ALTER TABLE `tblRole_Resource_RightType`
DROP INDEX `fk2Role_Resource_RightType`
;

/*
**	Name:		tblRole_Resource_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRole_Resource_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRole_Resource_RightType`
ADD	CONSTRAINT	`fk2Role_Resource_RightType`	FOREIGN KEY
(
	Resrc_id		
,	Resrc_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;
ALTER TABLE `tblUser`
DROP INDEX `fk2User`
;

/*
**	Name:		tblUser
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblUser foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblUser`
ADD	CONSTRAINT	`fk2User`	FOREIGN KEY
(
	User_tp		

)
	REFERENCES	`tblUserType`
(
	User_tp		

)
;
ALTER TABLE `tblRole_Resource_RightType`
DROP INDEX `fk3Role_Resource_RightType`
;

/*
**	Name:		tblRole_Resource_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRole_Resource_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRole_Resource_RightType`
ADD	CONSTRAINT	`fk3Role_Resource_RightType`	FOREIGN KEY
(
	Resrc_id		
,	Resrc_tp		
,	Right_tp		

)
	REFERENCES	`tblResource_RightType`
(
	Resrc_id		
,	Resrc_tp		
,	Right_tp		

)
;
ALTER TABLE `tblRole_ResourceType_RightType`
DROP INDEX `fk3Role_ResourceType_RightType`
;

/*
**	Name:		tblRole_ResourceType_RightType
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblRole_ResourceType_RightType foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblRole_ResourceType_RightType`
ADD	CONSTRAINT	`fk3Role_ResourceType_RightType`	FOREIGN KEY
(
	Resrc_tp		
,	Right_tp		

)
	REFERENCES	`tblResourceType_RightType`
(
	Resrc_tp		
,	Right_tp		

)
;

