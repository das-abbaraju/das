DROP PROCEDURE IF EXISTS	`gfpRole_Report_RightType`
;

DELIMITER //
CREATE PROCEDURE	gfpRole_Report_RightType
(
	ReportPermissionAccount_id	INT SIGNED
,	Role_id		INT SIGNED		-- PK1 
,	Role_tp		VARCHAR(80)		-- PK2 AK1
,	Role_nm		VARCHAR(128)		--  AK2
,	Role_cd		VARCHAR(48)	
,	Role_tx TEXT

,	Report_id		INT SIGNED		-- PK1 
,	Report_tp		VARCHAR(80)		-- PK2 AK1
,	Report_nm		VARCHAR(128)		--  AK2
,	Report_cd		VARCHAR(48)	
,	Report_tx		MEDIUMTEXT	

,	ReportOwner_id		INT SIGNED
,	ReportIsDeleted_fg	TINYINT
,	ReportParameters_tx	TEXT
,	ReportFilter_tx		VARCHAR(100)
,	ReportIsPrivate_fg	TINYINT

,	ReportADD_dm		DATETIME	
,	ReportADD_nm		VARCHAR(128)	
,	ReportUPD_dm		DATETIME	
,	ReportUPD_nm		VARCHAR(128)	
,	ReportDEL_dm		DATETIME	
,	ReportDEL_nm		VARCHAR(128)	
,	ParentReport_tp		VARCHAR(80)	
,	ReportType_tx		MEDIUMTEXT	
,	ReportTypeLeft_id		INT SIGNED	
,	ReportTypeRight_id		INT SIGNED	
,	ReportTypeLevel_id		INT SIGNED	
,	ReportTypeOrder_id		INT SIGNED	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpRole_Report_RightType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwRole_Report_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/11/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwRole_Report_RightType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpRole_Report_RightType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;

	IF ReportPermissionAccount_id IS NULL OR ReportPermissionAccount_id = 0 THEN SET ReportPermissionAccount_id =  -2147483647;	END IF;
	IF Role_id IS NULL OR Role_id = 0 THEN SET Role_id =  -2147483647;	END IF;
	IF Role_tp IS NULL OR Role_tp = '' THEN SET Role_tp = '-2147483647';	END IF;
	IF Role_nm IS NULL OR Role_nm = '' THEN SET Role_nm = '-2147483647';	END IF;
	IF Role_cd IS NULL OR Role_cd = '' THEN SET Role_cd = '-2147483647';	END IF;
	IF Role_tx IS NULL OR Role_tx = '' THEN SET Role_tx = '-2147483647';	END IF;

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
	IF ReportOwner_id IS NULL OR ReportOwner_id = 0 THEN SET ReportOwner_id =  -2147483647;	END IF;
	IF ReportIsDeleted_fg IS NULL THEN SET ReportIsDeleted_fg =  -1;	END IF;
	IF ReportParameters_tx IS NULL OR ReportParameters_tx = '' THEN SET ReportParameters_tx = '-2147483647';	END IF;
	IF ReportFilter_tx IS NULL OR ReportFilter_tx = '' THEN SET ReportFilter_tx = '-2147483647';	END IF;
	IF ReportIsPrivate_fg IS NULL THEN SET ReportIsPrivate_fg =  -1;	END IF;

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
			*
		FROM
			vwRole_Report_RightType
		WHERE
			vwRole_Report_RightType.Report_id	= Report_id
		AND	vwRole_Report_RightType.Report_tp	= Report_tp
		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			*
		FROM
			vwRole_Report_RightType
		WHERE
			vwRole_Report_RightType.Role_id	= Role_id
		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			*
		FROM
			vwRole_Report_RightType
		WHERE
			vwRole_Report_RightType.Report_id	= Report_id
		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			*
		FROM
			vwRole_Report_RightType
		WHERE
			vwRole_Report_RightType.Role_tp	= Role_tp
		AND	vwRole_Report_RightType.Role_nm	= Role_nm
		AND	vwRole_Report_RightType.Report_tp	= Report_tp
		AND	vwRole_Report_RightType.Report_nm	= Report_nm
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
			*
		FROM
			vwRole_Report_RightType
		WHERE
			(
			vwRole_Report_RightType.ReportPermissionAccount_id	= ReportPermissionAccount_id
		OR	ReportPermissionAccount_id	=  -2147483647
			)
		AND	(
			vwRole_Report_RightType.Role_id	= Role_id
		OR	Role_id	=  -2147483647
			)
		AND	(
			vwRole_Report_RightType.Role_tp	= Role_tp
		OR	Role_tp	= '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.Role_nm	LIKE CONCAT('%', Role_nm, '%')
		OR	Role_nm	= '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.Role_cd	LIKE CONCAT('%', Role_cd, '%')
		OR	Role_cd	= '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.Role_tx	LIKE CONCAT('%', Role_tx, '%')
		OR	Role_tx	= '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.Report_id	= Report_id
		OR	Report_id	=  -2147483647
			)
		AND	(
			vwRole_Report_RightType.Report_tp	= Report_tp
		OR	Report_tp	= '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.Report_nm	LIKE CONCAT('%', Report_nm, '%')
		OR	Report_nm	= '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.Report_cd	LIKE CONCAT('%', Report_cd, '%')
		OR	Report_cd	= '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.Report_tx	LIKE CONCAT('%', Report_tx, '%')
		OR	Report_tx	LIKE '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.ReportADD_dm	= ReportADD_dm
		OR	ReportADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwRole_Report_RightType.ReportADD_nm	LIKE CONCAT('%', ReportADD_nm, '%')
		OR	ReportADD_nm	= '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.ReportUPD_dm	= ReportUPD_dm
		OR	ReportUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwRole_Report_RightType.ReportUPD_nm	LIKE CONCAT('%', ReportUPD_nm, '%')
		OR	ReportUPD_nm	= '-2147483647'
			)
--		AND	vwRole_Report_RightType.ReportDEL_dm	IS NULL
-- 		AND	(
-- 			vwRole_Report_RightType.ReportDEL_nm	LIKE CONCAT('%', ReportDEL_nm, '%')
-- 		OR	ReportDEL_nm	= '-2147483647'
-- 			)
		AND	(
			vwRole_Report_RightType.ParentReport_tp	= ParentReport_tp
		OR	ParentReport_tp	= '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.ReportType_tx	LIKE CONCAT('%', ReportType_tx, '%')
		OR	ReportType_tx	LIKE '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.ReportTypeLeft_id	= ReportTypeLeft_id
		OR	ReportTypeLeft_id	=  -2147483647
			)
		AND	(
			vwRole_Report_RightType.ReportTypeRight_id	= ReportTypeRight_id
		OR	ReportTypeRight_id	=  -2147483647
			)
		AND	(
			vwRole_Report_RightType.ReportTypeLevel_id	= ReportTypeLevel_id
		OR	ReportTypeLevel_id	=  -2147483647
			)
		AND	(
			vwRole_Report_RightType.ReportTypeOrder_id	= ReportTypeOrder_id
		OR	ReportTypeOrder_id	=  -2147483647
			)
		AND	(
			vwRole_Report_RightType.ReportOwner_id	= ReportOwner_id
		OR	ReportOwner_id	=  -2147483647
			)
		AND	(
			vwRole_Report_RightType.ReportIsDeleted_fg	= ReportIsDeleted_fg
		OR	ReportIsDeleted_fg	=  -1
			)
		AND	(
			vwRole_Report_RightType.ReportParameters_tx	LIKE CONCAT('%', ReportParameters_tx, '%')
		OR	ReportParameters_tx	LIKE '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.ReportFilter_tx	LIKE CONCAT('%', ReportFilter_tx, '%')
		OR	ReportFilter_tx	LIKE '-2147483647'
			)
		AND	(
			vwRole_Report_RightType.ReportIsPrivate_fg	= ReportIsPrivate_fg
		OR	ReportIsPrivate_fg	=  -1
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

/*
CALL	gfpRole_Report_RightType
(
		@ReportPermissionAccount_id	:= NULL
	,	@Role_id	:= NULL
	,	@Role_tp	:= 'Admin'
	,	@Role_nm	:= NULL
	,	@Role_cd	:= NULL
	,	@Role_tx	:= NULL

	,	@Report_id	:= NULL
	,	@Report_tp	:= NULL
	,	@Report_nm	:= 'Con'
	,	@Report_cd	:= NULL
	,	@Report_tx	:= NULL

	,	@ReportOwner_id	:= NULL
	,	@ReportIsDeleted_fg	:= NULL
	,	@ReportParameters_tx	:= NULL
	,	@ReportFilter_tx	:= NULL
	,	@ReportIsPrivate_fg	:= NULL
	
	,	@ReportADD_dm	:= NULL
	,	@ReportADD_nm	:= NULL
	,	@ReportUPD_dm	:= NULL
	,	@ReportUPD_nm	:= NULL
	,	@ReportDEL_dm	:= NULL
	,	@ReportDEL_nm	:= NULL
	,	@ParentReport_tp	:= NULL
	,	@ReportType_tx	:= NULL
	,	@ReportTypeLeft_id	:= NULL
	,	@ReportTypeRight_id	:= NULL
	,	@ReportTypeLevel_id	:= NULL
	,	@ReportTypeOrder_id	:= NULL

	,	@Key_cd	:= 'AL'
)
;


*/