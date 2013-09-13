ALTER TABLE `tblResource`
DROP INDEX `pkResource`
;

/*
**	Name:		tblResource
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain tblResource "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
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
ALTER TABLE	`pics_alpha1`.`tblresource`
CHANGE
	`Resrc_id`	`Resrc_id`	INT(10) UNSIGNED NOT NULL AUTO_INCREMENT
;

