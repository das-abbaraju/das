DROP PROCEDURE IF EXISTS	`gfpItemType`
;

DELIMITER //
CREATE PROCEDURE	gfpItemType
(
	Item_tp		varchar(64)		-- PK1 
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
**	Name:		gfpItemType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItemType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItemType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItemType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
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
			vwItemType.Item_tp
		,	vwItemType.ParentItem_tp
		,	vwItemType.ItemType_tx
		,	vwItemType.ItemTypeLeft_id
		,	vwItemType.ItemTypeRight_id
		,	vwItemType.ItemTypeLevel_id
		,	vwItemType.ItemTypeOrder_id
		FROM
			vwItemType
		WHERE
			vwItemType.Item_tp	= Item_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItemType.Item_tp
		,	vwItemType.ParentItem_tp
		,	vwItemType.ItemType_tx
		,	vwItemType.ItemTypeLeft_id
		,	vwItemType.ItemTypeRight_id
		,	vwItemType.ItemTypeLevel_id
		,	vwItemType.ItemTypeOrder_id
		FROM
			vwItemType
		WHERE
			vwItemType.Item_tp	= Item_tp

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	-- NO ALTERNATE KEY DEFINED FOR THIS OBJECT
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
			vwItemType.Item_tp
		,	vwItemType.ParentItem_tp
		,	vwItemType.ItemType_tx
		,	vwItemType.ItemTypeLeft_id
		,	vwItemType.ItemTypeRight_id
		,	vwItemType.ItemTypeLevel_id
		,	vwItemType.ItemTypeOrder_id
		FROM
			vwItemType
		WHERE
			(
			vwItemType.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItemType.ParentItem_tp	= ParentItem_tp
		OR	ParentItem_tp	= '-2147483647'
			)
		AND	(
			vwItemType.ItemType_tx	LIKE CONCAT('%', ItemType_tx, '%')
		OR	ItemType_tx	LIKE '-2147483647'
			)
		AND	(
			vwItemType.ItemTypeLeft_id	= ItemTypeLeft_id
		OR	ItemTypeLeft_id	=  -2147483647
			)
		AND	(
			vwItemType.ItemTypeRight_id	= ItemTypeRight_id
		OR	ItemTypeRight_id	=  -2147483647
			)
		AND	(
			vwItemType.ItemTypeLevel_id	= ItemTypeLevel_id
		OR	ItemTypeLevel_id	=  -2147483647
			)
		AND	(
			vwItemType.ItemTypeOrder_id	= ItemTypeOrder_id
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

