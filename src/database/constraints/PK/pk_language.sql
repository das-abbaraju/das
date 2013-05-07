ALTER TABLE `app_language`
DROP INDEX `pk_language`
;

/*
**	Name:		app_language
**	Type:		Constraint: Primary Key
**	Purpose:	To constrain app_language "meaningless" primary Key data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
ALTER TABLE	`app_language`
ADD CONSTRAINT	`pk_language`	PRIMARY KEY
(
	locale		

)
;

