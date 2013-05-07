DROP TABLE IF EXISTS	`tblItem`
;

/*
**	Name:		tblItem
**	Type:		Database Table
**	Purpose:	To hold tblItem data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblItem`
(
	Item_id		int signed		NOT NULL
,	Item_tp		varchar(64)		NOT NULL
,	Item_nm		varchar(256)		NOT NULL
,	Item_cd		varchar(128)		NULL

)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
;

