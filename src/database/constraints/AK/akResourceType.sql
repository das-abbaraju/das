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

