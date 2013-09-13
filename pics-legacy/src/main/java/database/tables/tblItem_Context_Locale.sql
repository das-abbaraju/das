DROP TABLE IF EXISTS	`tblItem_Context_Locale`
;

/*
**	Name:		tblItem_Context_Locale
**	Type:		Database Table
**	Purpose:	To hold tblItem_Context_Locale data.
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
CREATE TABLE IF NOT EXISTS	`tblItem_Context_Locale`
(
	Item_id		int signed		NOT NULL
,	Item_tp		varchar(64)		NOT NULL
,	Context_id		int signed		NOT NULL
,	Context_tp		varchar(64)		NOT NULL
,	Locale_cd		varchar(128)		NOT NULL
,	ItemEntry_tx		text		NULL

)
ENGINE = MYISAM
DEFAULT CHARACTER SET = utf8
;

