ALTER TABLE `tblResource_RightType`
DROP INDEX `pkResource_RightType`
;

/*
**	Name:		tblResource_RightType
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResource_RightType "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResource_RightType`
ADD CONSTRAINT	`pkResource_RightType`	PRIMARY KEY
(
	Resrc_id		
,	Resrc_tp		
,	Right_tp		

)
;

