DROP TABLE IF EXISTS	`tblItem_Locale`
;

/*
**	Name:		tblItem_Locale
**	Type:		Database Table
**	Purpose:	To hold tblItem_Locale data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblItem_Locale`
(
	Item_id		int signed		NOT NULL
,	Item_tp		varchar(64)		NOT NULL
,	Locale_cd		varchar(128)		NOT NULL
,	Entry_tp		varchar(64)		NOT NULL
,	Entry_tx		text		NULL
,	EFF_dm		datetime		NULL
,	USE_dm		datetime		NULL

)
ENGINE = MYISAM
DEFAULT CHARACTER SET = utf8
;

