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

