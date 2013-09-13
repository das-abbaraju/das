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

