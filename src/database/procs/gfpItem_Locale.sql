DROP PROCEDURE IF EXISTS	`gfpItem_Locale`
;

DELIMITER //
CREATE PROCEDURE	gfpItem_Locale
(
	Item_id		int signed		-- PK1 
,	Item_tp		varchar(64)		-- PK2 AK1
,	Locale_cd		varchar(128)		-- PK3 AK3
,	Entry_tp		varchar(64)	
,	Entry_tx		text	
,	EFF_dm		datetime	
,	USE_dm		datetime	
,	Item_nm		varchar(256)		--  AK2
,	Item_cd		varchar(128)	
,	Language_cd		varchar(128)	
,	Country_cd		varchar(128)	
,	Status_nm		varchar(128)	
,	Item_tx		mediumtext	
,	ParentItem_tp		varchar(64)	
,	ItemType_tx		mediumtext	
,	ItemADD_dm		datetime	
,	ItemADD_nm		varchar(256)	
,	ItemUPD_dm		datetime	
,	ItemUPD_nm		varchar(256)	
,	ItemDEL_dm		datetime	
,	ItemDEL_nm		varchar(256)	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpItem_Locale
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItem_Locale
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItem_Locale';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItem_Locale';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Item_id IS NULL OR Item_id = 0 THEN SET Item_id =  -2147483647;	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF Locale_cd IS NULL OR Locale_cd = '' THEN SET Locale_cd = '-2147483647';	END IF;
	IF Entry_tp IS NULL OR Entry_tp = '' THEN SET Entry_tp = '-2147483647';	END IF;
	IF Entry_tx IS NULL OR Entry_tx = '' THEN SET Entry_tx = '-2147483647';	END IF;
	IF EFF_dm IS NULL OR EFF_dm = '' THEN SET EFF_dm = '0000-00-00 00:00:00';	END IF;
	IF USE_dm IS NULL OR USE_dm = '' THEN SET USE_dm = '0000-00-00 00:00:00';	END IF;
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;
	IF Language_cd IS NULL OR Language_cd = '' THEN SET Language_cd = '-2147483647';	END IF;
	IF Country_cd IS NULL OR Country_cd = '' THEN SET Country_cd = '-2147483647';	END IF;
	IF Status_nm IS NULL OR Status_nm = '' THEN SET Status_nm = '-2147483647';	END IF;
	IF Item_tx IS NULL OR Item_tx = '' THEN SET Item_tx = '-2147483647';	END IF;
	IF ParentItem_tp IS NULL OR ParentItem_tp = '' THEN SET ParentItem_tp = '-2147483647';	END IF;
	IF ItemType_tx IS NULL OR ItemType_tx = '' THEN SET ItemType_tx = '-2147483647';	END IF;
	IF ItemADD_dm IS NULL OR ItemADD_dm = '' THEN SET ItemADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemADD_nm IS NULL OR ItemADD_nm = '' THEN SET ItemADD_nm = '-2147483647';	END IF;
	IF ItemUPD_dm IS NULL OR ItemUPD_dm = '' THEN SET ItemUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemUPD_nm IS NULL OR ItemUPD_nm = '' THEN SET ItemUPD_nm = '-2147483647';	END IF;
	IF ItemDEL_dm IS NULL OR ItemDEL_dm = '' THEN SET ItemDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemDEL_nm IS NULL OR ItemDEL_nm = '' THEN SET ItemDEL_nm = '-2147483647';	END IF;

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
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			vwItem_Locale.Item_id	= Item_id
		AND	vwItem_Locale.Item_tp	= Item_tp
		AND	vwItem_Locale.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			vwItem_Locale.Item_id	= Item_id
		AND	vwItem_Locale.Item_tp	= Item_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			vwItem_Locale.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			vwItem_Locale.Item_tp	= Item_tp
		AND	vwItem_Locale.Locale_cd	= Locale_cd
		AND	vwItem_Locale.Item_nm	= Item_nm

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
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			(
			vwItem_Locale.Item_id	= Item_id
		OR	Item_id	=  -2147483647
			)
		AND	(
			vwItem_Locale.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Locale_cd	LIKE CONCAT('%', Locale_cd, '%')
		OR	Locale_cd	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Entry_tp	= Entry_tp
		OR	Entry_tp	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Entry_tx	LIKE CONCAT('%', Entry_tx, '%')
		OR	Entry_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Locale.EFF_dm	= EFF_dm
		OR	EFF_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.USE_dm	= USE_dm
		OR	USE_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.Item_nm	LIKE CONCAT('%', Item_nm, '%')
		OR	Item_nm	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Item_cd	LIKE CONCAT('%', Item_cd, '%')
		OR	Item_cd	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Language_cd	LIKE CONCAT('%', Language_cd, '%')
		OR	Language_cd	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Country_cd	LIKE CONCAT('%', Country_cd, '%')
		OR	Country_cd	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Status_nm	LIKE CONCAT('%', Status_nm, '%')
		OR	Status_nm	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Item_tx	LIKE CONCAT('%', Item_tx, '%')
		OR	Item_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Locale.ParentItem_tp	= ParentItem_tp
		OR	ParentItem_tp	= '-2147483647'
			)
		AND	(
			vwItem_Locale.ItemType_tx	LIKE CONCAT('%', ItemType_tx, '%')
		OR	ItemType_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Locale.ItemADD_dm	= ItemADD_dm
		OR	ItemADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.ItemADD_nm	LIKE CONCAT('%', ItemADD_nm, '%')
		OR	ItemADD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Locale.ItemUPD_dm	= ItemUPD_dm
		OR	ItemUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.ItemUPD_nm	LIKE CONCAT('%', ItemUPD_nm, '%')
		OR	ItemUPD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Locale.ItemDEL_dm	= ItemDEL_dm
		OR	ItemDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.ItemDEL_nm	LIKE CONCAT('%', ItemDEL_nm, '%')
		OR	ItemDEL_nm	= '-2147483647'
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

