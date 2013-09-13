ALTER TABLE `tblReport`
DROP INDEX `akReport`
;

/*
**	Name:		tblReport
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblReport "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblReport`
ADD CONSTRAINT	`akReport`	UNIQUE 
(
	Report_tp		
,	Report_nm		

)
;

