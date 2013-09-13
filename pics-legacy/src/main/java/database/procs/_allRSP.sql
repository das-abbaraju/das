DROP PROCEDURE IF EXISTS	`rsp_language`
;

DELIMITER //
CREATE PROCEDURE	rsp_language
(
	locale		varchar(128)		
,	language		varchar(128)		
,	country		varchar(128)		
,	status		varchar(128)		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rsp_language
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in app_language
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
	IF locale IS NULL OR locale = '' THEN SET locale = '-2147483647';	END IF;
	IF language IS NULL OR language = '' THEN SET language = '-2147483647';	END IF;
	IF country IS NULL OR country = '' THEN SET country = '-2147483647';	END IF;
	IF status IS NULL OR status = '' THEN SET status = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	app_language
			WHERE
				app_language.locale	= locale

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
			FROM	app_language
			WHERE
				(
				locale	= locale
			OR	locale	= '-2147483647'
				)
			AND	(
				language	= language
			OR	language	= '-2147483647'
				)
			AND	(
				country	= country
			OR	country	= '-2147483647'
				)
			AND	(
				status	= status
			OR	status	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspContext`
;

DELIMITER //
CREATE PROCEDURE	rspContext
(
	Context_id		int signed		
,	Context_tp		varchar(64)		
,	Context_nm		varchar(256)		
,	Context_cd		varchar(128)		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspContext
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblContext
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
	IF Context_id IS NULL OR Context_id = 0 THEN SET Context_id =  -2147483647;	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF Context_nm IS NULL OR Context_nm = '' THEN SET Context_nm = '-2147483647';	END IF;
	IF Context_cd IS NULL OR Context_cd = '' THEN SET Context_cd = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblContext
			WHERE
				tblContext.Context_id	= Context_id
			AND	tblContext.Context_tp	= Context_tp

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
			FROM	tblContext
			WHERE
				tblContext.Context_id	= Context_id
			AND	tblContext.Context_tp	= Context_tp

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
			FROM	tblContext
			WHERE
				tblContext.Context_tp	= Context_tp

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;


	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblContext
			WHERE
				tblContext.Context_tp	= Context_tp
			AND	tblContext.Context_nm	= Context_nm

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
			FROM	tblContext
			WHERE
				(
				Context_id	= Context_id
			OR	Context_id	=  -2147483647
				)
			AND	(
				Context_tp	= Context_tp
			OR	Context_tp	= '-2147483647'
				)
			AND	(
				Context_nm	= Context_nm
			OR	Context_nm	= '-2147483647'
				)
			AND	(
				Context_cd	= Context_cd
			OR	Context_cd	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspContextType`
;

DELIMITER //
CREATE PROCEDURE	rspContextType
(
	Context_tp		varchar(64)		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspContextType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblContextType
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
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblContextType
			WHERE
				tblContextType.Context_tp	= Context_tp

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
			FROM	tblContextType
			WHERE
				tblContextType.Context_tp	= Context_tp

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
			FROM	tblContextType
			WHERE
				(
				Context_tp	= Context_tp
			OR	Context_tp	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspItem`
;

DELIMITER //
CREATE PROCEDURE	rspItem
(
	Item_id		int signed		
,	Item_tp		varchar(64)		
,	Item_nm		varchar(256)		
,	Item_cd		varchar(128)		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspItem
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblItem
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
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem
			WHERE
				tblItem.Item_id	= Item_id
			AND	tblItem.Item_tp	= Item_tp

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
			FROM	tblItem
			WHERE
				tblItem.Item_id	= Item_id
			AND	tblItem.Item_tp	= Item_tp

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
			FROM	tblItem
			WHERE
				tblItem.Item_tp	= Item_tp

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;


	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem
			WHERE
				tblItem.Item_tp	= Item_tp
			AND	tblItem.Item_nm	= Item_nm

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
			FROM	tblItem
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
				Item_nm	= Item_nm
			OR	Item_nm	= '-2147483647'
				)
			AND	(
				Item_cd	= Item_cd
			OR	Item_cd	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspItem_Context`
;

DELIMITER //
CREATE PROCEDURE	rspItem_Context
(
	Item_id		int signed		
,	Item_tp		varchar(64)		
,	Context_id		int signed		
,	Context_tp		varchar(64)		
,	Order_id		int signed		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspItem_Context
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblItem_Context
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
	IF Order_id IS NULL OR Order_id = 0 THEN SET Order_id =  -2147483647;	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem_Context
			WHERE
				tblItem_Context.Item_id	= Item_id
			AND	tblItem_Context.Item_tp	= Item_tp
			AND	tblItem_Context.Context_id	= Context_id
			AND	tblItem_Context.Context_tp	= Context_tp

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
			FROM	tblItem_Context
			WHERE
				tblItem_Context.Item_id	= Item_id
			AND	tblItem_Context.Item_tp	= Item_tp

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
			FROM	tblItem_Context
			WHERE
				tblItem_Context.Context_id	= Context_id
			AND	tblItem_Context.Context_tp	= Context_tp

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
			FROM	tblItem_Context
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
				Order_id	= Order_id
			OR	Order_id	=  -2147483647
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
DROP PROCEDURE IF EXISTS	`rspItemType`
;

DELIMITER //
CREATE PROCEDURE	rspItemType
(
	Item_tp		varchar(64)		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspItemType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblItemType
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
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItemType
			WHERE
				tblItemType.Item_tp	= Item_tp

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
			FROM	tblItemType
			WHERE
				tblItemType.Item_tp	= Item_tp

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
			FROM	tblItemType
			WHERE
				(
				Item_tp	= Item_tp
			OR	Item_tp	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspResource`
;

DELIMITER //
CREATE PROCEDURE	rspResource
(
	Resrc_id		int signed		
,	Resrc_tp		varchar(64)		
,	Resrc_nm		varchar(256)		
,	Resrc_tx		mediumtext		
,	ADD_dm		datetime		
,	ADD_nm		varchar(256)		
,	UPD_dm		datetime		
,	UPD_nm		varchar(256)		
,	DEL_dm		datetime		
,	DEL_nm		varchar(256)		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspResource
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblResource
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
	IF Resrc_id IS NULL OR Resrc_id = 0 THEN SET Resrc_id =  -2147483647;	END IF;
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF Resrc_nm IS NULL OR Resrc_nm = '' THEN SET Resrc_nm = '-2147483647';	END IF;
	IF Resrc_tx IS NULL OR Resrc_tx = '' THEN SET Resrc_tx = '-2147483647';	END IF;
	IF ADD_dm IS NULL OR ADD_dm = '' THEN SET ADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ADD_nm IS NULL OR ADD_nm = '' THEN SET ADD_nm = '-2147483647';	END IF;
	IF UPD_dm IS NULL OR UPD_dm = '' THEN SET UPD_dm = '0000-00-00 00:00:00';	END IF;
	IF UPD_nm IS NULL OR UPD_nm = '' THEN SET UPD_nm = '-2147483647';	END IF;
	IF DEL_dm IS NULL OR DEL_dm = '' THEN SET DEL_dm = '0000-00-00 00:00:00';	END IF;
	IF DEL_nm IS NULL OR DEL_nm = '' THEN SET DEL_nm = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblResource
			WHERE
				tblResource.Resrc_id	= Resrc_id
			AND	tblResource.Resrc_tp	= Resrc_tp

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
			FROM	tblResource
			WHERE
				tblResource.Resrc_tp	= Resrc_tp

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;


	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblResource
			WHERE
				tblResource.Resrc_tp	= Resrc_tp
			AND	tblResource.Resrc_nm	= Resrc_nm

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
			FROM	tblResource
			WHERE
				(
				Resrc_id	= Resrc_id
			OR	Resrc_id	=  -2147483647
				)
			AND	(
				Resrc_tp	= Resrc_tp
			OR	Resrc_tp	= '-2147483647'
				)
			AND	(
				Resrc_nm	= Resrc_nm
			OR	Resrc_nm	= '-2147483647'
				)
			AND	(
				Resrc_tx	LIKE Resrc_tx
			OR	Resrc_tx	LIKE '-2147483647'
				)
			AND	(
				ADD_dm	= ADD_dm
			OR	ADD_dm	= '0000-00-00 00:00:00'
				)
			AND	(
				ADD_nm	= ADD_nm
			OR	ADD_nm	= '-2147483647'
				)
			AND	(
				UPD_dm	= UPD_dm
			OR	UPD_dm	= '0000-00-00 00:00:00'
				)
			AND	(
				UPD_nm	= UPD_nm
			OR	UPD_nm	= '-2147483647'
				)
			AND	(
				DEL_dm	= DEL_dm
			OR	DEL_dm	= '0000-00-00 00:00:00'
				)
			AND	(
				DEL_nm	= DEL_nm
			OR	DEL_nm	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspResourceType`
;

DELIMITER //
CREATE PROCEDURE	rspResourceType
(
	Resrc_tp		varchar(64)		
,	ParentResrc_tp		varchar(64)		
,	ResrcType_tx		mediumtext		
,	Left_id		int signed		
,	Right_id		int signed		
,	Level_id		int signed		
,	Order_id		int signed		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspResourceType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblResourceType
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
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF ParentResrc_tp IS NULL OR ParentResrc_tp = '' THEN SET ParentResrc_tp = '-2147483647';	END IF;
	IF ResrcType_tx IS NULL OR ResrcType_tx = '' THEN SET ResrcType_tx = '-2147483647';	END IF;
	IF Left_id IS NULL OR Left_id = 0 THEN SET Left_id =  -2147483647;	END IF;
	IF Right_id IS NULL OR Right_id = 0 THEN SET Right_id =  -2147483647;	END IF;
	IF Level_id IS NULL OR Level_id = 0 THEN SET Level_id =  -2147483647;	END IF;
	IF Order_id IS NULL OR Order_id = 0 THEN SET Order_id =  -2147483647;	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblResourceType
			WHERE
				tblResourceType.Resrc_tp	= Resrc_tp

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;


	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblResourceType
			WHERE
				tblResourceType.Resrc_tp	= Resrc_tp

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
			FROM	tblResourceType
			WHERE
				(
				Resrc_tp	= Resrc_tp
			OR	Resrc_tp	= '-2147483647'
				)
			AND	(
				ParentResrc_tp	= ParentResrc_tp
			OR	ParentResrc_tp	= '-2147483647'
				)
			AND	(
				ResrcType_tx	LIKE ResrcType_tx
			OR	ResrcType_tx	LIKE '-2147483647'
				)
			AND	(
				Left_id	= Left_id
			OR	Left_id	=  -2147483647
				)
			AND	(
				Right_id	= Right_id
			OR	Right_id	=  -2147483647
				)
			AND	(
				Level_id	= Level_id
			OR	Level_id	=  -2147483647
				)
			AND	(
				Order_id	= Order_id
			OR	Order_id	=  -2147483647
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

