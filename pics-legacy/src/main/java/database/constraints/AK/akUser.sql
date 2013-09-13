ALTER TABLE `tblUser`
DROP INDEX `akUser`
;

/*
**	Name:		tblUser
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblUser "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblUser`
ADD CONSTRAINT	`akUser`	UNIQUE 
(
	User_tp		
,	User_nm		

)
;

