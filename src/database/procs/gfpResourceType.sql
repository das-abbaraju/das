DROP PROCEDURE IF EXISTS	`gfpResourceType`
;

DELIMITER //
CREATE PROCEDURE	gfpResourceType
(
	Resrc_tp		varchar(80)		-- PK1 AK1
,	ParentResrc_tp		varchar(80)	
,	ResrcType_tx		mediumtext	
,	Left_id		int signed	
,	Right_id		int signed	
,	Level_id		int signed	
,	Order_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpResourceType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwResourceType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwResourceType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpResourceType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF ParentResrc_tp IS NULL OR ParentResrc_tp = '' THEN SET ParentResrc_tp = '-2147483647';	END IF;
	IF ResrcType_tx IS NULL OR ResrcType_tx = '' THEN SET ResrcType_tx = '-2147483647';	END IF;
	IF Left_id IS NULL OR Left_id = 0 THEN SET Left_id =  -2147483647;	END IF;
	IF Right_id IS NULL OR Right_id = 0 THEN SET Right_id =  -2147483647;	END IF;
	IF Level_id IS NULL OR Level_id = 0 THEN SET Level_id =  -2147483647;	END IF;
	IF Order_id IS NULL OR Order_id = 0 THEN SET Order_id =  -2147483647;	END IF;

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
			vwResourceType.Resrc_tp
		,	vwResourceType.ParentResrc_tp
		,	vwResourceType.ResrcType_tx
		,	vwResourceType.Left_id
		,	vwResourceType.Right_id
		,	vwResourceType.Level_id
		,	vwResourceType.Order_id
		FROM
			vwResourceType
		WHERE
			vwResourceType.Resrc_tp	= Resrc_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	--   NO SUPER-SET OR PARENT TABLE FOR THIS OBJECT TO REFERENCE
	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwResourceType.Resrc_tp
		,	vwResourceType.ParentResrc_tp
		,	vwResourceType.ResrcType_tx
		,	vwResourceType.Left_id
		,	vwResourceType.Right_id
		,	vwResourceType.Level_id
		,	vwResourceType.Order_id
		FROM
			vwResourceType
		WHERE
			vwResourceType.Resrc_tp	= Resrc_tp

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
			vwResourceType.Resrc_tp
		,	vwResourceType.ParentResrc_tp
		,	vwResourceType.ResrcType_tx
		,	vwResourceType.Left_id
		,	vwResourceType.Right_id
		,	vwResourceType.Level_id
		,	vwResourceType.Order_id
		FROM
			vwResourceType
		WHERE
			(
			vwResourceType.Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
			)
		AND	(
			vwResourceType.ParentResrc_tp	= ParentResrc_tp
		OR	ParentResrc_tp	= '-2147483647'
			)
		AND	(
			vwResourceType.ResrcType_tx	LIKE CONCAT('%', ResrcType_tx, '%')
		OR	ResrcType_tx	LIKE '-2147483647'
			)
		AND	(
			vwResourceType.Left_id	= Left_id
		OR	Left_id	=  -2147483647
			)
		AND	(
			vwResourceType.Right_id	= Right_id
		OR	Right_id	=  -2147483647
			)
		AND	(
			vwResourceType.Level_id	= Level_id
		OR	Level_id	=  -2147483647
			)
		AND	(
			vwResourceType.Order_id	= Order_id
		OR	Order_id	=  -2147483647
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

