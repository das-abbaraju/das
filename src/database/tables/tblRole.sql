DROP TABLE IF EXISTS	`tblRole`
;

/*
**	Name:		tblRole
**	Type:		Database Table
**	Purpose:	To hold tblRole data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblRole`
(
	Role_id		int signed		NOT NULL
,	Role_tp		varchar(80)		NOT NULL
,	Role_nm		varchar(128)		NOT NULL
,	Role_cd		varchar(48)		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

