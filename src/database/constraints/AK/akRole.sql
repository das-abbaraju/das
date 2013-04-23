ALTER TABLE `tblRole`
DROP INDEX `akRole`
;

/*
**	Name:		tblRole
**	Type:		Constraint: Alternate Key
**	Purpose:	To constrain tblRole "meaningful" alternate Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRole`
ADD CONSTRAINT	`akRole`	UNIQUE 
(
	Role_tp		
,	Role_nm		

)
;

