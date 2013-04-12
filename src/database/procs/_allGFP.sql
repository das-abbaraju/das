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
DROP PROCEDURE IF EXISTS	`gfpPersonType`
;

DELIMITER //
CREATE PROCEDURE	gfpPersonType
(
	Person_tp		varchar(80)		-- PK1 
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
**	Name:		gfpPersonType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwPersonType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwPersonType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpPersonType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Person_tp IS NULL OR Person_tp = '' THEN SET Person_tp = '-2147483647';	END IF;
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
			vwPersonType.Person_tp
		,	vwPersonType.ParentPerson_tp
		,	vwPersonType.PersonType_tx
		,	vwPersonType.PersonTypeLeft_id
		,	vwPersonType.PersonTypeRight_id
		,	vwPersonType.PersonTypeLevel_id
		,	vwPersonType.PersonTypeOrder_id
		FROM
			vwPersonType
		WHERE
			vwPersonType.Person_tp	= Person_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwPersonType.Person_tp
		,	vwPersonType.ParentPerson_tp
		,	vwPersonType.PersonType_tx
		,	vwPersonType.PersonTypeLeft_id
		,	vwPersonType.PersonTypeRight_id
		,	vwPersonType.PersonTypeLevel_id
		,	vwPersonType.PersonTypeOrder_id
		FROM
			vwPersonType
		WHERE
			vwPersonType.Person_tp	= Person_tp

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
			vwPersonType.Person_tp
		,	vwPersonType.ParentPerson_tp
		,	vwPersonType.PersonType_tx
		,	vwPersonType.PersonTypeLeft_id
		,	vwPersonType.PersonTypeRight_id
		,	vwPersonType.PersonTypeLevel_id
		,	vwPersonType.PersonTypeOrder_id
		FROM
			vwPersonType
		WHERE
			(
			Person_tp	= Person_tp
		OR	Person_tp	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`gfpReport`
;

DELIMITER //
CREATE PROCEDURE	gfpReport
(
	Report_id		int signed		-- PK1 
,	Report_tp		varchar(80)		-- PK2 AK1
,	Report_nm		varchar(128)		--  AK2
,	Report_cd		varchar(48)	
,	Report_tx		mediumtext	
,	ReportADD_dm		datetime	
,	ReportADD_nm		varchar(128)	
,	ReportUPD_dm		datetime	
,	ReportUPD_nm		varchar(128)	
,	ReportDEL_dm		datetime	
,	ReportDEL_nm		varchar(128)	
,	ParentReport_tp		varchar(80)	
,	ReportType_tx		mediumtext	
,	ReportTypeLeft_id		int signed	
,	ReportTypeRight_id		int signed	
,	ReportTypeLevel_id		int signed	
,	ReportTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpReport
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwReport
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwReport';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpReport';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Report_id IS NULL OR Report_id = 0 THEN SET Report_id =  -2147483647;	END IF;
	IF Report_tp IS NULL OR Report_tp = '' THEN SET Report_tp = '-2147483647';	END IF;
	IF Report_nm IS NULL OR Report_nm = '' THEN SET Report_nm = '-2147483647';	END IF;
	IF Report_cd IS NULL OR Report_cd = '' THEN SET Report_cd = '-2147483647';	END IF;
	IF Report_tx IS NULL OR Report_tx = '' THEN SET Report_tx = '-2147483647';	END IF;
	IF ReportADD_dm IS NULL OR ReportADD_dm = '' THEN SET ReportADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ReportADD_nm IS NULL OR ReportADD_nm = '' THEN SET ReportADD_nm = '-2147483647';	END IF;
	IF ReportUPD_dm IS NULL OR ReportUPD_dm = '' THEN SET ReportUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ReportUPD_nm IS NULL OR ReportUPD_nm = '' THEN SET ReportUPD_nm = '-2147483647';	END IF;
	IF ReportDEL_dm IS NULL OR ReportDEL_dm = '' THEN SET ReportDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ReportDEL_nm IS NULL OR ReportDEL_nm = '' THEN SET ReportDEL_nm = '-2147483647';	END IF;
	IF ParentReport_tp IS NULL OR ParentReport_tp = '' THEN SET ParentReport_tp = '-2147483647';	END IF;
	IF ReportType_tx IS NULL OR ReportType_tx = '' THEN SET ReportType_tx = '-2147483647';	END IF;
	IF ReportTypeLeft_id IS NULL OR ReportTypeLeft_id = 0 THEN SET ReportTypeLeft_id =  -2147483647;	END IF;
	IF ReportTypeRight_id IS NULL OR ReportTypeRight_id = 0 THEN SET ReportTypeRight_id =  -2147483647;	END IF;
	IF ReportTypeLevel_id IS NULL OR ReportTypeLevel_id = 0 THEN SET ReportTypeLevel_id =  -2147483647;	END IF;
	IF ReportTypeOrder_id IS NULL OR ReportTypeOrder_id = 0 THEN SET ReportTypeOrder_id =  -2147483647;	END IF;

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
			vwReport.Report_id
		,	vwReport.Report_tp
		,	vwReport.Report_nm
		,	vwReport.Report_cd
		,	vwReport.Report_tx
		,	vwReport.ReportADD_dm
		,	vwReport.ReportADD_nm
		,	vwReport.ReportUPD_dm
		,	vwReport.ReportUPD_nm
		,	vwReport.ReportDEL_dm
		,	vwReport.ReportDEL_nm
		,	vwReport.ParentReport_tp
		,	vwReport.ReportType_tx
		,	vwReport.ReportTypeLeft_id
		,	vwReport.ReportTypeRight_id
		,	vwReport.ReportTypeLevel_id
		,	vwReport.ReportTypeOrder_id
		FROM
			vwReport
		WHERE
			vwReport.Report_id	= Report_id
		AND	vwReport.Report_tp	= Report_tp
		AND	vwReport.ReportDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwReport.Report_id
		,	vwReport.Report_tp
		,	vwReport.Report_nm
		,	vwReport.Report_cd
		,	vwReport.Report_tx
		,	vwReport.ReportADD_dm
		,	vwReport.ReportADD_nm
		,	vwReport.ReportUPD_dm
		,	vwReport.ReportUPD_nm
		,	vwReport.ReportDEL_dm
		,	vwReport.ReportDEL_nm
		,	vwReport.ParentReport_tp
		,	vwReport.ReportType_tx
		,	vwReport.ReportTypeLeft_id
		,	vwReport.ReportTypeRight_id
		,	vwReport.ReportTypeLevel_id
		,	vwReport.ReportTypeOrder_id
		FROM
			vwReport
		WHERE
			vwReport.Report_id	= Report_id
		AND	vwReport.ReportDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwReport.Report_id
		,	vwReport.Report_tp
		,	vwReport.Report_nm
		,	vwReport.Report_cd
		,	vwReport.Report_tx
		,	vwReport.ReportADD_dm
		,	vwReport.ReportADD_nm
		,	vwReport.ReportUPD_dm
		,	vwReport.ReportUPD_nm
		,	vwReport.ReportDEL_dm
		,	vwReport.ReportDEL_nm
		,	vwReport.ParentReport_tp
		,	vwReport.ReportType_tx
		,	vwReport.ReportTypeLeft_id
		,	vwReport.ReportTypeRight_id
		,	vwReport.ReportTypeLevel_id
		,	vwReport.ReportTypeOrder_id
		FROM
			vwReport
		WHERE
			vwReport.Report_tp	= Report_tp
		AND	vwReport.Report_nm	= Report_nm
		AND	vwReport.ReportDEL_dm	IS NULL

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
			vwReport.Report_id
		,	vwReport.Report_tp
		,	vwReport.Report_nm
		,	vwReport.Report_cd
		,	vwReport.Report_tx
		,	vwReport.ReportADD_dm
		,	vwReport.ReportADD_nm
		,	vwReport.ReportUPD_dm
		,	vwReport.ReportUPD_nm
		,	vwReport.ReportDEL_dm
		,	vwReport.ReportDEL_nm
		,	vwReport.ParentReport_tp
		,	vwReport.ReportType_tx
		,	vwReport.ReportTypeLeft_id
		,	vwReport.ReportTypeRight_id
		,	vwReport.ReportTypeLevel_id
		,	vwReport.ReportTypeOrder_id
		FROM
			vwReport
		WHERE
			(
			Report_id	= Report_id
		OR	Report_id	=  -2147483647
			)
		AND	(
			Report_tp	= Report_tp
		OR	Report_tp	= '-2147483647'
			)
		AND	(
			Report_nm	LIKE CONCAT('%', Report_nm, '%')
		OR	Report_nm	= '-2147483647'
			)
		AND	(
			Report_cd	LIKE CONCAT('%', Report_cd, '%')
		OR	Report_cd	= '-2147483647'
			)
		AND	(
			Report_tx	LIKE CONCAT('%', Report_tx, '%')
		OR	Report_tx	LIKE '-2147483647'
			)
		AND	(
			ReportADD_dm	= ReportADD_dm
		OR	ReportADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			ReportADD_nm	LIKE CONCAT('%', ReportADD_nm, '%')
		OR	ReportADD_nm	= '-2147483647'
			)
		AND	(
			ReportUPD_dm	= ReportUPD_dm
		OR	ReportUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			ReportUPD_nm	LIKE CONCAT('%', ReportUPD_nm, '%')
		OR	ReportUPD_nm	= '-2147483647'
			)
		AND	(
			ReportDEL_dm	= ReportDEL_dm
		OR	ReportDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			ReportDEL_nm	LIKE CONCAT('%', ReportDEL_nm, '%')
		OR	ReportDEL_nm	= '-2147483647'
			)
		AND	(
			ParentReport_tp	= ParentReport_tp
		OR	ParentReport_tp	= '-2147483647'
			)
		AND	(
			ReportType_tx	LIKE CONCAT('%', ReportType_tx, '%')
		OR	ReportType_tx	LIKE '-2147483647'
			)
		AND	(
			ReportTypeLeft_id	= ReportTypeLeft_id
		OR	ReportTypeLeft_id	=  -2147483647
			)
		AND	(
			ReportTypeRight_id	= ReportTypeRight_id
		OR	ReportTypeRight_id	=  -2147483647
			)
		AND	(
			ReportTypeLevel_id	= ReportTypeLevel_id
		OR	ReportTypeLevel_id	=  -2147483647
			)
		AND	(
			ReportTypeOrder_id	= ReportTypeOrder_id
		OR	ReportTypeOrder_id	=  -2147483647
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
DROP PROCEDURE IF EXISTS	`gfpReportType`
;

DELIMITER //
CREATE PROCEDURE	gfpReportType
(
	Report_tp		varchar(80)		-- PK1 
,	ParentReport_tp		varchar(80)	
,	ReportType_tx		mediumtext	
,	ReportTypeLeft_id		int signed	
,	ReportTypeRight_id		int signed	
,	ReportTypeLevel_id		int signed	
,	ReportTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpReportType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwReportType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwReportType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpReportType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Report_tp IS NULL OR Report_tp = '' THEN SET Report_tp = '-2147483647';	END IF;
	IF ParentReport_tp IS NULL OR ParentReport_tp = '' THEN SET ParentReport_tp = '-2147483647';	END IF;
	IF ReportType_tx IS NULL OR ReportType_tx = '' THEN SET ReportType_tx = '-2147483647';	END IF;
	IF ReportTypeLeft_id IS NULL OR ReportTypeLeft_id = 0 THEN SET ReportTypeLeft_id =  -2147483647;	END IF;
	IF ReportTypeRight_id IS NULL OR ReportTypeRight_id = 0 THEN SET ReportTypeRight_id =  -2147483647;	END IF;
	IF ReportTypeLevel_id IS NULL OR ReportTypeLevel_id = 0 THEN SET ReportTypeLevel_id =  -2147483647;	END IF;
	IF ReportTypeOrder_id IS NULL OR ReportTypeOrder_id = 0 THEN SET ReportTypeOrder_id =  -2147483647;	END IF;

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
			vwReportType.Report_tp
		,	vwReportType.ParentReport_tp
		,	vwReportType.ReportType_tx
		,	vwReportType.ReportTypeLeft_id
		,	vwReportType.ReportTypeRight_id
		,	vwReportType.ReportTypeLevel_id
		,	vwReportType.ReportTypeOrder_id
		FROM
			vwReportType
		WHERE
			vwReportType.Report_tp	= Report_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwReportType.Report_tp
		,	vwReportType.ParentReport_tp
		,	vwReportType.ReportType_tx
		,	vwReportType.ReportTypeLeft_id
		,	vwReportType.ReportTypeRight_id
		,	vwReportType.ReportTypeLevel_id
		,	vwReportType.ReportTypeOrder_id
		FROM
			vwReportType
		WHERE
			vwReportType.Report_tp	= Report_tp

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
			vwReportType.Report_tp
		,	vwReportType.ParentReport_tp
		,	vwReportType.ReportType_tx
		,	vwReportType.ReportTypeLeft_id
		,	vwReportType.ReportTypeRight_id
		,	vwReportType.ReportTypeLevel_id
		,	vwReportType.ReportTypeOrder_id
		FROM
			vwReportType
		WHERE
			(
			Report_tp	= Report_tp
		OR	Report_tp	= '-2147483647'
			)
		AND	(
			ParentReport_tp	= ParentReport_tp
		OR	ParentReport_tp	= '-2147483647'
			)
		AND	(
			ReportType_tx	LIKE CONCAT('%', ReportType_tx, '%')
		OR	ReportType_tx	LIKE '-2147483647'
			)
		AND	(
			ReportTypeLeft_id	= ReportTypeLeft_id
		OR	ReportTypeLeft_id	=  -2147483647
			)
		AND	(
			ReportTypeRight_id	= ReportTypeRight_id
		OR	ReportTypeRight_id	=  -2147483647
			)
		AND	(
			ReportTypeLevel_id	= ReportTypeLevel_id
		OR	ReportTypeLevel_id	=  -2147483647
			)
		AND	(
			ReportTypeOrder_id	= ReportTypeOrder_id
		OR	ReportTypeOrder_id	=  -2147483647
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
DROP PROCEDURE IF EXISTS	`gfpResource`
;

DELIMITER //
CREATE PROCEDURE	gfpResource
(
	Resrc_id		int signed		-- PK1 
,	Resrc_tp		varchar(80)		-- PK2 AK1
,	Resrc_nm		varchar(128)		--  AK2
,	Resrc_tx		mediumtext	
,	ADD_dm		datetime	
,	ADD_nm		varchar(128)	
,	UPD_dm		datetime	
,	UPD_nm		varchar(128)	
,	DEL_dm		datetime	
,	DEL_nm		varchar(128)	
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
**	Name:		gfpResource
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwResource
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwResource';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpResource';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Resrc_id IS NULL OR Resrc_id = 0 THEN SET Resrc_id =  -2147483647;	END IF;
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF Resrc_nm IS NULL OR Resrc_nm = '' THEN SET Resrc_nm = '-2147483647';	END IF;
	IF Resrc_tx IS NULL OR Resrc_tx = '' THEN SET Resrc_tx = '-2147483647';	END IF;
	IF ADD_dm IS NULL OR ADD_dm = '' THEN SET ADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ADD_nm IS NULL OR ADD_nm = '' THEN SET ADD_nm = '-2147483647';	END IF;
	IF UPD_dm IS NULL OR UPD_dm = '' THEN SET UPD_dm = '0000-00-00 00:00:00';	END IF;
	IF UPD_nm IS NULL OR UPD_nm = '' THEN SET UPD_nm = '-2147483647';	END IF;
	IF DEL_dm IS NULL OR DEL_dm = '' THEN SET DEL_dm = '0000-00-00 00:00:00';	END IF;
	IF DEL_nm IS NULL OR DEL_nm = '' THEN SET DEL_nm = '-2147483647';	END IF;
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
			vwResource.Resrc_id
		,	vwResource.Resrc_tp
		,	vwResource.Resrc_nm
		,	vwResource.Resrc_tx
		,	vwResource.ADD_dm
		,	vwResource.ADD_nm
		,	vwResource.UPD_dm
		,	vwResource.UPD_nm
		,	vwResource.DEL_dm
		,	vwResource.DEL_nm
		,	vwResource.ParentResrc_tp
		,	vwResource.ResrcType_tx
		,	vwResource.Left_id
		,	vwResource.Right_id
		,	vwResource.Level_id
		,	vwResource.Order_id
		FROM
			vwResource
		WHERE
			vwResource.Resrc_id	= Resrc_id
		AND	vwResource.Resrc_tp	= Resrc_tp
		AND	vwResource.DEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwResource.Resrc_id
		,	vwResource.Resrc_tp
		,	vwResource.Resrc_nm
		,	vwResource.Resrc_tx
		,	vwResource.ADD_dm
		,	vwResource.ADD_nm
		,	vwResource.UPD_dm
		,	vwResource.UPD_nm
		,	vwResource.DEL_dm
		,	vwResource.DEL_nm
		,	vwResource.ParentResrc_tp
		,	vwResource.ResrcType_tx
		,	vwResource.Left_id
		,	vwResource.Right_id
		,	vwResource.Level_id
		,	vwResource.Order_id
		FROM
			vwResource
		WHERE
			vwResource.Resrc_tp	= Resrc_tp
		AND	vwResource.DEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwResource.Resrc_id
		,	vwResource.Resrc_tp
		,	vwResource.Resrc_nm
		,	vwResource.Resrc_tx
		,	vwResource.ADD_dm
		,	vwResource.ADD_nm
		,	vwResource.UPD_dm
		,	vwResource.UPD_nm
		,	vwResource.DEL_dm
		,	vwResource.DEL_nm
		,	vwResource.ParentResrc_tp
		,	vwResource.ResrcType_tx
		,	vwResource.Left_id
		,	vwResource.Right_id
		,	vwResource.Level_id
		,	vwResource.Order_id
		FROM
			vwResource
		WHERE
			vwResource.Resrc_tp	= Resrc_tp
		AND	vwResource.Resrc_nm	= Resrc_nm
		AND	vwResource.DEL_dm	IS NULL

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
			vwResource.Resrc_id
		,	vwResource.Resrc_tp
		,	vwResource.Resrc_nm
		,	vwResource.Resrc_tx
		,	vwResource.ADD_dm
		,	vwResource.ADD_nm
		,	vwResource.UPD_dm
		,	vwResource.UPD_nm
		,	vwResource.DEL_dm
		,	vwResource.DEL_nm
		,	vwResource.ParentResrc_tp
		,	vwResource.ResrcType_tx
		,	vwResource.Left_id
		,	vwResource.Right_id
		,	vwResource.Level_id
		,	vwResource.Order_id
		FROM
			vwResource
		WHERE
			(
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
			Resrc_tx	LIKE CONCAT('%', Resrc_tx, '%')
		OR	Resrc_tx	LIKE '-2147483647'
			)
		AND	(
			ADD_dm	= ADD_dm
		OR	ADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			ADD_nm	LIKE CONCAT('%', ADD_nm, '%')
		OR	ADD_nm	= '-2147483647'
			)
		AND	(
			UPD_dm	= UPD_dm
		OR	UPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			UPD_nm	LIKE CONCAT('%', UPD_nm, '%')
		OR	UPD_nm	= '-2147483647'
			)
		AND	(
			DEL_dm	= DEL_dm
		OR	DEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			DEL_nm	LIKE CONCAT('%', DEL_nm, '%')
		OR	DEL_nm	= '-2147483647'
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
			Left_id	= Left_id
		OR	Left_id	=  -2147483647
			)
		AND	(
			Right_id	= Right_id
		OR	Right_id	=  -2147483647
			)
		AND	(
			Level_id	= Level_id
		OR	Level_id	=  -2147483647
			)
		AND	(
			Order_id	= Order_id
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
			Resrc_id	= Resrc_id
		OR	Resrc_id	=  -2147483647
			)
		AND	(
			Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
			)
		AND	(
			Right_tp	= Right_tp
		OR	Right_tp	= '-2147483647'
			)
		AND	(
			Resrc_nm	LIKE CONCAT('%', Resrc_nm, '%')
		OR	Resrc_nm	= '-2147483647'
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
			ADD_dm	= ADD_dm
		OR	ADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			ADD_nm	LIKE CONCAT('%', ADD_nm, '%')
		OR	ADD_nm	= '-2147483647'
			)
		AND	(
			UPD_dm	= UPD_dm
		OR	UPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			UPD_nm	LIKE CONCAT('%', UPD_nm, '%')
		OR	UPD_nm	= '-2147483647'
			)
		AND	(
			DEL_dm	= DEL_dm
		OR	DEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			DEL_nm	LIKE CONCAT('%', DEL_nm, '%')
		OR	DEL_nm	= '-2147483647'
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
			Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
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
			Left_id	= Left_id
		OR	Left_id	=  -2147483647
			)
		AND	(
			Right_id	= Right_id
		OR	Right_id	=  -2147483647
			)
		AND	(
			Level_id	= Level_id
		OR	Level_id	=  -2147483647
			)
		AND	(
			Order_id	= Order_id
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
DROP PROCEDURE IF EXISTS	`gfpRightType`
;

DELIMITER //
CREATE PROCEDURE	gfpRightType
(
	Right_tp		varchar(80)		-- PK1 
,	ParentRight_tp		varchar(80)	
,	RightType_tx		mediumtext	
,	RightTypeLeft_id		int signed	
,	RightTypeRight_id		int signed	
,	RightTypeLevel_id		int signed	
,	RightTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpRightType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwRightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwRightType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpRightType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Right_tp IS NULL OR Right_tp = '' THEN SET Right_tp = '-2147483647';	END IF;
	IF ParentRight_tp IS NULL OR ParentRight_tp = '' THEN SET ParentRight_tp = '-2147483647';	END IF;
	IF RightType_tx IS NULL OR RightType_tx = '' THEN SET RightType_tx = '-2147483647';	END IF;
	IF RightTypeLeft_id IS NULL OR RightTypeLeft_id = 0 THEN SET RightTypeLeft_id =  -2147483647;	END IF;
	IF RightTypeRight_id IS NULL OR RightTypeRight_id = 0 THEN SET RightTypeRight_id =  -2147483647;	END IF;
	IF RightTypeLevel_id IS NULL OR RightTypeLevel_id = 0 THEN SET RightTypeLevel_id =  -2147483647;	END IF;
	IF RightTypeOrder_id IS NULL OR RightTypeOrder_id = 0 THEN SET RightTypeOrder_id =  -2147483647;	END IF;

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
			vwRightType.Right_tp
		,	vwRightType.ParentRight_tp
		,	vwRightType.RightType_tx
		,	vwRightType.RightTypeLeft_id
		,	vwRightType.RightTypeRight_id
		,	vwRightType.RightTypeLevel_id
		,	vwRightType.RightTypeOrder_id
		FROM
			vwRightType
		WHERE
			vwRightType.Right_tp	= Right_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwRightType.Right_tp
		,	vwRightType.ParentRight_tp
		,	vwRightType.RightType_tx
		,	vwRightType.RightTypeLeft_id
		,	vwRightType.RightTypeRight_id
		,	vwRightType.RightTypeLevel_id
		,	vwRightType.RightTypeOrder_id
		FROM
			vwRightType
		WHERE
			vwRightType.Right_tp	= Right_tp

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
			vwRightType.Right_tp
		,	vwRightType.ParentRight_tp
		,	vwRightType.RightType_tx
		,	vwRightType.RightTypeLeft_id
		,	vwRightType.RightTypeRight_id
		,	vwRightType.RightTypeLevel_id
		,	vwRightType.RightTypeOrder_id
		FROM
			vwRightType
		WHERE
			(
			Right_tp	= Right_tp
		OR	Right_tp	= '-2147483647'
			)
		AND	(
			ParentRight_tp	= ParentRight_tp
		OR	ParentRight_tp	= '-2147483647'
			)
		AND	(
			RightType_tx	LIKE CONCAT('%', RightType_tx, '%')
		OR	RightType_tx	LIKE '-2147483647'
			)
		AND	(
			RightTypeLeft_id	= RightTypeLeft_id
		OR	RightTypeLeft_id	=  -2147483647
			)
		AND	(
			RightTypeRight_id	= RightTypeRight_id
		OR	RightTypeRight_id	=  -2147483647
			)
		AND	(
			RightTypeLevel_id	= RightTypeLevel_id
		OR	RightTypeLevel_id	=  -2147483647
			)
		AND	(
			RightTypeOrder_id	= RightTypeOrder_id
		OR	RightTypeOrder_id	=  -2147483647
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
			Role_tx	LIKE CONCAT('%', Role_tx, '%')
		OR	Role_tx	LIKE '-2147483647'
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
		AND	(
			RoleTypeLeft_id	= RoleTypeLeft_id
		OR	RoleTypeLeft_id	=  -2147483647
			)
		AND	(
			RoleTypeRight_id	= RoleTypeRight_id
		OR	RoleTypeRight_id	=  -2147483647
			)
		AND	(
			RoleTypeLevel_id	= RoleTypeLevel_id
		OR	RoleTypeLevel_id	=  -2147483647
			)
		AND	(
			RoleTypeOrder_id	= RoleTypeOrder_id
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
DROP PROCEDURE IF EXISTS	`gfpRole_ResourceType_RightType`
;

DELIMITER //
CREATE PROCEDURE	gfpRole_ResourceType_RightType
(
	Role_id		int signed		-- PK1 
,	Role_tp		varchar(80)		-- PK2 AK2
,	Role_nm		varchar(128)		--  AK1
,	Role_cd		varchar(48)	
,	Resrc_tp		varchar(80)		-- PK3 AK3
,	Right_tp		varchar(80)		-- PK4 AK4
,	Role_tx		mediumtext	
,	ParentResrc_tp		varchar(80)	
,	ResrcType_tx		mediumtext	
,	ParentRight_tp		varchar(80)	
,	RightType_tx		mediumtext	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpRole_ResourceType_RightType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwRole_ResourceType_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwRole_ResourceType_RightType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpRole_ResourceType_RightType';
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
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF Right_tp IS NULL OR Right_tp = '' THEN SET Right_tp = '-2147483647';	END IF;
	IF Role_tx IS NULL OR Role_tx = '' THEN SET Role_tx = '-2147483647';	END IF;
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
			vwRole_ResourceType_RightType.Role_id
		,	vwRole_ResourceType_RightType.Role_tp
		,	vwRole_ResourceType_RightType.Role_nm
		,	vwRole_ResourceType_RightType.Role_cd
		,	vwRole_ResourceType_RightType.Resrc_tp
		,	vwRole_ResourceType_RightType.Right_tp
		,	vwRole_ResourceType_RightType.Role_tx
		,	vwRole_ResourceType_RightType.ParentResrc_tp
		,	vwRole_ResourceType_RightType.ResrcType_tx
		,	vwRole_ResourceType_RightType.ParentRight_tp
		,	vwRole_ResourceType_RightType.RightType_tx
		FROM
			vwRole_ResourceType_RightType
		WHERE
			vwRole_ResourceType_RightType.Role_id	= Role_id
		AND	vwRole_ResourceType_RightType.Role_tp	= Role_tp
		AND	vwRole_ResourceType_RightType.Resrc_tp	= Resrc_tp
		AND	vwRole_ResourceType_RightType.Right_tp	= Right_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwRole_ResourceType_RightType.Role_id
		,	vwRole_ResourceType_RightType.Role_tp
		,	vwRole_ResourceType_RightType.Role_nm
		,	vwRole_ResourceType_RightType.Role_cd
		,	vwRole_ResourceType_RightType.Resrc_tp
		,	vwRole_ResourceType_RightType.Right_tp
		,	vwRole_ResourceType_RightType.Role_tx
		,	vwRole_ResourceType_RightType.ParentResrc_tp
		,	vwRole_ResourceType_RightType.ResrcType_tx
		,	vwRole_ResourceType_RightType.ParentRight_tp
		,	vwRole_ResourceType_RightType.RightType_tx
		FROM
			vwRole_ResourceType_RightType
		WHERE
			vwRole_ResourceType_RightType.Role_id	= Role_id
		AND	vwRole_ResourceType_RightType.Role_tp	= Role_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwRole_ResourceType_RightType.Role_id
		,	vwRole_ResourceType_RightType.Role_tp
		,	vwRole_ResourceType_RightType.Role_nm
		,	vwRole_ResourceType_RightType.Role_cd
		,	vwRole_ResourceType_RightType.Resrc_tp
		,	vwRole_ResourceType_RightType.Right_tp
		,	vwRole_ResourceType_RightType.Role_tx
		,	vwRole_ResourceType_RightType.ParentResrc_tp
		,	vwRole_ResourceType_RightType.ResrcType_tx
		,	vwRole_ResourceType_RightType.ParentRight_tp
		,	vwRole_ResourceType_RightType.RightType_tx
		FROM
			vwRole_ResourceType_RightType
		WHERE
			vwRole_ResourceType_RightType.Resrc_tp	= Resrc_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK3'
	THEN
		SELECT
			vwRole_ResourceType_RightType.Role_id
		,	vwRole_ResourceType_RightType.Role_tp
		,	vwRole_ResourceType_RightType.Role_nm
		,	vwRole_ResourceType_RightType.Role_cd
		,	vwRole_ResourceType_RightType.Resrc_tp
		,	vwRole_ResourceType_RightType.Right_tp
		,	vwRole_ResourceType_RightType.Role_tx
		,	vwRole_ResourceType_RightType.ParentResrc_tp
		,	vwRole_ResourceType_RightType.ResrcType_tx
		,	vwRole_ResourceType_RightType.ParentRight_tp
		,	vwRole_ResourceType_RightType.RightType_tx
		FROM
			vwRole_ResourceType_RightType
		WHERE
			vwRole_ResourceType_RightType.Resrc_tp	= Resrc_tp
		AND	vwRole_ResourceType_RightType.Right_tp	= Right_tp

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwRole_ResourceType_RightType.Role_id
		,	vwRole_ResourceType_RightType.Role_tp
		,	vwRole_ResourceType_RightType.Role_nm
		,	vwRole_ResourceType_RightType.Role_cd
		,	vwRole_ResourceType_RightType.Resrc_tp
		,	vwRole_ResourceType_RightType.Right_tp
		,	vwRole_ResourceType_RightType.Role_tx
		,	vwRole_ResourceType_RightType.ParentResrc_tp
		,	vwRole_ResourceType_RightType.ResrcType_tx
		,	vwRole_ResourceType_RightType.ParentRight_tp
		,	vwRole_ResourceType_RightType.RightType_tx
		FROM
			vwRole_ResourceType_RightType
		WHERE
			vwRole_ResourceType_RightType.Role_tp	= Role_tp
		AND	vwRole_ResourceType_RightType.Role_nm	= Role_nm
		AND	vwRole_ResourceType_RightType.Resrc_tp	= Resrc_tp
		AND	vwRole_ResourceType_RightType.Right_tp	= Right_tp

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
			vwRole_ResourceType_RightType.Role_id
		,	vwRole_ResourceType_RightType.Role_tp
		,	vwRole_ResourceType_RightType.Role_nm
		,	vwRole_ResourceType_RightType.Role_cd
		,	vwRole_ResourceType_RightType.Resrc_tp
		,	vwRole_ResourceType_RightType.Right_tp
		,	vwRole_ResourceType_RightType.Role_tx
		,	vwRole_ResourceType_RightType.ParentResrc_tp
		,	vwRole_ResourceType_RightType.ResrcType_tx
		,	vwRole_ResourceType_RightType.ParentRight_tp
		,	vwRole_ResourceType_RightType.RightType_tx
		FROM
			vwRole_ResourceType_RightType
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
			Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
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
			Role_tp	= Role_tp
		OR	Role_tp	= '-2147483647'
			)
		AND	(
			ParentRole_tp	= ParentRole_tp
		OR	ParentRole_tp	= '-2147483647'
			)
		AND	(
			RoleType_tx	LIKE CONCAT('%', RoleType_tx, '%')
		OR	RoleType_tx	LIKE '-2147483647'
			)
		AND	(
			RoleTypeLeft_id	= RoleTypeLeft_id
		OR	RoleTypeLeft_id	=  -2147483647
			)
		AND	(
			RoleTypeRight_id	= RoleTypeRight_id
		OR	RoleTypeRight_id	=  -2147483647
			)
		AND	(
			RoleTypeLevel_id	= RoleTypeLevel_id
		OR	RoleTypeLevel_id	=  -2147483647
			)
		AND	(
			RoleTypeOrder_id	= RoleTypeOrder_id
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
DROP PROCEDURE IF EXISTS	`gfpUserType`
;

DELIMITER //
CREATE PROCEDURE	gfpUserType
(
	User_tp		varchar(80)		-- PK1 
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
**	Name:		gfpUserType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwUserType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwUserType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpUserType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF User_tp IS NULL OR User_tp = '' THEN SET User_tp = '-2147483647';	END IF;
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
			vwUserType.User_tp
		,	vwUserType.ParentUser_tp
		,	vwUserType.UserType_tx
		,	vwUserType.UserTypeLeft_id
		,	vwUserType.UserTypeRight_id
		,	vwUserType.UserTypeLevel_id
		,	vwUserType.UserTypeOrder_id
		FROM
			vwUserType
		WHERE
			vwUserType.User_tp	= User_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwUserType.User_tp
		,	vwUserType.ParentUser_tp
		,	vwUserType.UserType_tx
		,	vwUserType.UserTypeLeft_id
		,	vwUserType.UserTypeRight_id
		,	vwUserType.UserTypeLevel_id
		,	vwUserType.UserTypeOrder_id
		FROM
			vwUserType
		WHERE
			vwUserType.User_tp	= User_tp

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
			vwUserType.User_tp
		,	vwUserType.ParentUser_tp
		,	vwUserType.UserType_tx
		,	vwUserType.UserTypeLeft_id
		,	vwUserType.UserTypeRight_id
		,	vwUserType.UserTypeLevel_id
		,	vwUserType.UserTypeOrder_id
		FROM
			vwUserType
		WHERE
			(
			User_tp	= User_tp
		OR	User_tp	= '-2147483647'
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

