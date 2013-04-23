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

