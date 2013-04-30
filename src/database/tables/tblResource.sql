DROP TABLE IF EXISTS	`tblResource`
;

/*
**	Name:		tblResource
**	Type:		Database Table
**	Purpose:	To hold tblResource data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblResource`
(
	Resrc_id		int signed		NOT NULL
,	Resrc_tp		varchar(64)		NOT NULL
,	Resrc_nm		varchar(256)		NOT NULL
,	Resrc_tx		mediumtext		NULL
,	ADD_dm		datetime		NOT NULL
,	ADD_nm		varchar(256)		NOT NULL
,	UPD_dm		datetime		NULL
,	UPD_nm		varchar(256)		NULL
,	DEL_dm		datetime		NULL
,	DEL_nm		varchar(256)		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

