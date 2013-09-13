DROP TABLE IF EXISTS	`tblRole_ResourceType_RightType`
;

/*
**	Name:		tblRole_ResourceType_RightType
**	Type:		Database Table
**	Purpose:	To hold tblRole_ResourceType_RightType data.
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblRole_ResourceType_RightType`
(
	Role_id		int signed		NOT NULL
,	Role_tp		varchar(80)		NOT NULL
,	Resrc_tp		varchar(80)		NOT NULL
,	Right_tp		varchar(80)		NOT NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

