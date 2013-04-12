DROP PROCEDURE IF EXISTS	`gfpPerson`
;

DELIMITER //
CREATE PROCEDURE	gfpPerson
(
	Person_id		int signed		-- PK1 
,	Person_tp		varchar(80)		-- PK2 AK1
,	Person_nm		varchar(128)		--  AK2
,	First_nm		varchar(128)	
,	Middle_nm		varchar(128)	
,	Last_nm		varchar(128)	
,	FirstSNDX_cd		varchar(48)	
,	LastSNDX_cd		varchar(48)	
,	Birth_dm		datetime	
,	Gender_cd		varchar(48)	
,	Person_tx		mediumtext	
,	PersonADD_dm		datetime	
,	PersonADD_nm		varchar(128)	
,	PersonUPD_dm		datetime	
,	PersonUPD_nm		varchar(128)	
,	PersonDEL_dm		datetime	
,	PersonDEL_nm		varchar(128)	
,	ParentPerson_tp		varchar(80)	
,	PersonType_tx		mediumtext	
,	PersonTypeLeft_id		int signed	
,	PersonTypeRight_id		int signed	
,	PersonTypeLevel_id		int signed	
,	PersonTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpPerson
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwPerson
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwPerson';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpPerson';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
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
	IF Person_tx IS NULL OR Person_tx = '' THEN SET Person_tx = '-2147483647';	END IF;
	IF PersonADD_dm IS NULL OR PersonADD_dm = '' THEN SET PersonADD_dm = '0000-00-00 00:00:00';	END IF;
	IF PersonADD_nm IS NULL OR PersonADD_nm = '' THEN SET PersonADD_nm = '-2147483647';	END IF;
	IF PersonUPD_dm IS NULL OR PersonUPD_dm = '' THEN SET PersonUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF PersonUPD_nm IS NULL OR PersonUPD_nm = '' THEN SET PersonUPD_nm = '-2147483647';	END IF;
	IF PersonDEL_dm IS NULL OR PersonDEL_dm = '' THEN SET PersonDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF PersonDEL_nm IS NULL OR PersonDEL_nm = '' THEN SET PersonDEL_nm = '-2147483647';	END IF;
	IF ParentPerson_tp IS NULL OR ParentPerson_tp = '' THEN SET ParentPerson_tp = '-2147483647';	END IF;
	IF PersonType_tx IS NULL OR PersonType_tx = '' THEN SET PersonType_tx = '-2147483647';	END IF;
	IF PersonTypeLeft_id IS NULL OR PersonTypeLeft_id = 0 THEN SET PersonTypeLeft_id =  -2147483647;	END IF;
	IF PersonTypeRight_id IS NULL OR PersonTypeRight_id = 0 THEN SET PersonTypeRight_id =  -2147483647;	END IF;
	IF PersonTypeLevel_id IS NULL OR PersonTypeLevel_id = 0 THEN SET PersonTypeLevel_id =  -2147483647;	END IF;
	IF PersonTypeOrder_id IS NULL OR PersonTypeOrder_id = 0 THEN SET PersonTypeOrder_id =  -2147483647;	END IF;

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
			vwPerson.Person_id
		,	vwPerson.Person_tp
		,	vwPerson.Person_nm
		,	vwPerson.First_nm
		,	vwPerson.Middle_nm
		,	vwPerson.Last_nm
		,	vwPerson.FirstSNDX_cd
		,	vwPerson.LastSNDX_cd
		,	vwPerson.Birth_dm
		,	vwPerson.Gender_cd
		,	vwPerson.Person_tx
		,	vwPerson.PersonADD_dm
		,	vwPerson.PersonADD_nm
		,	vwPerson.PersonUPD_dm
		,	vwPerson.PersonUPD_nm
		,	vwPerson.PersonDEL_dm
		,	vwPerson.PersonDEL_nm
		,	vwPerson.ParentPerson_tp
		,	vwPerson.PersonType_tx
		,	vwPerson.PersonTypeLeft_id
		,	vwPerson.PersonTypeRight_id
		,	vwPerson.PersonTypeLevel_id
		,	vwPerson.PersonTypeOrder_id
		FROM
			vwPerson
		WHERE
			vwPerson.Person_id	= Person_id
		AND	vwPerson.Person_tp	= Person_tp
		AND	vwPerson.PersonDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwPerson.Person_id
		,	vwPerson.Person_tp
		,	vwPerson.Person_nm
		,	vwPerson.First_nm
		,	vwPerson.Middle_nm
		,	vwPerson.Last_nm
		,	vwPerson.FirstSNDX_cd
		,	vwPerson.LastSNDX_cd
		,	vwPerson.Birth_dm
		,	vwPerson.Gender_cd
		,	vwPerson.Person_tx
		,	vwPerson.PersonADD_dm
		,	vwPerson.PersonADD_nm
		,	vwPerson.PersonUPD_dm
		,	vwPerson.PersonUPD_nm
		,	vwPerson.PersonDEL_dm
		,	vwPerson.PersonDEL_nm
		,	vwPerson.ParentPerson_tp
		,	vwPerson.PersonType_tx
		,	vwPerson.PersonTypeLeft_id
		,	vwPerson.PersonTypeRight_id
		,	vwPerson.PersonTypeLevel_id
		,	vwPerson.PersonTypeOrder_id
		FROM
			vwPerson
		WHERE
			vwPerson.Person_tp	= Person_tp
		AND	vwPerson.Person_id	= Person_id
		AND	vwPerson.Person_tp	= Person_tp
		AND	vwPerson.PersonDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwPerson.Person_id
		,	vwPerson.Person_tp
		,	vwPerson.Person_nm
		,	vwPerson.First_nm
		,	vwPerson.Middle_nm
		,	vwPerson.Last_nm
		,	vwPerson.FirstSNDX_cd
		,	vwPerson.LastSNDX_cd
		,	vwPerson.Birth_dm
		,	vwPerson.Gender_cd
		,	vwPerson.Person_tx
		,	vwPerson.PersonADD_dm
		,	vwPerson.PersonADD_nm
		,	vwPerson.PersonUPD_dm
		,	vwPerson.PersonUPD_nm
		,	vwPerson.PersonDEL_dm
		,	vwPerson.PersonDEL_nm
		,	vwPerson.ParentPerson_tp
		,	vwPerson.PersonType_tx
		,	vwPerson.PersonTypeLeft_id
		,	vwPerson.PersonTypeRight_id
		,	vwPerson.PersonTypeLevel_id
		,	vwPerson.PersonTypeOrder_id
		FROM
			vwPerson
		WHERE
			vwPerson.Person_tp	= Person_tp
		AND	vwPerson.Person_tp	= Person_tp
		AND	vwPerson.PersonDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwPerson.Person_id
		,	vwPerson.Person_tp
		,	vwPerson.Person_nm
		,	vwPerson.First_nm
		,	vwPerson.Middle_nm
		,	vwPerson.Last_nm
		,	vwPerson.FirstSNDX_cd
		,	vwPerson.LastSNDX_cd
		,	vwPerson.Birth_dm
		,	vwPerson.Gender_cd
		,	vwPerson.Person_tx
		,	vwPerson.PersonADD_dm
		,	vwPerson.PersonADD_nm
		,	vwPerson.PersonUPD_dm
		,	vwPerson.PersonUPD_nm
		,	vwPerson.PersonDEL_dm
		,	vwPerson.PersonDEL_nm
		,	vwPerson.ParentPerson_tp
		,	vwPerson.PersonType_tx
		,	vwPerson.PersonTypeLeft_id
		,	vwPerson.PersonTypeRight_id
		,	vwPerson.PersonTypeLevel_id
		,	vwPerson.PersonTypeOrder_id
		FROM
			vwPerson
		WHERE
			vwPerson.Person_tp	= Person_tp
		AND	vwPerson.Person_nm	= Person_nm
		AND	vwPerson.PersonDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Search Key lookup
	#######################################################################
	IF	Key_cd = 'SK1'
	THEN
		SELECT
			vwPerson.Person_id
		,	vwPerson.Person_tp
		,	vwPerson.Person_nm
		,	vwPerson.First_nm
		,	vwPerson.Middle_nm
		,	vwPerson.Last_nm
		,	vwPerson.FirstSNDX_cd
		,	vwPerson.LastSNDX_cd
		,	vwPerson.Birth_dm
		,	vwPerson.Gender_cd
		,	vwPerson.Person_tx
		,	vwPerson.PersonADD_dm
		,	vwPerson.PersonADD_nm
		,	vwPerson.PersonUPD_dm
		,	vwPerson.PersonUPD_nm
		,	vwPerson.PersonDEL_dm
		,	vwPerson.PersonDEL_nm
		,	vwPerson.ParentPerson_tp
		,	vwPerson.PersonType_tx
		,	vwPerson.PersonTypeLeft_id
		,	vwPerson.PersonTypeRight_id
		,	vwPerson.PersonTypeLevel_id
		,	vwPerson.PersonTypeOrder_id
		FROM
			vwPerson
		WHERE
			vwPerson.Person_tp	= Person_tp
		AND	vwPerson.First_nm	= First_nm
		AND	vwPerson.Last_nm	= Last_nm
		AND	vwPerson.PersonDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Attribute lookup
	#######################################################################
	IF	Key_cd = 'AL'
	THEN
		SELECT
			vwPerson.Person_id
		,	vwPerson.Person_tp
		,	vwPerson.Person_nm
		,	vwPerson.First_nm
		,	vwPerson.Middle_nm
		,	vwPerson.Last_nm
		,	vwPerson.FirstSNDX_cd
		,	vwPerson.LastSNDX_cd
		,	vwPerson.Birth_dm
		,	vwPerson.Gender_cd
		,	vwPerson.Person_tx
		,	vwPerson.PersonADD_dm
		,	vwPerson.PersonADD_nm
		,	vwPerson.PersonUPD_dm
		,	vwPerson.PersonUPD_nm
		,	vwPerson.PersonDEL_dm
		,	vwPerson.PersonDEL_nm
		,	vwPerson.ParentPerson_tp
		,	vwPerson.PersonType_tx
		,	vwPerson.PersonTypeLeft_id
		,	vwPerson.PersonTypeRight_id
		,	vwPerson.PersonTypeLevel_id
		,	vwPerson.PersonTypeOrder_id
		FROM
			vwPerson
		WHERE
			(
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
			Person_tx	LIKE CONCAT('%', Person_tx, '%')
		OR	Person_tx	LIKE '-2147483647'
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
		AND	(
			PersonTypeLeft_id	= PersonTypeLeft_id
		OR	PersonTypeLeft_id	=  -2147483647
			)
		AND	(
			PersonTypeRight_id	= PersonTypeRight_id
		OR	PersonTypeRight_id	=  -2147483647
			)
		AND	(
			PersonTypeLevel_id	= PersonTypeLevel_id
		OR	PersonTypeLevel_id	=  -2147483647
			)
		AND	(
			PersonTypeOrder_id	= PersonTypeOrder_id
		OR	PersonTypeOrder_id	=  -2147483647
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

