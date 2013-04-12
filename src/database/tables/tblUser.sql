DROP TABLE IF EXISTS	`tblUser`
;

/*
**	Name:		tblUser
**	Type:		Database Table
**	Purpose:	To hold tblUser data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblUser`
(
	User_id		int signed		NOT NULL
,	User_tp		varchar(80)		NOT NULL
,	User_nm		varchar(128)		NOT NULL
,	Password_cd		varchar(48)		NULL
,	Domain_nm		varchar(128)		NULL
,	Email_tx		mediumtext		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

