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

