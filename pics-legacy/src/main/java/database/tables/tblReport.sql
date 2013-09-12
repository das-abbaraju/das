DROP TABLE IF EXISTS	`tblReport`
;

/*
**	Name:		tblReport
**	Type:		Database Table
**	Purpose:	To hold tblReport data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblReport`
(
	Report_id		int signed		NOT NULL
,	Report_tp		varchar(80)		NOT NULL
,	Report_nm		varchar(128)		NOT NULL
,	Report_cd		varchar(48)		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

