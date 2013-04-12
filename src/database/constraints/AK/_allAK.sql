ALTER TABLE `tblPerson`
DROP INDEX `akPerson`
;

/*
**	Name:		tblPerson
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblPerson "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblPerson`
ADD CONSTRAINT	`akPerson`	UNIQUE 
(
	Person_tp		
,	Person_nm		

)
;
	--    NO ALTERNATE KEY DEFINED FOR tblPersonType
ALTER TABLE `tblReport`
DROP INDEX `akReport`
;

/*
**	Name:		tblReport
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblReport "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblReport`
ADD CONSTRAINT	`akReport`	UNIQUE 
(
	Report_tp		
,	Report_nm		

)
;
	--    NO ALTERNATE KEY DEFINED FOR tblReportType
ALTER TABLE `tblResource`
DROP INDEX `akResource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblResource "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResource`
ADD CONSTRAINT	`akResource`	UNIQUE 
(
	Resrc_nm		
,	Resrc_tp		

)
;
ALTER TABLE `tblResourceType`
DROP INDEX `akResourceType`
;

/*
**	Name:		tblResourceType
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblResourceType "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResourceType`
ADD CONSTRAINT	`akResourceType`	UNIQUE 
(
	Resrc_tp		

)
;
	--    NO ALTERNATE KEY DEFINED FOR tblRightType
ALTER TABLE `tblRole`
DROP INDEX `akRole`
;

/*
**	Name:		tblRole
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblRole "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRole`
ADD CONSTRAINT	`akRole`	UNIQUE 
(
	Role_tp		
,	Role_nm		

)
;
	--    NO ALTERNATE KEY DEFINED FOR tblRoleType
ALTER TABLE `tblUser`
DROP INDEX `akUser`
;

/*
**	Name:		tblUser
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblUser "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUser`
ADD CONSTRAINT	`akUser`	UNIQUE 
(
	User_tp		
,	User_nm		

)
;
	--    NO ALTERNATE KEY DEFINED FOR tblUserType
	--    NO ALTERNATE KEY DEFINED FOR tblResource_RightType
	--    NO ALTERNATE KEY DEFINED FOR tblResourceType_RightType
	--    NO ALTERNATE KEY DEFINED FOR tblRole_Resource_RightType
	--    NO ALTERNATE KEY DEFINED FOR tblRole_ResourceType_RightType
	--    NO ALTERNATE KEY DEFINED FOR tblUser_Person
	--    NO ALTERNATE KEY DEFINED FOR tblUser_Role

