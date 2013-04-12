DROP TABLE IF EXISTS	`tblUser_Role`
;

/*
**	Name:		tblUser_Role
**	Type:		Database Table
**	Purpose:	To hold tblUser_Role data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblUser_Role`
(
	User_id		int signed		NOT NULL
,	User_tp		varchar(80)		NOT NULL
,	Role_id		int signed		NOT NULL
,	Role_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

