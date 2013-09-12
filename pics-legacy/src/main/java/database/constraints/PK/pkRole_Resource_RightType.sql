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

