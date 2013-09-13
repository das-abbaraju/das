DROP TABLE IF EXISTS	`tblResourceType`
;

/*
**	Name:		tblResourceType
**	Type:		Database Table
**	Purpose:	To hold tblResourceType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblResourceType`
(
	Resrc_tp		varchar(64)		NOT NULL
,	ParentResrc_tp		varchar(64)		NULL
,	ResrcType_tx		mediumtext		NULL
,	Left_id		int signed		NULL
,	Right_id		int signed		NULL
,	Level_id		int signed		NULL
,	Order_id		int signed		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

