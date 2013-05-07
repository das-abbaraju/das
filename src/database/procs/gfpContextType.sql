DROP PROCEDURE IF EXISTS	`gfpContextType`
;

DELIMITER //
CREATE PROCEDURE	gfpContextType
(
	Context_tp		varchar(64)		-- PK1 
,	ParentContext_tp		varchar(64)	
,	ContextType_tx		mediumtext	
,	ContextTypeLeft_id		int signed	
,	ContextTypeRight_id		int signed	
,	ContextTypeLevel_id		int signed	
,	ContextTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpContextType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwContextType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwContextType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpContextType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF ParentContext_tp IS NULL OR ParentContext_tp = '' THEN SET ParentContext_tp = '-2147483647';	END IF;
	IF ContextType_tx IS NULL OR ContextType_tx = '' THEN SET ContextType_tx = '-2147483647';	END IF;
	IF ContextTypeLeft_id IS NULL OR ContextTypeLeft_id = 0 THEN SET ContextTypeLeft_id =  -2147483647;	END IF;
	IF ContextTypeRight_id IS NULL OR ContextTypeRight_id = 0 THEN SET ContextTypeRight_id =  -2147483647;	END IF;
	IF ContextTypeLevel_id IS NULL OR ContextTypeLevel_id = 0 THEN SET ContextTypeLevel_id =  -2147483647;	END IF;
	IF ContextTypeOrder_id IS NULL OR ContextTypeOrder_id = 0 THEN SET ContextTypeOrder_id =  -2147483647;	END IF;

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
			vwContextType.Context_tp
		,	vwContextType.ParentContext_tp
		,	vwContextType.ContextType_tx
		,	vwContextType.ContextTypeLeft_id
		,	vwContextType.ContextTypeRight_id
		,	vwContextType.ContextTypeLevel_id
		,	vwContextType.ContextTypeOrder_id
		FROM
			vwContextType
		WHERE
			vwContextType.Context_tp	= Context_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwContextType.Context_tp
		,	vwContextType.ParentContext_tp
		,	vwContextType.ContextType_tx
		,	vwContextType.ContextTypeLeft_id
		,	vwContextType.ContextTypeRight_id
		,	vwContextType.ContextTypeLevel_id
		,	vwContextType.ContextTypeOrder_id
		FROM
			vwContextType
		WHERE
			vwContextType.Context_tp	= Context_tp

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
			vwContextType.Context_tp
		,	vwContextType.ParentContext_tp
		,	vwContextType.ContextType_tx
		,	vwContextType.ContextTypeLeft_id
		,	vwContextType.ContextTypeRight_id
		,	vwContextType.ContextTypeLevel_id
		,	vwContextType.ContextTypeOrder_id
		FROM
			vwContextType
		WHERE
			(
			vwContextType.Context_tp	= Context_tp
		OR	Context_tp	= '-2147483647'
			)
		AND	(
			vwContextType.ParentContext_tp	= ParentContext_tp
		OR	ParentContext_tp	= '-2147483647'
			)
		AND	(
			vwContextType.ContextType_tx	LIKE CONCAT('%', ContextType_tx, '%')
		OR	ContextType_tx	LIKE '-2147483647'
			)
		AND	(
			vwContextType.ContextTypeLeft_id	= ContextTypeLeft_id
		OR	ContextTypeLeft_id	=  -2147483647
			)
		AND	(
			vwContextType.ContextTypeRight_id	= ContextTypeRight_id
		OR	ContextTypeRight_id	=  -2147483647
			)
		AND	(
			vwContextType.ContextTypeLevel_id	= ContextTypeLevel_id
		OR	ContextTypeLevel_id	=  -2147483647
			)
		AND	(
			vwContextType.ContextTypeOrder_id	= ContextTypeOrder_id
		OR	ContextTypeOrder_id	=  -2147483647
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

