DROP PROCEDURE IF EXISTS	`gfpItem_Context_Locale`
;

DELIMITER //
CREATE PROCEDURE	gfpItem_Context_Locale
(
	Item_id		int signed		-- PK1 
,	Item_tp		varchar(64)		-- PK2 AK1
,	Context_id		int signed		-- PK3 
,	Context_tp		varchar(64)		-- PK4 AK3
,	Locale_cd		varchar(128)		-- PK5 AK5
,	ItemEntry_tp		varchar(64)	
,	ItemEntry_tx		text	
,	Item_nm		varchar(256)		--  AK2
,	Item_cd		varchar(128)	
,	Context_nm		varchar(256)		--  AK4
,	Context_cd		varchar(128)	
,	Language_cd		varchar(128)	
,	Country_cd		varchar(128)	
,	Status_nm		varchar(128)	
,	Item_tx		mediumtext	
,	EFF_dm		datetime	
,	USE_dm		datetime	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpItem_Context_Locale
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItem_Context_Locale
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItem_Context_Locale';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItem_Context_Locale';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Item_id IS NULL OR Item_id = 0 THEN SET Item_id =  -2147483647;	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF Context_id IS NULL OR Context_id = 0 THEN SET Context_id =  -2147483647;	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF Locale_cd IS NULL OR Locale_cd = '' THEN SET Locale_cd = '-2147483647';	END IF;
	IF ItemEntry_tp IS NULL OR ItemEntry_tp = '' THEN SET ItemEntry_tp = '-2147483647';	END IF;
	IF ItemEntry_tx IS NULL OR ItemEntry_tx = '' THEN SET ItemEntry_tx = '-2147483647';	END IF;
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;
	IF Context_nm IS NULL OR Context_nm = '' THEN SET Context_nm = '-2147483647';	END IF;
	IF Context_cd IS NULL OR Context_cd = '' THEN SET Context_cd = '-2147483647';	END IF;
	IF Language_cd IS NULL OR Language_cd = '' THEN SET Language_cd = '-2147483647';	END IF;
	IF Country_cd IS NULL OR Country_cd = '' THEN SET Country_cd = '-2147483647';	END IF;
	IF Status_nm IS NULL OR Status_nm = '' THEN SET Status_nm = '-2147483647';	END IF;
	IF Item_tx IS NULL OR Item_tx = '' THEN SET Item_tx = '-2147483647';	END IF;
	IF EFF_dm IS NULL OR EFF_dm = '' THEN SET EFF_dm = '0000-00-00 00:00:00';	END IF;
	IF USE_dm IS NULL OR USE_dm = '' THEN SET USE_dm = '0000-00-00 00:00:00';	END IF;

	#######################################################################
	-- Check Security
	#######################################################################
/*	EXECUTE	RETURN		= spSecurityCheck
		SYSTABLE	= SYSTABLE
	,	SYSRIGHT	= SYSRIGHT

	IF
	(
		RETURN	<> 0
	)
	BEGIN
		EXECUTE	STATUS		= errFailedSecurity
			Proc_nm	= Proc_nm
		,	Table_nm	= SYSTABLE
		,	Action_nm	= SYSRIGHT
		RETURN	STATUS
	END
*/
	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF	Key_cd = 'PK'
	THEN
		SELECT
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			vwItem_Context_Locale.Item_id	= Item_id
		AND	vwItem_Context_Locale.Item_tp	= Item_tp
		AND	vwItem_Context_Locale.Context_id	= Context_id
		AND	vwItem_Context_Locale.Context_tp	= Context_tp
		AND	vwItem_Context_Locale.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			vwItem_Context_Locale.Item_id	= Item_id
		AND	vwItem_Context_Locale.Item_tp	= Item_tp
		AND	vwItem_Context_Locale.Context_id	= Context_id
		AND	vwItem_Context_Locale.Context_tp	= Context_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			vwItem_Context_Locale.Item_id	= Item_id
		AND	vwItem_Context_Locale.Item_tp	= Item_tp
		AND	vwItem_Context_Locale.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			vwItem_Context_Locale.Item_tp	= Item_tp
		AND	vwItem_Context_Locale.Context_tp	= Context_tp
		AND	vwItem_Context_Locale.Locale_cd	= Locale_cd
		AND	vwItem_Context_Locale.Item_nm	= Item_nm
		AND	vwItem_Context_Locale.Context_nm	= Context_nm

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Search Key lookup
	#######################################################################
	--   NO UI SEARCH KEY(S) DEFINED FOR THIS OBJECT
	#######################################################################
	-- Attribute lookup
	#######################################################################
	IF	Key_cd = 'AL'
	THEN
		SELECT
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			(
			vwItem_Context_Locale.Item_id	= Item_id
		OR	Item_id	=  -2147483647
			)
		AND	(
			vwItem_Context_Locale.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Context_id	= Context_id
		OR	Context_id	=  -2147483647
			)
		AND	(
			vwItem_Context_Locale.Context_tp	= Context_tp
		OR	Context_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Locale_cd	LIKE CONCAT('%', Locale_cd, '%')
		OR	Locale_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.ItemEntry_tp	= ItemEntry_tp
		OR	ItemEntry_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.ItemEntry_tx	LIKE CONCAT('%', ItemEntry_tx, '%')
		OR	ItemEntry_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Item_nm	LIKE CONCAT('%', Item_nm, '%')
		OR	Item_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Item_cd	LIKE CONCAT('%', Item_cd, '%')
		OR	Item_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Context_nm	LIKE CONCAT('%', Context_nm, '%')
		OR	Context_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Context_cd	LIKE CONCAT('%', Context_cd, '%')
		OR	Context_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Language_cd	LIKE CONCAT('%', Language_cd, '%')
		OR	Language_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Country_cd	LIKE CONCAT('%', Country_cd, '%')
		OR	Country_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Status_nm	LIKE CONCAT('%', Status_nm, '%')
		OR	Status_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Item_tx	LIKE CONCAT('%', Item_tx, '%')
		OR	Item_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.EFF_dm	= EFF_dm
		OR	EFF_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context_Locale.USE_dm	= USE_dm
		OR	USE_dm	= '0000-00-00 00:00:00'
			)

		;
		LEAVE GFP;
	END IF;
	#######################################################################
END	GFP
;
###############################################################################
END
//
DELIMITER ;
;

