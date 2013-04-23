DROP PROCEDURE IF EXISTS	`gfpRoleType`
;

DELIMITER //
CREATE PROCEDURE	gfpRoleType
(
	Role_tp		varchar(80)		-- PK1 
,	ParentRole_tp		varchar(80)	
,	RoleType_tx		mediumtext	
,	RoleTypeLeft_id		int signed	
,	RoleTypeRight_id		int signed	
,	RoleTypeLevel_id		int signed	
,	RoleTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpRoleType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwRoleType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwRoleType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpRoleType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Role_tp IS NULL OR Role_tp = '' THEN SET Role_tp = '-2147483647';	END IF;
	IF ParentRole_tp IS NULL OR ParentRole_tp = '' THEN SET ParentRole_tp = '-2147483647';	END IF;
	IF RoleType_tx IS NULL OR RoleType_tx = '' THEN SET RoleType_tx = '-2147483647';	END IF;
	IF RoleTypeLeft_id IS NULL OR RoleTypeLeft_id = 0 THEN SET RoleTypeLeft_id =  -2147483647;	END IF;
	IF RoleTypeRight_id IS NULL OR RoleTypeRight_id = 0 THEN SET RoleTypeRight_id =  -2147483647;	END IF;
	IF RoleTypeLevel_id IS NULL OR RoleTypeLevel_id = 0 THEN SET RoleTypeLevel_id =  -2147483647;	END IF;
	IF RoleTypeOrder_id IS NULL OR RoleTypeOrder_id = 0 THEN SET RoleTypeOrder_id =  -2147483647;	END IF;

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
			vwRoleType.Role_tp
		,	vwRoleType.ParentRole_tp
		,	vwRoleType.RoleType_tx
		,	vwRoleType.RoleTypeLeft_id
		,	vwRoleType.RoleTypeRight_id
		,	vwRoleType.RoleTypeLevel_id
		,	vwRoleType.RoleTypeOrder_id
		FROM
			vwRoleType
		WHERE
			vwRoleType.Role_tp	= Role_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwRoleType.Role_tp
		,	vwRoleType.ParentRole_tp
		,	vwRoleType.RoleType_tx
		,	vwRoleType.RoleTypeLeft_id
		,	vwRoleType.RoleTypeRight_id
		,	vwRoleType.RoleTypeLevel_id
		,	vwRoleType.RoleTypeOrder_id
		FROM
			vwRoleType
		WHERE
			vwRoleType.Role_tp	= Role_tp

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
			vwRoleType.Role_tp
		,	vwRoleType.ParentRole_tp
		,	vwRoleType.RoleType_tx
		,	vwRoleType.RoleTypeLeft_id
		,	vwRoleType.RoleTypeRight_id
		,	vwRoleType.RoleTypeLevel_id
		,	vwRoleType.RoleTypeOrder_id
		FROM
			vwRoleType
		WHERE
			(
			vwRoleType.Role_tp	= Role_tp
		OR	Role_tp	= '-2147483647'
			)
		AND	(
			vwRoleType.ParentRole_tp	= ParentRole_tp
		OR	ParentRole_tp	= '-2147483647'
			)
		AND	(
			vwRoleType.RoleType_tx	LIKE CONCAT('%', RoleType_tx, '%')
		OR	RoleType_tx	LIKE '-2147483647'
			)
		AND	(
			vwRoleType.RoleTypeLeft_id	= RoleTypeLeft_id
		OR	RoleTypeLeft_id	=  -2147483647
			)
		AND	(
			vwRoleType.RoleTypeRight_id	= RoleTypeRight_id
		OR	RoleTypeRight_id	=  -2147483647
			)
		AND	(
			vwRoleType.RoleTypeLevel_id	= RoleTypeLevel_id
		OR	RoleTypeLevel_id	=  -2147483647
			)
		AND	(
			vwRoleType.RoleTypeOrder_id	= RoleTypeOrder_id
		OR	RoleTypeOrder_id	=  -2147483647
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

