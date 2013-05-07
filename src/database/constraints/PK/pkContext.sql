ALTER TABLE `tblContext`
DROP INDEX `pkContext`
;

/*
**	Name:		tblContext
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblContext "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblContext`
ADD CONSTRAINT	`pkContext`	PRIMARY KEY
(
	Context_id		
,	Context_tp		

)
;

