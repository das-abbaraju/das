ALTER TABLE `tblResource`
DROP INDEX `pkResource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResource "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResource`
ADD CONSTRAINT	`pkResource`	PRIMARY KEY
(
	Resrc_id		
,	Resrc_tp		

)
;

