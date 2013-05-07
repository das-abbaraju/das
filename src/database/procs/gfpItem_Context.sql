DROP PROCEDURE IF EXISTS	`gfpItem_Context`
;

DELIMITER //
CREATE PROCEDURE	gfpItem_Context
(
	Item_id		int signed		-- PK1 
,	Item_tp		varchar(64)		-- PK2 AK1
,	Context_id		int signed		-- PK3 
,	Context_tp		varchar(64)		-- PK4 AK3
,	Order_id		int signed	
,	Item_nm		varchar(256)		--  AK2
,	Item_cd		varchar(128)	
,	Context_nm		varchar(256)		--  AK4
,	Context_cd		varchar(128)	
,	Item_tx		mediumtext	
,	ItemADD_dm		datetime	
,	ItemADD_nm		varchar(256)	
,	ItemUPD_dm		datetime	
,	ItemUPD_nm		varchar(256)	
,	ItemDEL_dm		datetime	
,	ItemDEL_nm		varchar(256)	
,	Context_tx		mediumtext	
,	ContextADD_dm		datetime	
,	ContextADD_nm		varchar(256)	
,	ContextUPD_dm		datetime	
,	ContextUPD_nm		varchar(256)	
,	ContextDEL_dm		datetime	
,	ContextDEL_nm		varchar(256)	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpItem_Context
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItem_Context
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItem_Context';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItem_Context';
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
	IF Order_id IS NULL OR Order_id = 0 THEN SET Order_id =  -2147483647;	END IF;
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;
	IF Context_nm IS NULL OR Context_nm = '' THEN SET Context_nm = '-2147483647';	END IF;
	IF Context_cd IS NULL OR Context_cd = '' THEN SET Context_cd = '-2147483647';	END IF;
	IF Item_tx IS NULL OR Item_tx = '' THEN SET Item_tx = '-2147483647';	END IF;
	IF ItemADD_dm IS NULL OR ItemADD_dm = '' THEN SET ItemADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemADD_nm IS NULL OR ItemADD_nm = '' THEN SET ItemADD_nm = '-2147483647';	END IF;
	IF ItemUPD_dm IS NULL OR ItemUPD_dm = '' THEN SET ItemUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemUPD_nm IS NULL OR ItemUPD_nm = '' THEN SET ItemUPD_nm = '-2147483647';	END IF;
	IF ItemDEL_dm IS NULL OR ItemDEL_dm = '' THEN SET ItemDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemDEL_nm IS NULL OR ItemDEL_nm = '' THEN SET ItemDEL_nm = '-2147483647';	END IF;
	IF Context_tx IS NULL OR Context_tx = '' THEN SET Context_tx = '-2147483647';	END IF;
	IF ContextADD_dm IS NULL OR ContextADD_dm = '' THEN SET ContextADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextADD_nm IS NULL OR ContextADD_nm = '' THEN SET ContextADD_nm = '-2147483647';	END IF;
	IF ContextUPD_dm IS NULL OR ContextUPD_dm = '' THEN SET ContextUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextUPD_nm IS NULL OR ContextUPD_nm = '' THEN SET ContextUPD_nm = '-2147483647';	END IF;
	IF ContextDEL_dm IS NULL OR ContextDEL_dm = '' THEN SET ContextDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextDEL_nm IS NULL OR ContextDEL_nm = '' THEN SET ContextDEL_nm = '-2147483647';	END IF;

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
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			vwItem_Context.Item_id	= Item_id
		AND	vwItem_Context.Item_tp	= Item_tp
		AND	vwItem_Context.Context_id	= Context_id
		AND	vwItem_Context.Context_tp	= Context_tp
		AND	vwItem_Context.ItemDEL_dm	IS NULL
		AND	vwItem_Context.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			vwItem_Context.Item_id	= Item_id
		AND	vwItem_Context.Item_tp	= Item_tp
		AND	vwItem_Context.ItemDEL_dm	IS NULL
		AND	vwItem_Context.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			vwItem_Context.Context_id	= Context_id
		AND	vwItem_Context.Context_tp	= Context_tp
		AND	vwItem_Context.ItemDEL_dm	IS NULL
		AND	vwItem_Context.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			vwItem_Context.Item_tp	= Item_tp
		AND	vwItem_Context.Context_tp	= Context_tp
		AND	vwItem_Context.Item_nm	= Item_nm
		AND	vwItem_Context.Context_nm	= Context_nm
		AND	vwItem_Context.ItemDEL_dm	IS NULL
		AND	vwItem_Context.ContextDEL_dm	IS NULL

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
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			(
			vwItem_Context.Item_id	= Item_id
		OR	Item_id	=  -2147483647
			)
		AND	(
			vwItem_Context.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context.Context_id	= Context_id
		OR	Context_id	=  -2147483647
			)
		AND	(
			vwItem_Context.Context_tp	= Context_tp
		OR	Context_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context.Order_id	= Order_id
		OR	Order_id	=  -2147483647
			)
		AND	(
			vwItem_Context.Item_nm	LIKE CONCAT('%', Item_nm, '%')
		OR	Item_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.Item_cd	LIKE CONCAT('%', Item_cd, '%')
		OR	Item_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context.Context_nm	LIKE CONCAT('%', Context_nm, '%')
		OR	Context_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.Context_cd	LIKE CONCAT('%', Context_cd, '%')
		OR	Context_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context.Item_tx	LIKE CONCAT('%', Item_tx, '%')
		OR	Item_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Context.ItemADD_dm	= ItemADD_dm
		OR	ItemADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ItemADD_nm	LIKE CONCAT('%', ItemADD_nm, '%')
		OR	ItemADD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.ItemUPD_dm	= ItemUPD_dm
		OR	ItemUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ItemUPD_nm	LIKE CONCAT('%', ItemUPD_nm, '%')
		OR	ItemUPD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.ItemDEL_dm	= ItemDEL_dm
		OR	ItemDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ItemDEL_nm	LIKE CONCAT('%', ItemDEL_nm, '%')
		OR	ItemDEL_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.Context_tx	LIKE CONCAT('%', Context_tx, '%')
		OR	Context_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Context.ContextADD_dm	= ContextADD_dm
		OR	ContextADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ContextADD_nm	LIKE CONCAT('%', ContextADD_nm, '%')
		OR	ContextADD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.ContextUPD_dm	= ContextUPD_dm
		OR	ContextUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ContextUPD_nm	LIKE CONCAT('%', ContextUPD_nm, '%')
		OR	ContextUPD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.ContextDEL_dm	= ContextDEL_dm
		OR	ContextDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ContextDEL_nm	LIKE CONCAT('%', ContextDEL_nm, '%')
		OR	ContextDEL_nm	= '-2147483647'
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

