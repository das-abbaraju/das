DROP TABLE IF EXISTS	`tblRole_Resource_RightType`
;

/*
**	Name:		tblRole_Resource_RightType
**	Type:		Database Table
**	Purpose:	To hold tblRole_Resource_RightType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblRole_Resource_RightType`
(
	Role_id		int signed		NOT NULL
,	Role_tp		varchar(80)		NOT NULL
,	Resrc_id		int signed		NOT NULL
,	Resrc_tp		varchar(80)		NOT NULL
,	Right_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

