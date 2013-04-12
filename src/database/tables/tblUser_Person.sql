DROP TABLE IF EXISTS	`tblUser_Person`
;

/*
**	Name:		tblUser_Person
**	Type:		Database Table
**	Purpose:	To hold tblUser_Person data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblUser_Person`
(
	User_id		int signed		NOT NULL
,	User_tp		varchar(80)		NOT NULL
,	Person_id		int signed		NOT NULL
,	Person_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

