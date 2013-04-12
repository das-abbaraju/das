ALTER TABLE `tblResource`
DROP INDEX `akResource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblResource "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblResource`
ADD CONSTRAINT	`akResource`	UNIQUE 
(
	Resrc_nm		
,	Resrc_tp		

)
;

