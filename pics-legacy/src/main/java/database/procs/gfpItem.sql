DROP PROCEDURE IF EXISTS	`gfpItem`
;

DELIMITER //
CREATE PROCEDURE	gfpItem
(
	Item_id		int signed		-- PK1 
,	Item_tp		varchar(64)		-- PK2 AK1
,	Item_nm		varchar(256)		--  AK2
,	Item_cd		varchar(128)	
,	Item_tx		mediumtext	
,	ItemADD_dm		datetime	
,	ItemADD_nm		varchar(256)	
,	ItemUPD_dm		datetime	
,	ItemUPD_nm		varchar(256)	
,	ItemDEL_dm		datetime	
,	ItemDEL_nm		varchar(256)	
,	ParentItem_tp		varchar(64)	
,	ItemType_tx		mediumtext	
,	ItemTypeLeft_id		int signed	
,	ItemTypeRight_id		int signed	
,	ItemTypeLevel_id		int signed	
,	ItemTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpItem
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItem
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItem';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItem';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Item_id IS NULL OR Item_id = 0 THEN SET Item_id =  -2147483647;	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;
	IF Item_tx IS NULL OR Item_tx = '' THEN SET Item_tx = '-2147483647';	END IF;
	IF ItemADD_dm IS NULL OR ItemADD_dm = '' THEN SET ItemADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemADD_nm IS NULL OR ItemADD_nm = '' THEN SET ItemADD_nm = '-2147483647';	END IF;
	IF ItemUPD_dm IS NULL OR ItemUPD_dm = '' THEN SET ItemUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemUPD_nm IS NULL OR ItemUPD_nm = '' THEN SET ItemUPD_nm = '-2147483647';	END IF;
	IF ItemDEL_dm IS NULL OR ItemDEL_dm = '' THEN SET ItemDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemDEL_nm IS NULL OR ItemDEL_nm = '' THEN SET ItemDEL_nm = '-2147483647';	END IF;
	IF ParentItem_tp IS NULL OR ParentItem_tp = '' THEN SET ParentItem_tp = '-2147483647';	END IF;
	IF ItemType_tx IS NULL OR ItemType_tx = '' THEN SET ItemType_tx = '-2147483647';	END IF;
	IF ItemTypeLeft_id IS NULL OR ItemTypeLeft_id = 0 THEN SET ItemTypeLeft_id =  -2147483647;	END IF;
	IF ItemTypeRight_id IS NULL OR ItemTypeRight_id = 0 THEN SET ItemTypeRight_id =  -2147483647;	END IF;
	IF ItemTypeLevel_id IS NULL OR ItemTypeLevel_id = 0 THEN SET ItemTypeLevel_id =  -2147483647;	END IF;
	IF ItemTypeOrder_id IS NULL OR ItemTypeOrder_id = 0 THEN SET ItemTypeOrder_id =  -2147483647;	END IF;

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
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			vwItem.Item_id	= Item_id
		AND	vwItem.Item_tp	= Item_tp
		AND	vwItem.ItemDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			vwItem.Item_tp	= Item_tp
		AND	vwItem.Item_id	= Item_id
		AND	vwItem.Item_tp	= Item_tp
		AND	vwItem.ItemDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			vwItem.Item_tp	= Item_tp
		AND	vwItem.Item_tp	= Item_tp
		AND	vwItem.ItemDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			vwItem.Item_tp	= Item_tp
		AND	vwItem.Item_nm	= Item_nm
		AND	vwItem.ItemDEL_dm	IS NULL

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
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			(
			vwItem.Item_id	= Item_id
		OR	Item_id	=  -2147483647
			)
		AND	(
			vwItem.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItem.Item_nm	LIKE CONCAT('%', Item_nm, '%')
		OR	Item_nm	= '-2147483647'
			)
		AND	(
			vwItem.Item_cd	LIKE CONCAT('%', Item_cd, '%')
		OR	Item_cd	= '-2147483647'
			)
		AND	(
			vwItem.Item_tx	LIKE CONCAT('%', Item_tx, '%')
		OR	Item_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem.ItemADD_dm	= ItemADD_dm
		OR	ItemADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem.ItemADD_nm	LIKE CONCAT('%', ItemADD_nm, '%')
		OR	ItemADD_nm	= '-2147483647'
			)
		AND	(
			vwItem.ItemUPD_dm	= ItemUPD_dm
		OR	ItemUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem.ItemUPD_nm	LIKE CONCAT('%', ItemUPD_nm, '%')
		OR	ItemUPD_nm	= '-2147483647'
			)
		AND	(
			vwItem.ItemDEL_dm	= ItemDEL_dm
		OR	ItemDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem.ItemDEL_nm	LIKE CONCAT('%', ItemDEL_nm, '%')
		OR	ItemDEL_nm	= '-2147483647'
			)
		AND	(
			vwItem.ParentItem_tp	= ParentItem_tp
		OR	ParentItem_tp	= '-2147483647'
			)
		AND	(
			vwItem.ItemType_tx	LIKE CONCAT('%', ItemType_tx, '%')
		OR	ItemType_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem.ItemTypeLeft_id	= ItemTypeLeft_id
		OR	ItemTypeLeft_id	=  -2147483647
			)
		AND	(
			vwItem.ItemTypeRight_id	= ItemTypeRight_id
		OR	ItemTypeRight_id	=  -2147483647
			)
		AND	(
			vwItem.ItemTypeLevel_id	= ItemTypeLevel_id
		OR	ItemTypeLevel_id	=  -2147483647
			)
		AND	(
			vwItem.ItemTypeOrder_id	= ItemTypeOrder_id
		OR	ItemTypeOrder_id	=  -2147483647
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

