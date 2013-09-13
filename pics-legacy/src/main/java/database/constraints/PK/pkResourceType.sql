ALTER TABLE `tblResourceType`
DROP INDEX `pkResourceType`
;

/*
**	Name:		tblResourceType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResourceType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
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

