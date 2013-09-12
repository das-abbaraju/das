DROP PROCEDURE IF EXISTS	`rspItem_Locale`
;

DELIMITER //
CREATE PROCEDURE	rspItem_Locale
(
	Item_id		int signed		
,	Item_tp		varchar(64)		
,	Locale_cd		varchar(128)		
,	Entry_tp		varchar(64)		
,	Entry_tx		text		
,	EFF_dm		datetime		
,	USE_dm		datetime		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspItem_Locale
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblItem_Locale
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
	IF Locale_cd IS NULL OR Locale_cd = '' THEN SET Locale_cd = '-2147483647';	END IF;
	IF Entry_tp IS NULL OR Entry_tp = '' THEN SET Entry_tp = '-2147483647';	END IF;
	IF Entry_tx IS NULL OR Entry_tx = '' THEN SET Entry_tx = '-2147483647';	END IF;
	IF EFF_dm IS NULL OR EFF_dm = '' THEN SET EFF_dm = '0000-00-00 00:00:00';	END IF;
	IF USE_dm IS NULL OR USE_dm = '' THEN SET USE_dm = '0000-00-00 00:00:00';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem_Locale
			WHERE
				tblItem_Locale.Item_id	= Item_id
			AND	tblItem_Locale.Item_tp	= Item_tp
			AND	tblItem_Locale.Locale_cd	= Locale_cd

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
			FROM	tblItem_Locale
			WHERE
				tblItem_Locale.Item_id	= Item_id
			AND	tblItem_Locale.Item_tp	= Item_tp

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
			FROM	tblItem_Locale
			WHERE
				tblItem_Locale.Locale_cd	= Locale_cd

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
			FROM	tblItem_Locale
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
				Locale_cd	= Locale_cd
			OR	Locale_cd	= '-2147483647'
				)
			AND	(
				Entry_tp	= Entry_tp
			OR	Entry_tp	= '-2147483647'
				)
			AND	(
				Entry_tx	LIKE Entry_tx
			OR	Entry_tx	LIKE '-2147483647'
				)
			AND	(
				EFF_dm	= EFF_dm
			OR	EFF_dm	= '0000-00-00 00:00:00'
				)
			AND	(
				USE_dm	= USE_dm
			OR	USE_dm	= '0000-00-00 00:00:00'
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

