DROP PROCEDURE IF EXISTS	`rspItem_Context_Locale`
;

DELIMITER //
CREATE PROCEDURE	rspItem_Context_Locale
(
	Item_id		int signed		
,	Item_tp		varchar(64)		
,	Context_id		int signed		
,	Context_tp		varchar(64)		
,	Locale_cd		varchar(128)		
,	ItemEntry_tx		text		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspItem_Context_Locale
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblItem_Context_Locale
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
###############################################################################
RSP:
BEGIN
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'PK';	END IF;
	IF Item_id IS NULL OR Item_id = 0 THEN SET Item_id =  -2147483647;	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF Context_id IS NULL OR Context_id = 0 THEN SET Context_id =  -2147483647;	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF Locale_cd IS NULL OR Locale_cd = '' THEN SET Locale_cd = '-2147483647';	END IF;
	IF ItemEntry_tx IS NULL OR ItemEntry_tx = '' THEN SET ItemEntry_tx = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem_Context_Locale
			WHERE
				tblItem_Context_Locale.Item_id	= Item_id
			AND	tblItem_Context_Locale.Item_tp	= Item_tp
			AND	tblItem_Context_Locale.Context_id	= Context_id
			AND	tblItem_Context_Locale.Context_tp	= Context_tp
			AND	tblItem_Context_Locale.Locale_cd	= Locale_cd

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;

	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem_Context_Locale
			WHERE
				tblItem_Context_Locale.Item_id	= Item_id
			AND	tblItem_Context_Locale.Item_tp	= Item_tp
			AND	tblItem_Context_Locale.Context_id	= Context_id
			AND	tblItem_Context_Locale.Context_tp	= Context_tp

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;

	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd = 'FK2'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem_Context_Locale
			WHERE
				tblItem_Context_Locale.Item_id	= Item_id
			AND	tblItem_Context_Locale.Item_tp	= Item_tp
			AND	tblItem_Context_Locale.Locale_cd	= Locale_cd

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;


	#######################################################################
	-- Attribute lookup
	#######################################################################
	IF	Key_cd	= 'AL'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem_Context_Locale
			WHERE
				(
				Item_id	= Item_id
			OR	Item_id	=  -2147483647
				)
			AND	(
				Item_tp	= Item_tp
			OR	Item_tp	= '-2147483647'
				)
			AND	(
				Context_id	= Context_id
			OR	Context_id	=  -2147483647
				)
			AND	(
				Context_tp	= Context_tp
			OR	Context_tp	= '-2147483647'
				)
			AND	(
				Locale_cd	= Locale_cd
			OR	Locale_cd	= '-2147483647'
				)
			AND	(
				ItemEntry_tx	LIKE ItemEntry_tx
			OR	ItemEntry_tx	LIKE '-2147483647'
				)

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;
	#######################################################################
END	RSP
;
###############################################################################
END
//
DELIMITER ;
;

