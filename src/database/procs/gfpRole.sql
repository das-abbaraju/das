DROP PROCEDURE IF EXISTS	`gfpRole`
;

DELIMITER //
CREATE PROCEDURE	gfpRole
(
	Role_id		int signed		-- PK1 
,	Role_tp		varchar(80)		-- PK2 AK1
,	Role_nm		varchar(128)		--  AK2
,	Role_cd		varchar(48)	
,	Role_tx		mediumtext	
,	RoleADD_dm		datetime	
,	RoleADD_nm		varchar(128)	
,	RoleUPD_dm		datetime	
,	RoleUPD_nm		varchar(128)	
,	RoleDEL_dm		datetime	
,	RoleDEL_nm		varchar(128)	
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
**	Name:		gfpRole
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwRole
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwRole';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpRole';
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
	IF Role_tx IS NULL OR Role_tx = '' THEN SET Role_tx = '-2147483647';	END IF;
	IF RoleADD_dm IS NULL OR RoleADD_dm = '' THEN SET RoleADD_dm = '0000-00-00 00:00:00';	END IF;
	IF RoleADD_nm IS NULL OR RoleADD_nm = '' THEN SET RoleADD_nm = '-2147483647';	END IF;
	IF RoleUPD_dm IS NULL OR RoleUPD_dm = '' THEN SET RoleUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF RoleUPD_nm IS NULL OR RoleUPD_nm = '' THEN SET RoleUPD_nm = '-2147483647';	END IF;
	IF RoleDEL_dm IS NULL OR RoleDEL_dm = '' THEN SET RoleDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF RoleDEL_nm IS NULL OR RoleDEL_nm = '' THEN SET RoleDEL_nm = '-2147483647';	END IF;
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
			vwRole.Role_id
		,	vwRole.Role_tp
		,	vwRole.Role_nm
		,	vwRole.Role_cd
		,	vwRole.Role_tx
		,	vwRole.RoleADD_dm
		,	vwRole.RoleADD_nm
		,	vwRole.RoleUPD_dm
		,	vwRole.RoleUPD_nm
		,	vwRole.RoleDEL_dm
		,	vwRole.RoleDEL_nm
		,	vwRole.ParentRole_tp
		,	vwRole.RoleType_tx
		,	vwRole.RoleTypeLeft_id
		,	vwRole.RoleTypeRight_id
		,	vwRole.RoleTypeLevel_id
		,	vwRole.RoleTypeOrder_id
		FROM
			vwRole
		WHERE
			vwRole.Role_id	= Role_id
		AND	vwRole.Role_tp	= Role_tp
		AND	vwRole.RoleDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwRole.Role_id
		,	vwRole.Role_tp
		,	vwRole.Role_nm
		,	vwRole.Role_cd
		,	vwRole.Role_tx
		,	vwRole.RoleADD_dm
		,	vwRole.RoleADD_nm
		,	vwRole.RoleUPD_dm
		,	vwRole.RoleUPD_nm
		,	vwRole.RoleDEL_dm
		,	vwRole.RoleDEL_nm
		,	vwRole.ParentRole_tp
		,	vwRole.RoleType_tx
		,	vwRole.RoleTypeLeft_id
		,	vwRole.RoleTypeRight_id
		,	vwRole.RoleTypeLevel_id
		,	vwRole.RoleTypeOrder_id
		FROM
			vwRole
		WHERE
			vwRole.Role_tp	= Role_tp
		AND	vwRole.Role_id	= Role_id
		AND	vwRole.Role_tp	= Role_tp
		AND	vwRole.RoleDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwRole.Role_id
		,	vwRole.Role_tp
		,	vwRole.Role_nm
		,	vwRole.Role_cd
		,	vwRole.Role_tx
		,	vwRole.RoleADD_dm
		,	vwRole.RoleADD_nm
		,	vwRole.RoleUPD_dm
		,	vwRole.RoleUPD_nm
		,	vwRole.RoleDEL_dm
		,	vwRole.RoleDEL_nm
		,	vwRole.ParentRole_tp
		,	vwRole.RoleType_tx
		,	vwRole.RoleTypeLeft_id
		,	vwRole.RoleTypeRight_id
		,	vwRole.RoleTypeLevel_id
		,	vwRole.RoleTypeOrder_id
		FROM
			vwRole
		WHERE
			vwRole.Role_tp	= Role_tp
		AND	vwRole.Role_tp	= Role_tp
		AND	vwRole.RoleDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwRole.Role_id
		,	vwRole.Role_tp
		,	vwRole.Role_nm
		,	vwRole.Role_cd
		,	vwRole.Role_tx
		,	vwRole.RoleADD_dm
		,	vwRole.RoleADD_nm
		,	vwRole.RoleUPD_dm
		,	vwRole.RoleUPD_nm
		,	vwRole.RoleDEL_dm
		,	vwRole.RoleDEL_nm
		,	vwRole.ParentRole_tp
		,	vwRole.RoleType_tx
		,	vwRole.RoleTypeLeft_id
		,	vwRole.RoleTypeRight_id
		,	vwRole.RoleTypeLevel_id
		,	vwRole.RoleTypeOrder_id
		FROM
			vwRole
		WHERE
			vwRole.Role_tp	= Role_tp
		AND	vwRole.Role_nm	= Role_nm
		AND	vwRole.RoleDEL_dm	IS NULL

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
			vwRole.Role_id
		,	vwRole.Role_tp
		,	vwRole.Role_nm
		,	vwRole.Role_cd
		,	vwRole.Role_tx
		,	vwRole.RoleADD_dm
		,	vwRole.RoleADD_nm
		,	vwRole.RoleUPD_dm
		,	vwRole.RoleUPD_nm
		,	vwRole.RoleDEL_dm
		,	vwRole.RoleDEL_nm
		,	vwRole.ParentRole_tp
		,	vwRole.RoleType_tx
		,	vwRole.RoleTypeLeft_id
		,	vwRole.RoleTypeRight_id
		,	vwRole.RoleTypeLevel_id
		,	vwRole.RoleTypeOrder_id
		FROM
			vwRole
		WHERE
			(
			vwRole.Role_id	= Role_id
		OR	Role_id	=  -2147483647
			)
		AND	(
			vwRole.Role_tp	= Role_tp
		OR	Role_tp	= '-2147483647'
			)
		AND	(
			vwRole.Role_nm	LIKE CONCAT('%', Role_nm, '%')
		OR	Role_nm	= '-2147483647'
			)
		AND	(
			vwRole.Role_cd	LIKE CONCAT('%', Role_cd, '%')
		OR	Role_cd	= '-2147483647'
			)
		AND	(
			vwRole.Role_tx	LIKE CONCAT('%', Role_tx, '%')
		OR	Role_tx	LIKE '-2147483647'
			)
		AND	(
			vwRole.RoleADD_dm	= RoleADD_dm
		OR	RoleADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwRole.RoleADD_nm	LIKE CONCAT('%', RoleADD_nm, '%')
		OR	RoleADD_nm	= '-2147483647'
			)
		AND	(
			vwRole.RoleUPD_dm	= RoleUPD_dm
		OR	RoleUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwRole.RoleUPD_nm	LIKE CONCAT('%', RoleUPD_nm, '%')
		OR	RoleUPD_nm	= '-2147483647'
			)
		AND	(
			vwRole.RoleDEL_dm	= RoleDEL_dm
		OR	RoleDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwRole.RoleDEL_nm	LIKE CONCAT('%', RoleDEL_nm, '%')
		OR	RoleDEL_nm	= '-2147483647'
			)
		AND	(
			vwRole.ParentRole_tp	= ParentRole_tp
		OR	ParentRole_tp	= '-2147483647'
			)
		AND	(
			vwRole.RoleType_tx	LIKE CONCAT('%', RoleType_tx, '%')
		OR	RoleType_tx	LIKE '-2147483647'
			)
		AND	(
			vwRole.RoleTypeLeft_id	= RoleTypeLeft_id
		OR	RoleTypeLeft_id	=  -2147483647
			)
		AND	(
			vwRole.RoleTypeRight_id	= RoleTypeRight_id
		OR	RoleTypeRight_id	=  -2147483647
			)
		AND	(
			vwRole.RoleTypeLevel_id	= RoleTypeLevel_id
		OR	RoleTypeLevel_id	=  -2147483647
			)
		AND	(
			vwRole.RoleTypeOrder_id	= RoleTypeOrder_id
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

