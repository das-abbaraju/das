DROP PROCEDURE IF EXISTS	`gfpRole_Resource_RightType`
;

DELIMITER //
CREATE PROCEDURE	gfpRole_Resource_RightType
(
	Role_id		int signed		-- PK1 
,	Role_tp		varchar(80)		-- PK2 AK2
,	Role_nm		varchar(128)		--  AK1
,	Role_cd		varchar(48)	
,	Resrc_id		int signed		-- PK3 AK3
,	Resrc_tp		varchar(80)		-- PK4 AK4
,	Resrc_nm		varchar(128)		--  AK6
,	Right_tp		varchar(80)		-- PK5 AK5
,	Role_tx		mediumtext	
,	Resrc_tx		mediumtext	
,	ParentResrc_tp		varchar(80)	
,	ResrcType_tx		mediumtext	
,	ParentRight_tp		varchar(80)	
,	RightType_tx		mediumtext	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpRole_Resource_RightType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwRole_Resource_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwRole_Resource_RightType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpRole_Resource_RightType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Role_id IS NULL OR Role_id = 0 THEN SET Role_id =  -2147483647;	END IF;
	IF Role_tp IS NULL OR Role_tp = '' THEN SET Role_tp = '-2147483647';	END IF;
	IF Role_nm IS NULL OR Role_nm = '' THEN SET Role_nm = '-2147483647';	END IF;
	IF Role_cd IS NULL OR Role_cd = '' THEN SET Role_cd = '-2147483647';	END IF;
	IF Resrc_id IS NULL OR Resrc_id = 0 THEN SET Resrc_id =  -2147483647;	END IF;
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF Resrc_nm IS NULL OR Resrc_nm = '' THEN SET Resrc_nm = '-2147483647';	END IF;
	IF Right_tp IS NULL OR Right_tp = '' THEN SET Right_tp = '-2147483647';	END IF;
	IF Role_tx IS NULL OR Role_tx = '' THEN SET Role_tx = '-2147483647';	END IF;
	IF Resrc_tx IS NULL OR Resrc_tx = '' THEN SET Resrc_tx = '-2147483647';	END IF;
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
			vwRole_Resource_RightType.Role_id
		,	vwRole_Resource_RightType.Role_tp
		,	vwRole_Resource_RightType.Role_nm
		,	vwRole_Resource_RightType.Role_cd
		,	vwRole_Resource_RightType.Resrc_id
		,	vwRole_Resource_RightType.Resrc_tp
		,	vwRole_Resource_RightType.Resrc_nm
		,	vwRole_Resource_RightType.Right_tp
		,	vwRole_Resource_RightType.Role_tx
		,	vwRole_Resource_RightType.Resrc_tx
		,	vwRole_Resource_RightType.ParentResrc_tp
		,	vwRole_Resource_RightType.ResrcType_tx
		,	vwRole_Resource_RightType.ParentRight_tp
		,	vwRole_Resource_RightType.RightType_tx
		FROM
			vwRole_Resource_RightType
		WHERE
			vwRole_Resource_RightType.Role_id	= Role_id
		AND	vwRole_Resource_RightType.Role_tp	= Role_tp
		AND	vwRole_Resource_RightType.Resrc_id	= Resrc_id
		AND	vwRole_Resource_RightType.Resrc_tp	= Resrc_tp
		AND	vwRole_Resource_RightType.Right_tp	= Right_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwRole_Resource_RightType.Role_id
		,	vwRole_Resource_RightType.Role_tp
		,	vwRole_Resource_RightType.Role_nm
		,	vwRole_Resource_RightType.Role_cd
		,	vwRole_Resource_RightType.Resrc_id
		,	vwRole_Resource_RightType.Resrc_tp
		,	vwRole_Resource_RightType.Resrc_nm
		,	vwRole_Resource_RightType.Right_tp
		,	vwRole_Resource_RightType.Role_tx
		,	vwRole_Resource_RightType.Resrc_tx
		,	vwRole_Resource_RightType.ParentResrc_tp
		,	vwRole_Resource_RightType.ResrcType_tx
		,	vwRole_Resource_RightType.ParentRight_tp
		,	vwRole_Resource_RightType.RightType_tx
		FROM
			vwRole_Resource_RightType
		WHERE
			vwRole_Resource_RightType.Role_id	= Role_id
		AND	vwRole_Resource_RightType.Role_tp	= Role_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwRole_Resource_RightType.Role_id
		,	vwRole_Resource_RightType.Role_tp
		,	vwRole_Resource_RightType.Role_nm
		,	vwRole_Resource_RightType.Role_cd
		,	vwRole_Resource_RightType.Resrc_id
		,	vwRole_Resource_RightType.Resrc_tp
		,	vwRole_Resource_RightType.Resrc_nm
		,	vwRole_Resource_RightType.Right_tp
		,	vwRole_Resource_RightType.Role_tx
		,	vwRole_Resource_RightType.Resrc_tx
		,	vwRole_Resource_RightType.ParentResrc_tp
		,	vwRole_Resource_RightType.ResrcType_tx
		,	vwRole_Resource_RightType.ParentRight_tp
		,	vwRole_Resource_RightType.RightType_tx
		FROM
			vwRole_Resource_RightType
		WHERE
			vwRole_Resource_RightType.Resrc_id	= Resrc_id
		AND	vwRole_Resource_RightType.Resrc_tp	= Resrc_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK3'
	THEN
		SELECT
			vwRole_Resource_RightType.Role_id
		,	vwRole_Resource_RightType.Role_tp
		,	vwRole_Resource_RightType.Role_nm
		,	vwRole_Resource_RightType.Role_cd
		,	vwRole_Resource_RightType.Resrc_id
		,	vwRole_Resource_RightType.Resrc_tp
		,	vwRole_Resource_RightType.Resrc_nm
		,	vwRole_Resource_RightType.Right_tp
		,	vwRole_Resource_RightType.Role_tx
		,	vwRole_Resource_RightType.Resrc_tx
		,	vwRole_Resource_RightType.ParentResrc_tp
		,	vwRole_Resource_RightType.ResrcType_tx
		,	vwRole_Resource_RightType.ParentRight_tp
		,	vwRole_Resource_RightType.RightType_tx
		FROM
			vwRole_Resource_RightType
		WHERE
			vwRole_Resource_RightType.Resrc_id	= Resrc_id
		AND	vwRole_Resource_RightType.Resrc_tp	= Resrc_tp
		AND	vwRole_Resource_RightType.Right_tp	= Right_tp

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwRole_Resource_RightType.Role_id
		,	vwRole_Resource_RightType.Role_tp
		,	vwRole_Resource_RightType.Role_nm
		,	vwRole_Resource_RightType.Role_cd
		,	vwRole_Resource_RightType.Resrc_id
		,	vwRole_Resource_RightType.Resrc_tp
		,	vwRole_Resource_RightType.Resrc_nm
		,	vwRole_Resource_RightType.Right_tp
		,	vwRole_Resource_RightType.Role_tx
		,	vwRole_Resource_RightType.Resrc_tx
		,	vwRole_Resource_RightType.ParentResrc_tp
		,	vwRole_Resource_RightType.ResrcType_tx
		,	vwRole_Resource_RightType.ParentRight_tp
		,	vwRole_Resource_RightType.RightType_tx
		FROM
			vwRole_Resource_RightType
		WHERE
			vwRole_Resource_RightType.Role_tp	= Role_tp
		AND	vwRole_Resource_RightType.Role_nm	= Role_nm
		AND	vwRole_Resource_RightType.Resrc_id	= Resrc_id
		AND	vwRole_Resource_RightType.Resrc_tp	= Resrc_tp
		AND	vwRole_Resource_RightType.Resrc_nm	= Resrc_nm
		AND	vwRole_Resource_RightType.Right_tp	= Right_tp

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
			vwRole_Resource_RightType.Role_id
		,	vwRole_Resource_RightType.Role_tp
		,	vwRole_Resource_RightType.Role_nm
		,	vwRole_Resource_RightType.Role_cd
		,	vwRole_Resource_RightType.Resrc_id
		,	vwRole_Resource_RightType.Resrc_tp
		,	vwRole_Resource_RightType.Resrc_nm
		,	vwRole_Resource_RightType.Right_tp
		,	vwRole_Resource_RightType.Role_tx
		,	vwRole_Resource_RightType.Resrc_tx
		,	vwRole_Resource_RightType.ParentResrc_tp
		,	vwRole_Resource_RightType.ResrcType_tx
		,	vwRole_Resource_RightType.ParentRight_tp
		,	vwRole_Resource_RightType.RightType_tx
		FROM
			vwRole_Resource_RightType
		WHERE
			(
			Role_id	= Role_id
		OR	Role_id	=  -2147483647
			)
		AND	(
			Role_tp	= Role_tp
		OR	Role_tp	= '-2147483647'
			)
		AND	(
			Role_nm	LIKE CONCAT('%', Role_nm, '%')
		OR	Role_nm	= '-2147483647'
			)
		AND	(
			Role_cd	LIKE CONCAT('%', Role_cd, '%')
		OR	Role_cd	= '-2147483647'
			)
		AND	(
			Resrc_id	= Resrc_id
		OR	Resrc_id	=  -2147483647
			)
		AND	(
			Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
			)
		AND	(
			Resrc_nm	LIKE CONCAT('%', Resrc_nm, '%')
		OR	Resrc_nm	= '-2147483647'
			)
		AND	(
			Right_tp	= Right_tp
		OR	Right_tp	= '-2147483647'
			)
		AND	(
			Role_tx	LIKE CONCAT('%', Role_tx, '%')
		OR	Role_tx	LIKE '-2147483647'
			)
		AND	(
			Resrc_tx	LIKE CONCAT('%', Resrc_tx, '%')
		OR	Resrc_tx	LIKE '-2147483647'
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

