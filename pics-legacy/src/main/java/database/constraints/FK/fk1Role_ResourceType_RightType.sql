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

