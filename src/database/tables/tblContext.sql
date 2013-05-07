DROP TABLE IF EXISTS	`tblContext`
;

/*
**	Name:		tblContext
**	Type:		Database Table
**	Purpose:	To hold tblContext data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblContext`
(
	Context_id		int signed		NOT NULL
,	Context_tp		varchar(64)		NOT NULL
,	Context_nm		varchar(256)		NOT NULL
,	Context_cd		varchar(128)		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

