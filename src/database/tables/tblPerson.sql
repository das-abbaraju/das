DROP TABLE IF EXISTS	`tblPerson`
;

/*
**	Name:		tblPerson
**	Type:		Database Table
**	Purpose:	To hold tblPerson data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblPerson`
(
	Person_id		int signed		NOT NULL
,	Person_tp		varchar(80)		NOT NULL
,	Person_nm		varchar(128)		NOT NULL
,	First_nm		varchar(128)		NULL
,	Last_nm		varchar(128)		NULL
,	Middle_nm		varchar(128)		NULL
,	Gender_cd		varchar(48)		NULL
,	FirstSNDX_cd		varchar(48)		NULL
,	LastSNDX_cd		varchar(48)		NULL
,	Birth_dm		datetime		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

