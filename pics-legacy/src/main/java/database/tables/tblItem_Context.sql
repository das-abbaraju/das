DROP TABLE IF EXISTS	`tblItem_Context`
;

/*
**	Name:		tblItem_Context
**	Type:		Database Table
**	Purpose:	To hold tblItem_Context data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblItem_Context`
(
	Item_id		int signed		NOT NULL
,	Item_tp		varchar(64)		NOT NULL
,	Context_id		int signed		NOT NULL
,	Context_tp		varchar(64)		NOT NULL
,	Order_id		int signed		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

