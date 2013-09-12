DROP TABLE IF EXISTS	`app_language`
;

/*
**	Name:		app_language
**	Type:		Database Table
**	Purpose:	To hold app_language data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`app_language`
(
	locale		varchar(128)		NOT NULL
,	language		varchar(128)		NOT NULL
,	country		varchar(128)		NULL
,	status		varchar(128)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

