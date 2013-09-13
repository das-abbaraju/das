ALTER TABLE `tblContext`
DROP INDEX `akContext`
;

/*
**	Name:		tblContext
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblContext "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblContext`
ADD CONSTRAINT	`akContext`	UNIQUE 
(
	Context_tp		
,	Context_nm		

)
;

