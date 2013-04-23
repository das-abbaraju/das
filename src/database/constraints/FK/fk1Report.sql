ALTER TABLE `tblReport`
DROP INDEX `fk1Report`
;

/*
**	Name:		tblReport
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblReport foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblReport`
ADD	CONSTRAINT	`fk1Report`	FOREIGN KEY
(
	Report_id		
,	Report_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;

