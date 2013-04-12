DROP PROCEDURE IF EXISTS	`gfpResourceType_RightType`
;

DELIMITER //
CREATE PROCEDURE	gfpResourceType_RightType
(
	Resrc_tp		varchar(80)		-- PK1 
,	Right_tp		varchar(80)		-- PK2 
,	ParentResrc_tp		varchar(80)	
,	ResrcType_tx		mediumtext	
,	ParentRight_tp		varchar(80)	
,	RightType_tx		mediumtext	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpResourceType_RightType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwResourceType_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwResourceType_RightType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpResourceType_RightType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF Right_tp IS NULL OR Right_tp = '' THEN SET Right_tp = '-2147483647';	END IF;
	IF ParentResrc_tp IS NULL OR ParentResrc_tp = '' THEN SET ParentResrc_tp = '-2147483647';	END IF;
	IF ResrcType_tx IS NULL OR ResrcType_tx = '' THEN SET ResrcType_tx = '-2147483647';	END IF;
	IF ParentRight_tp IS NULL OR ParentRight_tp = '' THEN SET ParentRight_tp = '-2147483647';	END IF;
	IF RightType_tx IS NULL OR RightType_tx = '' THEN SET RightType_tx = '-2147483647';	END IF;

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
			vwResourceType_RightType.Resrc_tp
		,	vwResourceType_RightType.Right_tp
		,	vwResourceType_RightType.ParentResrc_tp
		,	vwResourceType_RightType.ResrcType_tx
		,	vwResourceType_RightType.ParentRight_tp
		,	vwResourceType_RightType.RightType_tx
		FROM
			vwResourceType_RightType
		WHERE
			vwResourceType_RightType.Resrc_tp	= Resrc_tp
		AND	vwResourceType_RightType.Right_tp	= Right_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwResourceType_RightType.Resrc_tp
		,	vwResourceType_RightType.Right_tp
		,	vwResourceType_RightType.ParentResrc_tp
		,	vwResourceType_RightType.ResrcType_tx
		,	vwResourceType_RightType.ParentRight_tp
		,	vwResourceType_RightType.RightType_tx
		FROM
			vwResourceType_RightType
		WHERE
			vwResourceType_RightType.Resrc_tp	= Resrc_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwResourceType_RightType.Resrc_tp
		,	vwResourceType_RightType.Right_tp
		,	vwResourceType_RightType.ParentResrc_tp
		,	vwResourceType_RightType.ResrcType_tx
		,	vwResourceType_RightType.ParentRight_tp
		,	vwResourceType_RightType.RightType_tx
		FROM
			vwResourceType_RightType
		WHERE
			vwResourceType_RightType.Right_tp	= Right_tp

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
			vwResourceType_RightType.Resrc_tp
		,	vwResourceType_RightType.Right_tp
		,	vwResourceType_RightType.ParentResrc_tp
		,	vwResourceType_RightType.ResrcType_tx
		,	vwResourceType_RightType.ParentRight_tp
		,	vwResourceType_RightType.RightType_tx
		FROM
			vwResourceType_RightType
		WHERE
			(
			Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
			)
		AND	(
			Right_tp	= Right_tp
		OR	Right_tp	= '-2147483647'
			)
		AND	(
			ParentResrc_tp	= ParentResrc_tp
		OR	ParentResrc_tp	= '-2147483647'
			)
		AND	(
			ResrcType_tx	LIKE CONCAT('%', ResrcType_tx, '%')
		OR	ResrcType_tx	LIKE '-2147483647'
			)
		AND	(
			ParentRight_tp	= ParentRight_tp
		OR	ParentRight_tp	= '-2147483647'
			)
		AND	(
			RightType_tx	LIKE CONCAT('%', RightType_tx, '%')
		OR	RightType_tx	LIKE '-2147483647'
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

