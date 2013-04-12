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

