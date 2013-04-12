DROP PROCEDURE IF EXISTS	`gspReport`
;

CREATE PROCEDURE	[pics_alpha1].[gspReport]
(
	@Report_id		udtResrc_id	=  -2147483647	-- PK1 
,	@Report_tp		udtResrc_tp	= '-2147483647'	-- PK2 AK1
,	@Report_nm		udtResrc_nm	= '-2147483647'	--  AK2
,	@Report_cd		udtResrc_cd	= '-2147483647'
,	@Report_tx		udtResrc_tx	= '-2147483647'
,	@ReportADD_dm		udtResrc_dm	= '01/01/1754'
,	@ReportADD_nm		udtResrc_nm	= '-2147483647'
,	@ReportUPD_dm		udtResrc_dm	= '01/01/1754'
,	@ReportUPD_nm		udtResrc_nm	= '-2147483647'
,	@ReportDEL_dm		udtResrc_dm	= '01/01/1754'
,	@ReportDEL_nm		udtResrc_nm	= '-2147483647'
,	@ParentReport_tp		udtResrc_tp	= '-2147483647'
,	@ReportType_tx		udtResrc_tx	= '-2147483647'
,	@ReportTypeLeft_id		udtResrc_id	=  -2147483647
,	@ReportTypeRight_id		udtResrc_id	=  -2147483647
,	@ReportTypeLevel_id		udtResrc_id	=  -2147483647
,	@ReportTypeOrder_id		udtResrc_id	=  -2147483647

,	@Key_cd			udtKey_cd		= 'PK'	-- Search key code
)
--WITH ENCRYPTION
AS
/*
**	Name:		gspReport
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwReport
**	Author:		Solomon S. Shacter
**	Company:	DataLabs, Inc. Copyright 2006. All Rights Reserved
**
**	Modified:	4/11/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
-------------------------------------------------------------------------------
SET	NOCOUNT	ON
-------------------------------------------------------------------------------
DECLARE	@RETURN		int
DECLARE	@STATUS		int
DECLARE	@ERROR		int
DECLARE	@SYSTABLE	varchar(255)
DECLARE	@SYSRIGHT	varchar(40)
DECLARE	@Proc_nm	varchar(255)
-------------------------------------------------------------------------------
BEGIN
	-----------------------------------------------------------------------
	-- Initialize
	-----------------------------------------------------------------------
	SELECT
 		@STATUS		= 0
	,	@RETURN		= 0
	,	@SYSTABLE	= 'vwReport'
	,	@SYSRIGHT	= 'SELECT'
	,	@Proc_nm	= OBJECT_NAME(@@PROCID)
	-----------------------------------------------------------------------
	-- Check Security
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= spSecurityCheck
		@SYSTABLE	= @SYSTABLE
	,	@SYSRIGHT	= @SYSRIGHT

	IF
	(
		@RETURN	<> 0
	)
	BEGIN
		EXECUTE	@STATUS		= errFailedSecurity
			@Proc_nm	= @Proc_nm
		,	@Table_nm	= @SYSTABLE
		,	@Action_nm	= @SYSRIGHT
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Primary Key lookup
	-----------------------------------------------------------------------
	IF	Key_cd = 'PK'
	THEN
		SELECT
			Report_id
		,	Report_tp
		,	Report_nm
		,	Report_cd
		,	Report_tx
		,	ReportADD_dm
		,	ReportADD_nm
		,	ReportUPD_dm
		,	ReportUPD_nm
		,	ReportDEL_dm
		,	ReportDEL_nm
		,	ParentReport_tp
		,	ReportType_tx
		,	ReportTypeLeft_id
		,	ReportTypeRight_id
		,	ReportTypeLevel_id
		,	ReportTypeOrder_id
		FROM
			vwReport
		WHERE
			Report_id	= @Report_id
		AND	Report_tp	= @Report_tp

		;
	END IF;
	-----------------------------------------------------------------------
	-- Foreign Key lookup
	-----------------------------------------------------------------------
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			Report_id
		,	Report_tp
		,	Report_nm
		,	Report_cd
		,	Report_tx
		,	ReportADD_dm
		,	ReportADD_nm
		,	ReportUPD_dm
		,	ReportUPD_nm
		,	ReportDEL_dm
		,	ReportDEL_nm
		,	ParentReport_tp
		,	ReportType_tx
		,	ReportTypeLeft_id
		,	ReportTypeRight_id
		,	ReportTypeLevel_id
		,	ReportTypeOrder_id
		FROM
			vwReport
		WHERE
			Report_id	= @Report_id

		;
	END IF;

	-----------------------------------------------------------------------
	-- Alternate Key lookup
	-----------------------------------------------------------------------
	IF	Key_cd = 'AK'
	THEN
		SELECT
			Report_id
		,	Report_tp
		,	Report_nm
		,	Report_cd
		,	Report_tx
		,	ReportADD_dm
		,	ReportADD_nm
		,	ReportUPD_dm
		,	ReportUPD_nm
		,	ReportDEL_dm
		,	ReportDEL_nm
		,	ParentReport_tp
		,	ReportType_tx
		,	ReportTypeLeft_id
		,	ReportTypeRight_id
		,	ReportTypeLevel_id
		,	ReportTypeOrder_id
		FROM
			vwReport
		WHERE
			Report_tp	= @Report_tp
		AND	Report_nm	= @Report_nm

		;
	END IF;
	-----------------------------------------------------------------------
	-- Search Key lookup
	-----------------------------------------------------------------------
	--   NO UI SEARCH KEY(S) DEFINED FOR THIS OBJECT
	-----------------------------------------------------------------------
	-- Attribute lookup
	-----------------------------------------------------------------------
	IF	Key_cd = 'AL'
	THEN
		SELECT
			Report_id
		,	Report_tp
		,	Report_nm
		,	Report_cd
		,	Report_tx
		,	ReportADD_dm
		,	ReportADD_nm
		,	ReportUPD_dm
		,	ReportUPD_nm
		,	ReportDEL_dm
		,	ReportDEL_nm
		,	ParentReport_tp
		,	ReportType_tx
		,	ReportTypeLeft_id
		,	ReportTypeRight_id
		,	ReportTypeLevel_id
		,	ReportTypeOrder_id
		FROM
			vwReport
		WHERE
			(
			Report_id	= @Report_id
		OR	@Report_id	=  -2147483647
			)
		AND	(
			Report_tp	= @Report_tp
		OR	@Report_tp	= '-2147483647'
			)
		AND	(
			Report_nm	= @Report_nm
		OR	@Report_nm	= '-2147483647'
			)
		AND	(
			Report_cd	= @Report_cd
		OR	@Report_cd	= '-2147483647'
			)
		AND	(
			Report_tx	LIKE @Report_tx
		OR	@Report_tx	LIKE '-2147483647'
			)
		AND	(
			ReportADD_dm	= @ReportADD_dm
		OR	@ReportADD_dm	= '01/01/1754'
			)
		AND	(
			ReportADD_nm	= @ReportADD_nm
		OR	@ReportADD_nm	= '-2147483647'
			)
		AND	(
			ReportUPD_dm	= @ReportUPD_dm
		OR	@ReportUPD_dm	= '01/01/1754'
			)
		AND	(
			ReportUPD_nm	= @ReportUPD_nm
		OR	@ReportUPD_nm	= '-2147483647'
			)
		AND	(
			ReportDEL_dm	= @ReportDEL_dm
		OR	@ReportDEL_dm	= '01/01/1754'
			)
		AND	(
			ReportDEL_nm	= @ReportDEL_nm
		OR	@ReportDEL_nm	= '-2147483647'
			)
		AND	(
			ParentReport_tp	= @ParentReport_tp
		OR	@ParentReport_tp	= '-2147483647'
			)
		AND	(
			ReportType_tx	LIKE @ReportType_tx
		OR	@ReportType_tx	LIKE '-2147483647'
			)
		AND	(
			ReportTypeLeft_id	= @ReportTypeLeft_id
		OR	@ReportTypeLeft_id	=  -2147483647
			)
		AND	(
			ReportTypeRight_id	= @ReportTypeRight_id
		OR	@ReportTypeRight_id	=  -2147483647
			)
		AND	(
			ReportTypeLevel_id	= @ReportTypeLevel_id
		OR	@ReportTypeLevel_id	=  -2147483647
			)
		AND	(
			ReportTypeOrder_id	= @ReportTypeOrder_id
		OR	@ReportTypeOrder_id	=  -2147483647
			)

		;
	END IF;
	-----------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	0
GO

