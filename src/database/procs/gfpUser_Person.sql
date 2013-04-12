DROP PROCEDURE IF EXISTS	`gfpUser_Person`
;

DELIMITER //
CREATE PROCEDURE	gfpUser_Person
(
	User_id		int signed		-- PK1 
,	User_tp		varchar(80)		-- PK2 AK1
,	User_nm		varchar(128)		--  AK2
,	Domain_nm		varchar(128)	
,	Password_cd		varchar(48)	
,	Email_tx		mediumtext	
,	Person_id		int signed		-- PK3 
,	Person_tp		varchar(80)		-- PK4 AK3
,	Person_nm		varchar(128)		--  AK4
,	First_nm		varchar(128)	
,	Middle_nm		varchar(128)	
,	Last_nm		varchar(128)	
,	FirstSNDX_cd		varchar(48)	
,	LastSNDX_cd		varchar(48)	
,	Birth_dm		datetime	
,	Gender_cd		varchar(48)	
,	User_tx		mediumtext	
,	Person_tx		mediumtext	
,	UserADD_dm		datetime	
,	UserADD_nm		varchar(128)	
,	UserUPD_dm		datetime	
,	UserUPD_nm		varchar(128)	
,	UserDEL_dm		datetime	
,	UserDEL_nm		varchar(128)	
,	ParentUser_tp		varchar(80)	
,	UserType_tx		mediumtext	
,	PersonADD_dm		datetime	
,	PersonADD_nm		varchar(128)	
,	PersonUPD_dm		datetime	
,	PersonUPD_nm		varchar(128)	
,	PersonDEL_dm		datetime	
,	PersonDEL_nm		varchar(128)	
,	ParentPerson_tp		varchar(80)	
,	PersonType_tx		mediumtext	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpUser_Person
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwUser_Person
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwUser_Person';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpUser_Person';
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
	IF Person_id IS NULL OR Person_id = 0 THEN SET Person_id =  -2147483647;	END IF;
	IF Person_tp IS NULL OR Person_tp = '' THEN SET Person_tp = '-2147483647';	END IF;
	IF Person_nm IS NULL OR Person_nm = '' THEN SET Person_nm = '-2147483647';	END IF;
	IF First_nm IS NULL OR First_nm = '' THEN SET First_nm = '-2147483647';	END IF;
	IF Middle_nm IS NULL OR Middle_nm = '' THEN SET Middle_nm = '-2147483647';	END IF;
	IF Last_nm IS NULL OR Last_nm = '' THEN SET Last_nm = '-2147483647';	END IF;
	IF FirstSNDX_cd IS NULL OR FirstSNDX_cd = '' THEN SET FirstSNDX_cd = '-2147483647';	END IF;
	IF LastSNDX_cd IS NULL OR LastSNDX_cd = '' THEN SET LastSNDX_cd = '-2147483647';	END IF;
	IF Birth_dm IS NULL OR Birth_dm = '' THEN SET Birth_dm = '0000-00-00 00:00:00';	END IF;
	IF Gender_cd IS NULL OR Gender_cd = '' THEN SET Gender_cd = '-2147483647';	END IF;
	IF User_tx IS NULL OR User_tx = '' THEN SET User_tx = '-2147483647';	END IF;
	IF Person_tx IS NULL OR Person_tx = '' THEN SET Person_tx = '-2147483647';	END IF;
	IF UserADD_dm IS NULL OR UserADD_dm = '' THEN SET UserADD_dm = '0000-00-00 00:00:00';	END IF;
	IF UserADD_nm IS NULL OR UserADD_nm = '' THEN SET UserADD_nm = '-2147483647';	END IF;
	IF UserUPD_dm IS NULL OR UserUPD_dm = '' THEN SET UserUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF UserUPD_nm IS NULL OR UserUPD_nm = '' THEN SET UserUPD_nm = '-2147483647';	END IF;
	IF UserDEL_dm IS NULL OR UserDEL_dm = '' THEN SET UserDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF UserDEL_nm IS NULL OR UserDEL_nm = '' THEN SET UserDEL_nm = '-2147483647';	END IF;
	IF ParentUser_tp IS NULL OR ParentUser_tp = '' THEN SET ParentUser_tp = '-2147483647';	END IF;
	IF UserType_tx IS NULL OR UserType_tx = '' THEN SET UserType_tx = '-2147483647';	END IF;
	IF PersonADD_dm IS NULL OR PersonADD_dm = '' THEN SET PersonADD_dm = '0000-00-00 00:00:00';	END IF;
	IF PersonADD_nm IS NULL OR PersonADD_nm = '' THEN SET PersonADD_nm = '-2147483647';	END IF;
	IF PersonUPD_dm IS NULL OR PersonUPD_dm = '' THEN SET PersonUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF PersonUPD_nm IS NULL OR PersonUPD_nm = '' THEN SET PersonUPD_nm = '-2147483647';	END IF;
	IF PersonDEL_dm IS NULL OR PersonDEL_dm = '' THEN SET PersonDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF PersonDEL_nm IS NULL OR PersonDEL_nm = '' THEN SET PersonDEL_nm = '-2147483647';	END IF;
	IF ParentPerson_tp IS NULL OR ParentPerson_tp = '' THEN SET ParentPerson_tp = '-2147483647';	END IF;
	IF PersonType_tx IS NULL OR PersonType_tx = '' THEN SET PersonType_tx = '-2147483647';	END IF;

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
			vwUser_Person.User_id
		,	vwUser_Person.User_tp
		,	vwUser_Person.User_nm
		,	vwUser_Person.Domain_nm
		,	vwUser_Person.Password_cd
		,	vwUser_Person.Email_tx
		,	vwUser_Person.Person_id
		,	vwUser_Person.Person_tp
		,	vwUser_Person.Person_nm
		,	vwUser_Person.First_nm
		,	vwUser_Person.Middle_nm
		,	vwUser_Person.Last_nm
		,	vwUser_Person.FirstSNDX_cd
		,	vwUser_Person.LastSNDX_cd
		,	vwUser_Person.Birth_dm
		,	vwUser_Person.Gender_cd
		,	vwUser_Person.User_tx
		,	vwUser_Person.Person_tx
		,	vwUser_Person.UserADD_dm
		,	vwUser_Person.UserADD_nm
		,	vwUser_Person.UserUPD_dm
		,	vwUser_Person.UserUPD_nm
		,	vwUser_Person.UserDEL_dm
		,	vwUser_Person.UserDEL_nm
		,	vwUser_Person.ParentUser_tp
		,	vwUser_Person.UserType_tx
		,	vwUser_Person.PersonADD_dm
		,	vwUser_Person.PersonADD_nm
		,	vwUser_Person.PersonUPD_dm
		,	vwUser_Person.PersonUPD_nm
		,	vwUser_Person.PersonDEL_dm
		,	vwUser_Person.PersonDEL_nm
		,	vwUser_Person.ParentPerson_tp
		,	vwUser_Person.PersonType_tx
		FROM
			vwUser_Person
		WHERE
			vwUser_Person.User_id	= User_id
		AND	vwUser_Person.User_tp	= User_tp
		AND	vwUser_Person.Person_id	= Person_id
		AND	vwUser_Person.Person_tp	= Person_tp
		AND	vwUser_Person.UserDEL_dm	IS NULL
		AND	vwUser_Person.PersonDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwUser_Person.User_id
		,	vwUser_Person.User_tp
		,	vwUser_Person.User_nm
		,	vwUser_Person.Domain_nm
		,	vwUser_Person.Password_cd
		,	vwUser_Person.Email_tx
		,	vwUser_Person.Person_id
		,	vwUser_Person.Person_tp
		,	vwUser_Person.Person_nm
		,	vwUser_Person.First_nm
		,	vwUser_Person.Middle_nm
		,	vwUser_Person.Last_nm
		,	vwUser_Person.FirstSNDX_cd
		,	vwUser_Person.LastSNDX_cd
		,	vwUser_Person.Birth_dm
		,	vwUser_Person.Gender_cd
		,	vwUser_Person.User_tx
		,	vwUser_Person.Person_tx
		,	vwUser_Person.UserADD_dm
		,	vwUser_Person.UserADD_nm
		,	vwUser_Person.UserUPD_dm
		,	vwUser_Person.UserUPD_nm
		,	vwUser_Person.UserDEL_dm
		,	vwUser_Person.UserDEL_nm
		,	vwUser_Person.ParentUser_tp
		,	vwUser_Person.UserType_tx
		,	vwUser_Person.PersonADD_dm
		,	vwUser_Person.PersonADD_nm
		,	vwUser_Person.PersonUPD_dm
		,	vwUser_Person.PersonUPD_nm
		,	vwUser_Person.PersonDEL_dm
		,	vwUser_Person.PersonDEL_nm
		,	vwUser_Person.ParentPerson_tp
		,	vwUser_Person.PersonType_tx
		FROM
			vwUser_Person
		WHERE
			vwUser_Person.User_id	= User_id
		AND	vwUser_Person.User_tp	= User_tp
		AND	vwUser_Person.UserDEL_dm	IS NULL
		AND	vwUser_Person.PersonDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwUser_Person.User_id
		,	vwUser_Person.User_tp
		,	vwUser_Person.User_nm
		,	vwUser_Person.Domain_nm
		,	vwUser_Person.Password_cd
		,	vwUser_Person.Email_tx
		,	vwUser_Person.Person_id
		,	vwUser_Person.Person_tp
		,	vwUser_Person.Person_nm
		,	vwUser_Person.First_nm
		,	vwUser_Person.Middle_nm
		,	vwUser_Person.Last_nm
		,	vwUser_Person.FirstSNDX_cd
		,	vwUser_Person.LastSNDX_cd
		,	vwUser_Person.Birth_dm
		,	vwUser_Person.Gender_cd
		,	vwUser_Person.User_tx
		,	vwUser_Person.Person_tx
		,	vwUser_Person.UserADD_dm
		,	vwUser_Person.UserADD_nm
		,	vwUser_Person.UserUPD_dm
		,	vwUser_Person.UserUPD_nm
		,	vwUser_Person.UserDEL_dm
		,	vwUser_Person.UserDEL_nm
		,	vwUser_Person.ParentUser_tp
		,	vwUser_Person.UserType_tx
		,	vwUser_Person.PersonADD_dm
		,	vwUser_Person.PersonADD_nm
		,	vwUser_Person.PersonUPD_dm
		,	vwUser_Person.PersonUPD_nm
		,	vwUser_Person.PersonDEL_dm
		,	vwUser_Person.PersonDEL_nm
		,	vwUser_Person.ParentPerson_tp
		,	vwUser_Person.PersonType_tx
		FROM
			vwUser_Person
		WHERE
			vwUser_Person.Person_id	= Person_id
		AND	vwUser_Person.Person_tp	= Person_tp
		AND	vwUser_Person.UserDEL_dm	IS NULL
		AND	vwUser_Person.PersonDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwUser_Person.User_id
		,	vwUser_Person.User_tp
		,	vwUser_Person.User_nm
		,	vwUser_Person.Domain_nm
		,	vwUser_Person.Password_cd
		,	vwUser_Person.Email_tx
		,	vwUser_Person.Person_id
		,	vwUser_Person.Person_tp
		,	vwUser_Person.Person_nm
		,	vwUser_Person.First_nm
		,	vwUser_Person.Middle_nm
		,	vwUser_Person.Last_nm
		,	vwUser_Person.FirstSNDX_cd
		,	vwUser_Person.LastSNDX_cd
		,	vwUser_Person.Birth_dm
		,	vwUser_Person.Gender_cd
		,	vwUser_Person.User_tx
		,	vwUser_Person.Person_tx
		,	vwUser_Person.UserADD_dm
		,	vwUser_Person.UserADD_nm
		,	vwUser_Person.UserUPD_dm
		,	vwUser_Person.UserUPD_nm
		,	vwUser_Person.UserDEL_dm
		,	vwUser_Person.UserDEL_nm
		,	vwUser_Person.ParentUser_tp
		,	vwUser_Person.UserType_tx
		,	vwUser_Person.PersonADD_dm
		,	vwUser_Person.PersonADD_nm
		,	vwUser_Person.PersonUPD_dm
		,	vwUser_Person.PersonUPD_nm
		,	vwUser_Person.PersonDEL_dm
		,	vwUser_Person.PersonDEL_nm
		,	vwUser_Person.ParentPerson_tp
		,	vwUser_Person.PersonType_tx
		FROM
			vwUser_Person
		WHERE
			vwUser_Person.User_tp	= User_tp
		AND	vwUser_Person.User_nm	= User_nm
		AND	vwUser_Person.Person_tp	= Person_tp
		AND	vwUser_Person.Person_nm	= Person_nm
		AND	vwUser_Person.UserDEL_dm	IS NULL
		AND	vwUser_Person.PersonDEL_dm	IS NULL

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
			vwUser_Person.User_id
		,	vwUser_Person.User_tp
		,	vwUser_Person.User_nm
		,	vwUser_Person.Domain_nm
		,	vwUser_Person.Password_cd
		,	vwUser_Person.Email_tx
		,	vwUser_Person.Person_id
		,	vwUser_Person.Person_tp
		,	vwUser_Person.Person_nm
		,	vwUser_Person.First_nm
		,	vwUser_Person.Middle_nm
		,	vwUser_Person.Last_nm
		,	vwUser_Person.FirstSNDX_cd
		,	vwUser_Person.LastSNDX_cd
		,	vwUser_Person.Birth_dm
		,	vwUser_Person.Gender_cd
		,	vwUser_Person.User_tx
		,	vwUser_Person.Person_tx
		,	vwUser_Person.UserADD_dm
		,	vwUser_Person.UserADD_nm
		,	vwUser_Person.UserUPD_dm
		,	vwUser_Person.UserUPD_nm
		,	vwUser_Person.UserDEL_dm
		,	vwUser_Person.UserDEL_nm
		,	vwUser_Person.ParentUser_tp
		,	vwUser_Person.UserType_tx
		,	vwUser_Person.PersonADD_dm
		,	vwUser_Person.PersonADD_nm
		,	vwUser_Person.PersonUPD_dm
		,	vwUser_Person.PersonUPD_nm
		,	vwUser_Person.PersonDEL_dm
		,	vwUser_Person.PersonDEL_nm
		,	vwUser_Person.ParentPerson_tp
		,	vwUser_Person.PersonType_tx
		FROM
			vwUser_Person
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
			Person_id	= Person_id
		OR	Person_id	=  -2147483647
			)
		AND	(
			Person_tp	= Person_tp
		OR	Person_tp	= '-2147483647'
			)
		AND	(
			Person_nm	LIKE CONCAT('%', Person_nm, '%')
		OR	Person_nm	= '-2147483647'
			)
		AND	(
			First_nm	LIKE CONCAT('%', First_nm, '%')
		OR	First_nm	= '-2147483647'
			)
		AND	(
			Middle_nm	LIKE CONCAT('%', Middle_nm, '%')
		OR	Middle_nm	= '-2147483647'
			)
		AND	(
			Last_nm	LIKE CONCAT('%', Last_nm, '%')
		OR	Last_nm	= '-2147483647'
			)
		AND	(
			FirstSNDX_cd	LIKE CONCAT('%', FirstSNDX_cd, '%')
		OR	FirstSNDX_cd	= '-2147483647'
			)
		AND	(
			LastSNDX_cd	LIKE CONCAT('%', LastSNDX_cd, '%')
		OR	LastSNDX_cd	= '-2147483647'
			)
		AND	(
			Birth_dm	= Birth_dm
		OR	Birth_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			Gender_cd	LIKE CONCAT('%', Gender_cd, '%')
		OR	Gender_cd	= '-2147483647'
			)
		AND	(
			User_tx	LIKE CONCAT('%', User_tx, '%')
		OR	User_tx	LIKE '-2147483647'
			)
		AND	(
			Person_tx	LIKE CONCAT('%', Person_tx, '%')
		OR	Person_tx	LIKE '-2147483647'
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
			PersonADD_dm	= PersonADD_dm
		OR	PersonADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			PersonADD_nm	LIKE CONCAT('%', PersonADD_nm, '%')
		OR	PersonADD_nm	= '-2147483647'
			)
		AND	(
			PersonUPD_dm	= PersonUPD_dm
		OR	PersonUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			PersonUPD_nm	LIKE CONCAT('%', PersonUPD_nm, '%')
		OR	PersonUPD_nm	= '-2147483647'
			)
		AND	(
			PersonDEL_dm	= PersonDEL_dm
		OR	PersonDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			PersonDEL_nm	LIKE CONCAT('%', PersonDEL_nm, '%')
		OR	PersonDEL_nm	= '-2147483647'
			)
		AND	(
			ParentPerson_tp	= ParentPerson_tp
		OR	ParentPerson_tp	= '-2147483647'
			)
		AND	(
			PersonType_tx	LIKE CONCAT('%', PersonType_tx, '%')
		OR	PersonType_tx	LIKE '-2147483647'
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

