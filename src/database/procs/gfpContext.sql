DROP PROCEDURE IF EXISTS	`gfpContext`
;

DELIMITER //
CREATE PROCEDURE	gfpContext
(
	Context_id		int signed		-- PK1 
,	Context_tp		varchar(64)		-- PK2 AK1
,	Context_nm		varchar(256)		--  AK2
,	Context_cd		varchar(128)	
,	Context_tx		mediumtext	
,	ContextADD_dm		datetime	
,	ContextADD_nm		varchar(256)	
,	ContextUPD_dm		datetime	
,	ContextUPD_nm		varchar(256)	
,	ContextDEL_dm		datetime	
,	ContextDEL_nm		varchar(256)	
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
**	Name:		gfpContext
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwContext
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwContext';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpContext';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Context_id IS NULL OR Context_id = 0 THEN SET Context_id =  -2147483647;	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF Context_nm IS NULL OR Context_nm = '' THEN SET Context_nm = '-2147483647';	END IF;
	IF Context_cd IS NULL OR Context_cd = '' THEN SET Context_cd = '-2147483647';	END IF;
	IF Context_tx IS NULL OR Context_tx = '' THEN SET Context_tx = '-2147483647';	END IF;
	IF ContextADD_dm IS NULL OR ContextADD_dm = '' THEN SET ContextADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextADD_nm IS NULL OR ContextADD_nm = '' THEN SET ContextADD_nm = '-2147483647';	END IF;
	IF ContextUPD_dm IS NULL OR ContextUPD_dm = '' THEN SET ContextUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextUPD_nm IS NULL OR ContextUPD_nm = '' THEN SET ContextUPD_nm = '-2147483647';	END IF;
	IF ContextDEL_dm IS NULL OR ContextDEL_dm = '' THEN SET ContextDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextDEL_nm IS NULL OR ContextDEL_nm = '' THEN SET ContextDEL_nm = '-2147483647';	END IF;
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
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			vwContext.Context_id	= Context_id
		AND	vwContext.Context_tp	= Context_tp
		AND	vwContext.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			vwContext.Context_tp	= Context_tp
		AND	vwContext.Context_id	= Context_id
		AND	vwContext.Context_tp	= Context_tp
		AND	vwContext.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			vwContext.Context_tp	= Context_tp
		AND	vwContext.Context_tp	= Context_tp
		AND	vwContext.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			vwContext.Context_tp	= Context_tp
		AND	vwContext.Context_nm	= Context_nm
		AND	vwContext.ContextDEL_dm	IS NULL

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
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			(
			vwContext.Context_id	= Context_id
		OR	Context_id	=  -2147483647
			)
		AND	(
			vwContext.Context_tp	= Context_tp
		OR	Context_tp	= '-2147483647'
			)
		AND	(
			vwContext.Context_nm	LIKE CONCAT('%', Context_nm, '%')
		OR	Context_nm	= '-2147483647'
			)
		AND	(
			vwContext.Context_cd	LIKE CONCAT('%', Context_cd, '%')
		OR	Context_cd	= '-2147483647'
			)
		AND	(
			vwContext.Context_tx	LIKE CONCAT('%', Context_tx, '%')
		OR	Context_tx	LIKE '-2147483647'
			)
		AND	(
			vwContext.ContextADD_dm	= ContextADD_dm
		OR	ContextADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwContext.ContextADD_nm	LIKE CONCAT('%', ContextADD_nm, '%')
		OR	ContextADD_nm	= '-2147483647'
			)
		AND	(
			vwContext.ContextUPD_dm	= ContextUPD_dm
		OR	ContextUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwContext.ContextUPD_nm	LIKE CONCAT('%', ContextUPD_nm, '%')
		OR	ContextUPD_nm	= '-2147483647'
			)
		AND	(
			vwContext.ContextDEL_dm	= ContextDEL_dm
		OR	ContextDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwContext.ContextDEL_nm	LIKE CONCAT('%', ContextDEL_nm, '%')
		OR	ContextDEL_nm	= '-2147483647'
			)
		AND	(
			vwContext.ParentContext_tp	= ParentContext_tp
		OR	ParentContext_tp	= '-2147483647'
			)
		AND	(
			vwContext.ContextType_tx	LIKE CONCAT('%', ContextType_tx, '%')
		OR	ContextType_tx	LIKE '-2147483647'
			)
		AND	(
			vwContext.ContextTypeLeft_id	= ContextTypeLeft_id
		OR	ContextTypeLeft_id	=  -2147483647
			)
		AND	(
			vwContext.ContextTypeRight_id	= ContextTypeRight_id
		OR	ContextTypeRight_id	=  -2147483647
			)
		AND	(
			vwContext.ContextTypeLevel_id	= ContextTypeLevel_id
		OR	ContextTypeLevel_id	=  -2147483647
			)
		AND	(
			vwContext.ContextTypeOrder_id	= ContextTypeOrder_id
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

