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

