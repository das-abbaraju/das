DROP PROCEDURE IF EXISTS	`gfpRightType`
;

DELIMITER //
CREATE PROCEDURE	gfpRightType
(
	Right_tp		varchar(80)		-- PK1 
,	ParentRight_tp		varchar(80)	
,	RightType_tx		mediumtext	
,	RightTypeLeft_id		int signed	
,	RightTypeRight_id		int signed	
,	RightTypeLevel_id		int signed	
,	RightTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpRightType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwRightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwRightType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpRightType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Right_tp IS NULL OR Right_tp = '' THEN SET Right_tp = '-2147483647';	END IF;
	IF ParentRight_tp IS NULL OR ParentRight_tp = '' THEN SET ParentRight_tp = '-2147483647';	END IF;
	IF RightType_tx IS NULL OR RightType_tx = '' THEN SET RightType_tx = '-2147483647';	END IF;
	IF RightTypeLeft_id IS NULL OR RightTypeLeft_id = 0 THEN SET RightTypeLeft_id =  -2147483647;	END IF;
	IF RightTypeRight_id IS NULL OR RightTypeRight_id = 0 THEN SET RightTypeRight_id =  -2147483647;	END IF;
	IF RightTypeLevel_id IS NULL OR RightTypeLevel_id = 0 THEN SET RightTypeLevel_id =  -2147483647;	END IF;
	IF RightTypeOrder_id IS NULL OR RightTypeOrder_id = 0 THEN SET RightTypeOrder_id =  -2147483647;	END IF;

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
			vwRightType.Right_tp
		,	vwRightType.ParentRight_tp
		,	vwRightType.RightType_tx
		,	vwRightType.RightTypeLeft_id
		,	vwRightType.RightTypeRight_id
		,	vwRightType.RightTypeLevel_id
		,	vwRightType.RightTypeOrder_id
		FROM
			vwRightType
		WHERE
			vwRightType.Right_tp	= Right_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwRightType.Right_tp
		,	vwRightType.ParentRight_tp
		,	vwRightType.RightType_tx
		,	vwRightType.RightTypeLeft_id
		,	vwRightType.RightTypeRight_id
		,	vwRightType.RightTypeLevel_id
		,	vwRightType.RightTypeOrder_id
		FROM
			vwRightType
		WHERE
			vwRightType.Right_tp	= Right_tp

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
			vwRightType.Right_tp
		,	vwRightType.ParentRight_tp
		,	vwRightType.RightType_tx
		,	vwRightType.RightTypeLeft_id
		,	vwRightType.RightTypeRight_id
		,	vwRightType.RightTypeLevel_id
		,	vwRightType.RightTypeOrder_id
		FROM
			vwRightType
		WHERE
			(
			Right_tp	= Right_tp
		OR	Right_tp	= '-2147483647'
			)
		AND	(
			ParentRight_tp	= ParentRight_tp
		OR	ParentRight_tp	= '-2147483647'
			)
		AND	(
			RightType_tx	LIKE CONCAT('%', RightType_tx, '%')
		OR	RightType_tx	LIKE '-2147483647'
			)
		AND	(
			RightTypeLeft_id	= RightTypeLeft_id
		OR	RightTypeLeft_id	=  -2147483647
			)
		AND	(
			RightTypeRight_id	= RightTypeRight_id
		OR	RightTypeRight_id	=  -2147483647
			)
		AND	(
			RightTypeLevel_id	= RightTypeLevel_id
		OR	RightTypeLevel_id	=  -2147483647
			)
		AND	(
			RightTypeOrder_id	= RightTypeOrder_id
		OR	RightTypeOrder_id	=  -2147483647
			)

		;
		LEAVE GFP;
	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

