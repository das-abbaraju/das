DROP TABLE IF EXISTS	`tblResource_RightType`
;

/*
**	Name:		tblResource_RightType
**	Type:		Database Table
**	Purpose:	To hold tblResource_RightType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblResource_RightType`
(
	Resrc_id		int signed		NOT NULL
,	Resrc_tp		varchar(80)		NOT NULL
,	Right_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

