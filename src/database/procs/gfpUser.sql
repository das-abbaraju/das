DROP PROCEDURE IF EXISTS	`gfpUser`
;

DELIMITER //
CREATE PROCEDURE	gfpUser
(
	User_id		int signed		-- PK1 
,	User_tp		varchar(80)		-- PK2 AK1
,	User_nm		varchar(128)		--  AK2
,	Domain_nm		varchar(128)	
,	Password_cd		varchar(48)	
,	Email_tx		mediumtext	
,	User_tx		mediumtext	
,	UserADD_dm		datetime	
,	UserADD_nm		varchar(128)	
,	UserUPD_dm		datetime	
,	UserUPD_nm		varchar(128)	
,	UserDEL_dm		datetime	
,	UserDEL_nm		varchar(128)	
,	ParentUser_tp		varchar(80)	
,	UserType_tx		mediumtext	
,	UserTypeLeft_id		int signed	
,	UserTypeRight_id		int signed	
,	UserTypeLevel_id		int signed	
,	UserTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpUser
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwUser
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwUser';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpUser';
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
	IF User_tx IS NULL OR User_tx = '' THEN SET User_tx = '-2147483647';	END IF;
	IF UserADD_dm IS NULL OR UserADD_dm = '' THEN SET UserADD_dm = '0000-00-00 00:00:00';	END IF;
	IF UserADD_nm IS NULL OR UserADD_nm = '' THEN SET UserADD_nm = '-2147483647';	END IF;
	IF UserUPD_dm IS NULL OR UserUPD_dm = '' THEN SET UserUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF UserUPD_nm IS NULL OR UserUPD_nm = '' THEN SET UserUPD_nm = '-2147483647';	END IF;
	IF UserDEL_dm IS NULL OR UserDEL_dm = '' THEN SET UserDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF UserDEL_nm IS NULL OR UserDEL_nm = '' THEN SET UserDEL_nm = '-2147483647';	END IF;
	IF ParentUser_tp IS NULL OR ParentUser_tp = '' THEN SET ParentUser_tp = '-2147483647';	END IF;
	IF UserType_tx IS NULL OR UserType_tx = '' THEN SET UserType_tx = '-2147483647';	END IF;
	IF UserTypeLeft_id IS NULL OR UserTypeLeft_id = 0 THEN SET UserTypeLeft_id =  -2147483647;	END IF;
	IF UserTypeRight_id IS NULL OR UserTypeRight_id = 0 THEN SET UserTypeRight_id =  -2147483647;	END IF;
	IF UserTypeLevel_id IS NULL OR UserTypeLevel_id = 0 THEN SET UserTypeLevel_id =  -2147483647;	END IF;
	IF UserTypeOrder_id IS NULL OR UserTypeOrder_id = 0 THEN SET UserTypeOrder_id =  -2147483647;	END IF;

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
			vwUser.User_id
		,	vwUser.User_tp
		,	vwUser.User_nm
		,	vwUser.Domain_nm
		,	vwUser.Password_cd
		,	vwUser.Email_tx
		,	vwUser.User_tx
		,	vwUser.UserADD_dm
		,	vwUser.UserADD_nm
		,	vwUser.UserUPD_dm
		,	vwUser.UserUPD_nm
		,	vwUser.UserDEL_dm
		,	vwUser.UserDEL_nm
		,	vwUser.ParentUser_tp
		,	vwUser.UserType_tx
		,	vwUser.UserTypeLeft_id
		,	vwUser.UserTypeRight_id
		,	vwUser.UserTypeLevel_id
		,	vwUser.UserTypeOrder_id
		FROM
			vwUser
		WHERE
			vwUser.User_id	= User_id
		AND	vwUser.User_tp	= User_tp
		AND	vwUser.UserDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwUser.User_id
		,	vwUser.User_tp
		,	vwUser.User_nm
		,	vwUser.Domain_nm
		,	vwUser.Password_cd
		,	vwUser.Email_tx
		,	vwUser.User_tx
		,	vwUser.UserADD_dm
		,	vwUser.UserADD_nm
		,	vwUser.UserUPD_dm
		,	vwUser.UserUPD_nm
		,	vwUser.UserDEL_dm
		,	vwUser.UserDEL_nm
		,	vwUser.ParentUser_tp
		,	vwUser.UserType_tx
		,	vwUser.UserTypeLeft_id
		,	vwUser.UserTypeRight_id
		,	vwUser.UserTypeLevel_id
		,	vwUser.UserTypeOrder_id
		FROM
			vwUser
		WHERE
			vwUser.User_tp	= User_tp
		AND	vwUser.User_id	= User_id
		AND	vwUser.User_tp	= User_tp
		AND	vwUser.UserDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwUser.User_id
		,	vwUser.User_tp
		,	vwUser.User_nm
		,	vwUser.Domain_nm
		,	vwUser.Password_cd
		,	vwUser.Email_tx
		,	vwUser.User_tx
		,	vwUser.UserADD_dm
		,	vwUser.UserADD_nm
		,	vwUser.UserUPD_dm
		,	vwUser.UserUPD_nm
		,	vwUser.UserDEL_dm
		,	vwUser.UserDEL_nm
		,	vwUser.ParentUser_tp
		,	vwUser.UserType_tx
		,	vwUser.UserTypeLeft_id
		,	vwUser.UserTypeRight_id
		,	vwUser.UserTypeLevel_id
		,	vwUser.UserTypeOrder_id
		FROM
			vwUser
		WHERE
			vwUser.User_tp	= User_tp
		AND	vwUser.User_tp	= User_tp
		AND	vwUser.UserDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwUser.User_id
		,	vwUser.User_tp
		,	vwUser.User_nm
		,	vwUser.Domain_nm
		,	vwUser.Password_cd
		,	vwUser.Email_tx
		,	vwUser.User_tx
		,	vwUser.UserADD_dm
		,	vwUser.UserADD_nm
		,	vwUser.UserUPD_dm
		,	vwUser.UserUPD_nm
		,	vwUser.UserDEL_dm
		,	vwUser.UserDEL_nm
		,	vwUser.ParentUser_tp
		,	vwUser.UserType_tx
		,	vwUser.UserTypeLeft_id
		,	vwUser.UserTypeRight_id
		,	vwUser.UserTypeLevel_id
		,	vwUser.UserTypeOrder_id
		FROM
			vwUser
		WHERE
			vwUser.User_tp	= User_tp
		AND	vwUser.User_nm	= User_nm
		AND	vwUser.UserDEL_dm	IS NULL

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
			vwUser.User_id
		,	vwUser.User_tp
		,	vwUser.User_nm
		,	vwUser.Domain_nm
		,	vwUser.Password_cd
		,	vwUser.Email_tx
		,	vwUser.User_tx
		,	vwUser.UserADD_dm
		,	vwUser.UserADD_nm
		,	vwUser.UserUPD_dm
		,	vwUser.UserUPD_nm
		,	vwUser.UserDEL_dm
		,	vwUser.UserDEL_nm
		,	vwUser.ParentUser_tp
		,	vwUser.UserType_tx
		,	vwUser.UserTypeLeft_id
		,	vwUser.UserTypeRight_id
		,	vwUser.UserTypeLevel_id
		,	vwUser.UserTypeOrder_id
		FROM
			vwUser
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
			User_tx	LIKE CONCAT('%', User_tx, '%')
		OR	User_tx	LIKE '-2147483647'
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
			UserTypeLeft_id	= UserTypeLeft_id
		OR	UserTypeLeft_id	=  -2147483647
			)
		AND	(
			UserTypeRight_id	= UserTypeRight_id
		OR	UserTypeRight_id	=  -2147483647
			)
		AND	(
			UserTypeLevel_id	= UserTypeLevel_id
		OR	UserTypeLevel_id	=  -2147483647
			)
		AND	(
			UserTypeOrder_id	= UserTypeOrder_id
		OR	UserTypeOrder_id	=  -2147483647
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

