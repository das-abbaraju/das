DROP PROCEDURE IF EXISTS	`gfpResource_RightType`
;

DELIMITER //
CREATE PROCEDURE	gfpResource_RightType
(
	Resrc_id		int signed		-- PK1 
,	Resrc_tp		varchar(80)		-- PK2 AK2
,	Right_tp		varchar(80)		-- PK3 AK3
,	Resrc_nm		varchar(128)		--  AK1
,	Resrc_tx		mediumtext	
,	ParentResrc_tp		varchar(80)	
,	ResrcType_tx		mediumtext	
,	ADD_dm		datetime	
,	ADD_nm		varchar(128)	
,	UPD_dm		datetime	
,	UPD_nm		varchar(128)	
,	DEL_dm		datetime	
,	DEL_nm		varchar(128)	
,	ParentRight_tp		varchar(80)	
,	RightType_tx		mediumtext	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpResource_RightType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwResource_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwResource_RightType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpResource_RightType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Resrc_id IS NULL OR Resrc_id = 0 THEN SET Resrc_id =  -2147483647;	END IF;
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF Right_tp IS NULL OR Right_tp = '' THEN SET Right_tp = '-2147483647';	END IF;
	IF Resrc_nm IS NULL OR Resrc_nm = '' THEN SET Resrc_nm = '-2147483647';	END IF;
	IF Resrc_tx IS NULL OR Resrc_tx = '' THEN SET Resrc_tx = '-2147483647';	END IF;
	IF ParentResrc_tp IS NULL OR ParentResrc_tp = '' THEN SET ParentResrc_tp = '-2147483647';	END IF;
	IF ResrcType_tx IS NULL OR ResrcType_tx = '' THEN SET ResrcType_tx = '-2147483647';	END IF;
	IF ADD_dm IS NULL OR ADD_dm = '' THEN SET ADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ADD_nm IS NULL OR ADD_nm = '' THEN SET ADD_nm = '-2147483647';	END IF;
	IF UPD_dm IS NULL OR UPD_dm = '' THEN SET UPD_dm = '0000-00-00 00:00:00';	END IF;
	IF UPD_nm IS NULL OR UPD_nm = '' THEN SET UPD_nm = '-2147483647';	END IF;
	IF DEL_dm IS NULL OR DEL_dm = '' THEN SET DEL_dm = '0000-00-00 00:00:00';	END IF;
	IF DEL_nm IS NULL OR DEL_nm = '' THEN SET DEL_nm = '-2147483647';	END IF;
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
			vwResource_RightType.Resrc_id
		,	vwResource_RightType.Resrc_tp
		,	vwResource_RightType.Right_tp
		,	vwResource_RightType.Resrc_nm
		,	vwResource_RightType.Resrc_tx
		,	vwResource_RightType.ParentResrc_tp
		,	vwResource_RightType.ResrcType_tx
		,	vwResource_RightType.ADD_dm
		,	vwResource_RightType.ADD_nm
		,	vwResource_RightType.UPD_dm
		,	vwResource_RightType.UPD_nm
		,	vwResource_RightType.DEL_dm
		,	vwResource_RightType.DEL_nm
		,	vwResource_RightType.ParentRight_tp
		,	vwResource_RightType.RightType_tx
		FROM
			vwResource_RightType
		WHERE
			vwResource_RightType.Resrc_id	= Resrc_id
		AND	vwResource_RightType.Resrc_tp	= Resrc_tp
		AND	vwResource_RightType.Right_tp	= Right_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwResource_RightType.Resrc_id
		,	vwResource_RightType.Resrc_tp
		,	vwResource_RightType.Right_tp
		,	vwResource_RightType.Resrc_nm
		,	vwResource_RightType.Resrc_tx
		,	vwResource_RightType.ParentResrc_tp
		,	vwResource_RightType.ResrcType_tx
		,	vwResource_RightType.ADD_dm
		,	vwResource_RightType.ADD_nm
		,	vwResource_RightType.UPD_dm
		,	vwResource_RightType.UPD_nm
		,	vwResource_RightType.DEL_dm
		,	vwResource_RightType.DEL_nm
		,	vwResource_RightType.ParentRight_tp
		,	vwResource_RightType.RightType_tx
		FROM
			vwResource_RightType
		WHERE
			vwResource_RightType.Resrc_id	= Resrc_id
		AND	vwResource_RightType.Resrc_tp	= Resrc_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwResource_RightType.Resrc_id
		,	vwResource_RightType.Resrc_tp
		,	vwResource_RightType.Right_tp
		,	vwResource_RightType.Resrc_nm
		,	vwResource_RightType.Resrc_tx
		,	vwResource_RightType.ParentResrc_tp
		,	vwResource_RightType.ResrcType_tx
		,	vwResource_RightType.ADD_dm
		,	vwResource_RightType.ADD_nm
		,	vwResource_RightType.UPD_dm
		,	vwResource_RightType.UPD_nm
		,	vwResource_RightType.DEL_dm
		,	vwResource_RightType.DEL_nm
		,	vwResource_RightType.ParentRight_tp
		,	vwResource_RightType.RightType_tx
		FROM
			vwResource_RightType
		WHERE
			vwResource_RightType.Right_tp	= Right_tp

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwResource_RightType.Resrc_id
		,	vwResource_RightType.Resrc_tp
		,	vwResource_RightType.Right_tp
		,	vwResource_RightType.Resrc_nm
		,	vwResource_RightType.Resrc_tx
		,	vwResource_RightType.ParentResrc_tp
		,	vwResource_RightType.ResrcType_tx
		,	vwResource_RightType.ADD_dm
		,	vwResource_RightType.ADD_nm
		,	vwResource_RightType.UPD_dm
		,	vwResource_RightType.UPD_nm
		,	vwResource_RightType.DEL_dm
		,	vwResource_RightType.DEL_nm
		,	vwResource_RightType.ParentRight_tp
		,	vwResource_RightType.RightType_tx
		FROM
			vwResource_RightType
		WHERE
			vwResource_RightType.Resrc_tp	= Resrc_tp
		AND	vwResource_RightType.Right_tp	= Right_tp
		AND	vwResource_RightType.Resrc_nm	= Resrc_nm

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
			vwResource_RightType.Resrc_id
		,	vwResource_RightType.Resrc_tp
		,	vwResource_RightType.Right_tp
		,	vwResource_RightType.Resrc_nm
		,	vwResource_RightType.Resrc_tx
		,	vwResource_RightType.ParentResrc_tp
		,	vwResource_RightType.ResrcType_tx
		,	vwResource_RightType.ADD_dm
		,	vwResource_RightType.ADD_nm
		,	vwResource_RightType.UPD_dm
		,	vwResource_RightType.UPD_nm
		,	vwResource_RightType.DEL_dm
		,	vwResource_RightType.DEL_nm
		,	vwResource_RightType.ParentRight_tp
		,	vwResource_RightType.RightType_tx
		FROM
			vwResource_RightType
		WHERE
			(
			vwResource_RightType.Resrc_id	= Resrc_id
		OR	Resrc_id	=  -2147483647
			)
		AND	(
			vwResource_RightType.Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
			)
		AND	(
			vwResource_RightType.Right_tp	= Right_tp
		OR	Right_tp	= '-2147483647'
			)
		AND	(
			vwResource_RightType.Resrc_nm	LIKE CONCAT('%', Resrc_nm, '%')
		OR	Resrc_nm	= '-2147483647'
			)
		AND	(
			vwResource_RightType.Resrc_tx	LIKE CONCAT('%', Resrc_tx, '%')
		OR	Resrc_tx	LIKE '-2147483647'
			)
		AND	(
			vwResource_RightType.ParentResrc_tp	= ParentResrc_tp
		OR	ParentResrc_tp	= '-2147483647'
			)
		AND	(
			vwResource_RightType.ResrcType_tx	LIKE CONCAT('%', ResrcType_tx, '%')
		OR	ResrcType_tx	LIKE '-2147483647'
			)
		AND	(
			vwResource_RightType.ADD_dm	= ADD_dm
		OR	ADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwResource_RightType.ADD_nm	LIKE CONCAT('%', ADD_nm, '%')
		OR	ADD_nm	= '-2147483647'
			)
		AND	(
			vwResource_RightType.UPD_dm	= UPD_dm
		OR	UPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwResource_RightType.UPD_nm	LIKE CONCAT('%', UPD_nm, '%')
		OR	UPD_nm	= '-2147483647'
			)
		AND	(
			vwResource_RightType.DEL_dm	= DEL_dm
		OR	DEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwResource_RightType.DEL_nm	LIKE CONCAT('%', DEL_nm, '%')
		OR	DEL_nm	= '-2147483647'
			)
		AND	(
			vwResource_RightType.ParentRight_tp	= ParentRight_tp
		OR	ParentRight_tp	= '-2147483647'
			)
		AND	(
			vwResource_RightType.RightType_tx	LIKE CONCAT('%', RightType_tx, '%')
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

