DROP PROCEDURE IF EXISTS	`gfpUser_Role`
;

DELIMITER //
CREATE PROCEDURE	gfpUser_Role
(
	User_id		int signed		-- PK1 
,	User_tp		varchar(80)		-- PK2 AK1
,	User_nm		varchar(128)		--  AK2
,	Domain_nm		varchar(128)	
,	Password_cd		varchar(48)	
,	Email_tx		mediumtext	
,	Role_id		int signed		-- PK3 
,	Role_tp		varchar(80)		-- PK4 AK3
,	Role_nm		varchar(128)		--  AK4
,	Role_cd		varchar(48)	
,	User_tx		mediumtext	
,	Role_tx		mediumtext	
,	UserADD_dm		datetime	
,	UserADD_nm		varchar(128)	
,	UserUPD_dm		datetime	
,	UserUPD_nm		varchar(128)	
,	UserDEL_dm		datetime	
,	UserDEL_nm		varchar(128)	
,	ParentUser_tp		varchar(80)	
,	UserType_tx		mediumtext	
,	RoleADD_dm		datetime	
,	RoleADD_nm		varchar(128)	
,	RoleUPD_dm		datetime	
,	RoleUPD_nm		varchar(128)	
,	RoleDEL_dm		datetime	
,	RoleDEL_nm		varchar(128)	
,	ParentRole_tp		varchar(80)	
,	RoleType_tx		mediumtext	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpUser_Role
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwUser_Role
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwUser_Role';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpUser_Role';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF User_id IS NULL OR User_id = 0 THEN SET User_id =  -2147483647;	END IF;
	IF User_tp IS NULL OR User_tp = '' THEN SET User_tp = '-2147483647';	END IF;
	IF User_nm IS NULL OR User_nm = '' THEN SET User_nm = '-2147483647';	END IF;
	IF Domain_nm IS NULL OR Domain_nm = '' THEN SET Domain_nm = '-2147483647';	END IF;
	IF Password_cd IS NULL OR Password_cd = '' THEN SET Password_cd = '-2147483647';	END IF;
	IF Email_tx IS NULL OR Email_tx = '' THEN SET Email_tx = '-2147483647';	END IF;
	IF Role_id IS NULL OR Role_id = 0 THEN SET Role_id =  -2147483647;	END IF;
	IF Role_tp IS NULL OR Role_tp = '' THEN SET Role_tp = '-2147483647';	END IF;
	IF Role_nm IS NULL OR Role_nm = '' THEN SET Role_nm = '-2147483647';	END IF;
	IF Role_cd IS NULL OR Role_cd = '' THEN SET Role_cd = '-2147483647';	END IF;
	IF User_tx IS NULL OR User_tx = '' THEN SET User_tx = '-2147483647';	END IF;
	IF Role_tx IS NULL OR Role_tx = '' THEN SET Role_tx = '-2147483647';	END IF;
	IF UserADD_dm IS NULL OR UserADD_dm = '' THEN SET UserADD_dm = '0000-00-00 00:00:00';	END IF;
	IF UserADD_nm IS NULL OR UserADD_nm = '' THEN SET UserADD_nm = '-2147483647';	END IF;
	IF UserUPD_dm IS NULL OR UserUPD_dm = '' THEN SET UserUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF UserUPD_nm IS NULL OR UserUPD_nm = '' THEN SET UserUPD_nm = '-2147483647';	END IF;
	IF UserDEL_dm IS NULL OR UserDEL_dm = '' THEN SET UserDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF UserDEL_nm IS NULL OR UserDEL_nm = '' THEN SET UserDEL_nm = '-2147483647';	END IF;
	IF ParentUser_tp IS NULL OR ParentUser_tp = '' THEN SET ParentUser_tp = '-2147483647';	END IF;
	IF UserType_tx IS NULL OR UserType_tx = '' THEN SET UserType_tx = '-2147483647';	END IF;
	IF RoleADD_dm IS NULL OR RoleADD_dm = '' THEN SET RoleADD_dm = '0000-00-00 00:00:00';	END IF;
	IF RoleADD_nm IS NULL OR RoleADD_nm = '' THEN SET RoleADD_nm = '-2147483647';	END IF;
	IF RoleUPD_dm IS NULL OR RoleUPD_dm = '' THEN SET RoleUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF RoleUPD_nm IS NULL OR RoleUPD_nm = '' THEN SET RoleUPD_nm = '-2147483647';	END IF;
	IF RoleDEL_dm IS NULL OR RoleDEL_dm = '' THEN SET RoleDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF RoleDEL_nm IS NULL OR RoleDEL_nm = '' THEN SET RoleDEL_nm = '-2147483647';	END IF;
	IF ParentRole_tp IS NULL OR ParentRole_tp = '' THEN SET ParentRole_tp = '-2147483647';	END IF;
	IF RoleType_tx IS NULL OR RoleType_tx = '' THEN SET RoleType_tx = '-2147483647';	END IF;

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
			vwUser_Role.User_id
		,	vwUser_Role.User_tp
		,	vwUser_Role.User_nm
		,	vwUser_Role.Domain_nm
		,	vwUser_Role.Password_cd
		,	vwUser_Role.Email_tx
		,	vwUser_Role.Role_id
		,	vwUser_Role.Role_tp
		,	vwUser_Role.Role_nm
		,	vwUser_Role.Role_cd
		,	vwUser_Role.User_tx
		,	vwUser_Role.Role_tx
		,	vwUser_Role.UserADD_dm
		,	vwUser_Role.UserADD_nm
		,	vwUser_Role.UserUPD_dm
		,	vwUser_Role.UserUPD_nm
		,	vwUser_Role.UserDEL_dm
		,	vwUser_Role.UserDEL_nm
		,	vwUser_Role.ParentUser_tp
		,	vwUser_Role.UserType_tx
		,	vwUser_Role.RoleADD_dm
		,	vwUser_Role.RoleADD_nm
		,	vwUser_Role.RoleUPD_dm
		,	vwUser_Role.RoleUPD_nm
		,	vwUser_Role.RoleDEL_dm
		,	vwUser_Role.RoleDEL_nm
		,	vwUser_Role.ParentRole_tp
		,	vwUser_Role.RoleType_tx
		FROM
			vwUser_Role
		WHERE
			vwUser_Role.User_id	= User_id
		AND	vwUser_Role.User_tp	= User_tp
		AND	vwUser_Role.Role_id	= Role_id
		AND	vwUser_Role.Role_tp	= Role_tp
		AND	vwUser_Role.UserDEL_dm	IS NULL
		AND	vwUser_Role.RoleDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwUser_Role.User_id
		,	vwUser_Role.User_tp
		,	vwUser_Role.User_nm
		,	vwUser_Role.Domain_nm
		,	vwUser_Role.Password_cd
		,	vwUser_Role.Email_tx
		,	vwUser_Role.Role_id
		,	vwUser_Role.Role_tp
		,	vwUser_Role.Role_nm
		,	vwUser_Role.Role_cd
		,	vwUser_Role.User_tx
		,	vwUser_Role.Role_tx
		,	vwUser_Role.UserADD_dm
		,	vwUser_Role.UserADD_nm
		,	vwUser_Role.UserUPD_dm
		,	vwUser_Role.UserUPD_nm
		,	vwUser_Role.UserDEL_dm
		,	vwUser_Role.UserDEL_nm
		,	vwUser_Role.ParentUser_tp
		,	vwUser_Role.UserType_tx
		,	vwUser_Role.RoleADD_dm
		,	vwUser_Role.RoleADD_nm
		,	vwUser_Role.RoleUPD_dm
		,	vwUser_Role.RoleUPD_nm
		,	vwUser_Role.RoleDEL_dm
		,	vwUser_Role.RoleDEL_nm
		,	vwUser_Role.ParentRole_tp
		,	vwUser_Role.RoleType_tx
		FROM
			vwUser_Role
		WHERE
			vwUser_Role.User_id	= User_id
		AND	vwUser_Role.User_tp	= User_tp
		AND	vwUser_Role.UserDEL_dm	IS NULL
		AND	vwUser_Role.RoleDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwUser_Role.User_id
		,	vwUser_Role.User_tp
		,	vwUser_Role.User_nm
		,	vwUser_Role.Domain_nm
		,	vwUser_Role.Password_cd
		,	vwUser_Role.Email_tx
		,	vwUser_Role.Role_id
		,	vwUser_Role.Role_tp
		,	vwUser_Role.Role_nm
		,	vwUser_Role.Role_cd
		,	vwUser_Role.User_tx
		,	vwUser_Role.Role_tx
		,	vwUser_Role.UserADD_dm
		,	vwUser_Role.UserADD_nm
		,	vwUser_Role.UserUPD_dm
		,	vwUser_Role.UserUPD_nm
		,	vwUser_Role.UserDEL_dm
		,	vwUser_Role.UserDEL_nm
		,	vwUser_Role.ParentUser_tp
		,	vwUser_Role.UserType_tx
		,	vwUser_Role.RoleADD_dm
		,	vwUser_Role.RoleADD_nm
		,	vwUser_Role.RoleUPD_dm
		,	vwUser_Role.RoleUPD_nm
		,	vwUser_Role.RoleDEL_dm
		,	vwUser_Role.RoleDEL_nm
		,	vwUser_Role.ParentRole_tp
		,	vwUser_Role.RoleType_tx
		FROM
			vwUser_Role
		WHERE
			vwUser_Role.Role_id	= Role_id
		AND	vwUser_Role.Role_tp	= Role_tp
		AND	vwUser_Role.UserDEL_dm	IS NULL
		AND	vwUser_Role.RoleDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwUser_Role.User_id
		,	vwUser_Role.User_tp
		,	vwUser_Role.User_nm
		,	vwUser_Role.Domain_nm
		,	vwUser_Role.Password_cd
		,	vwUser_Role.Email_tx
		,	vwUser_Role.Role_id
		,	vwUser_Role.Role_tp
		,	vwUser_Role.Role_nm
		,	vwUser_Role.Role_cd
		,	vwUser_Role.User_tx
		,	vwUser_Role.Role_tx
		,	vwUser_Role.UserADD_dm
		,	vwUser_Role.UserADD_nm
		,	vwUser_Role.UserUPD_dm
		,	vwUser_Role.UserUPD_nm
		,	vwUser_Role.UserDEL_dm
		,	vwUser_Role.UserDEL_nm
		,	vwUser_Role.ParentUser_tp
		,	vwUser_Role.UserType_tx
		,	vwUser_Role.RoleADD_dm
		,	vwUser_Role.RoleADD_nm
		,	vwUser_Role.RoleUPD_dm
		,	vwUser_Role.RoleUPD_nm
		,	vwUser_Role.RoleDEL_dm
		,	vwUser_Role.RoleDEL_nm
		,	vwUser_Role.ParentRole_tp
		,	vwUser_Role.RoleType_tx
		FROM
			vwUser_Role
		WHERE
			vwUser_Role.User_tp	= User_tp
		AND	vwUser_Role.User_nm	= User_nm
		AND	vwUser_Role.Role_tp	= Role_tp
		AND	vwUser_Role.Role_nm	= Role_nm
		AND	vwUser_Role.UserDEL_dm	IS NULL
		AND	vwUser_Role.RoleDEL_dm	IS NULL

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
			vwUser_Role.User_id
		,	vwUser_Role.User_tp
		,	vwUser_Role.User_nm
		,	vwUser_Role.Domain_nm
		,	vwUser_Role.Password_cd
		,	vwUser_Role.Email_tx
		,	vwUser_Role.Role_id
		,	vwUser_Role.Role_tp
		,	vwUser_Role.Role_nm
		,	vwUser_Role.Role_cd
		,	vwUser_Role.User_tx
		,	vwUser_Role.Role_tx
		,	vwUser_Role.UserADD_dm
		,	vwUser_Role.UserADD_nm
		,	vwUser_Role.UserUPD_dm
		,	vwUser_Role.UserUPD_nm
		,	vwUser_Role.UserDEL_dm
		,	vwUser_Role.UserDEL_nm
		,	vwUser_Role.ParentUser_tp
		,	vwUser_Role.UserType_tx
		,	vwUser_Role.RoleADD_dm
		,	vwUser_Role.RoleADD_nm
		,	vwUser_Role.RoleUPD_dm
		,	vwUser_Role.RoleUPD_nm
		,	vwUser_Role.RoleDEL_dm
		,	vwUser_Role.RoleDEL_nm
		,	vwUser_Role.ParentRole_tp
		,	vwUser_Role.RoleType_tx
		FROM
			vwUser_Role
		WHERE
			(
			User_id	= User_id
		OR	User_id	=  -2147483647
			)
		AND	(
			User_tp	= User_tp
		OR	User_tp	= '-2147483647'
			)
		AND	(
			User_nm	LIKE CONCAT('%', User_nm, '%')
		OR	User_nm	= '-2147483647'
			)
		AND	(
			Domain_nm	LIKE CONCAT('%', Domain_nm, '%')
		OR	Domain_nm	= '-2147483647'
			)
		AND	(
			Password_cd	LIKE CONCAT('%', Password_cd, '%')
		OR	Password_cd	= '-2147483647'
			)
		AND	(
			Email_tx	LIKE CONCAT('%', Email_tx, '%')
		OR	Email_tx	LIKE '-2147483647'
			)
		AND	(
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
			User_tx	LIKE CONCAT('%', User_tx, '%')
		OR	User_tx	LIKE '-2147483647'
			)
		AND	(
			Role_tx	LIKE CONCAT('%', Role_tx, '%')
		OR	Role_tx	LIKE '-2147483647'
			)
		AND	(
			UserADD_dm	= UserADD_dm
		OR	UserADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			UserADD_nm	LIKE CONCAT('%', UserADD_nm, '%')
		OR	UserADD_nm	= '-2147483647'
			)
		AND	(
			UserUPD_dm	= UserUPD_dm
		OR	UserUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			UserUPD_nm	LIKE CONCAT('%', UserUPD_nm, '%')
		OR	UserUPD_nm	= '-2147483647'
			)
		AND	(
			UserDEL_dm	= UserDEL_dm
		OR	UserDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			UserDEL_nm	LIKE CONCAT('%', UserDEL_nm, '%')
		OR	UserDEL_nm	= '-2147483647'
			)
		AND	(
			ParentUser_tp	= ParentUser_tp
		OR	ParentUser_tp	= '-2147483647'
			)
		AND	(
			UserType_tx	LIKE CONCAT('%', UserType_tx, '%')
		OR	UserType_tx	LIKE '-2147483647'
			)
		AND	(
			RoleADD_dm	= RoleADD_dm
		OR	RoleADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			RoleADD_nm	LIKE CONCAT('%', RoleADD_nm, '%')
		OR	RoleADD_nm	= '-2147483647'
			)
		AND	(
			RoleUPD_dm	= RoleUPD_dm
		OR	RoleUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			RoleUPD_nm	LIKE CONCAT('%', RoleUPD_nm, '%')
		OR	RoleUPD_nm	= '-2147483647'
			)
		AND	(
			RoleDEL_dm	= RoleDEL_dm
		OR	RoleDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			RoleDEL_nm	LIKE CONCAT('%', RoleDEL_nm, '%')
		OR	RoleDEL_nm	= '-2147483647'
			)
		AND	(
			ParentRole_tp	= ParentRole_tp
		OR	ParentRole_tp	= '-2147483647'
			)
		AND	(
			RoleType_tx	LIKE CONCAT('%', RoleType_tx, '%')
		OR	RoleType_tx	LIKE '-2147483647'
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

