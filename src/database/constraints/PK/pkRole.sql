ALTER TABLE `tblRole`
DROP INDEX `pkRole`
;

/*
**	Name:		tblRole
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblRole "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`tblRole`
ADD CONSTRAINT	`pkRole`	PRIMARY KEY
(
	Role_id		
,	Role_tp		

)
;

