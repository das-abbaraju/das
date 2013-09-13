ALTER TABLE `tblItem`
DROP INDEX `fk1Item`
;

/*
**	Name:		tblItem
**	Type:		Constraint: Foreign Key
**	Purpose:	To constrain tblItem foreign Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER	TABLE		`tblItem`
ADD	CONSTRAINT	`fk1Item`	FOREIGN KEY
(
	Item_id		
,	Item_tp		

)
	REFERENCES	`tblResource`
(
	Resrc_id		
,	Resrc_tp		

)
;

