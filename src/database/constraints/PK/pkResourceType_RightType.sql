ALTER TABLE `tblResourceType_RightType`
DROP INDEX `pkResourceType_RightType`
;

/*
**	Name:		tblResourceType_RightType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResourceType_RightType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResourceType_RightType`
ADD CONSTRAINT	`pkResourceType_RightType`	PRIMARY KEY
(
	Resrc_tp		
,	Right_tp		

)
;

