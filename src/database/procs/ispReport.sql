DROP PROCEDURE IF EXISTS	`ispReport`
;

DELIMITER //
CREATE PROCEDURE	ispReport
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

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispReport
**	Type:		DB API procedure: Insert
**	Purpose:	To insert Report data into tblReport
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblReport';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispReport';
DECLARE	Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE RowExists_fg	TINYINT	DEFAULT 0;
DECLARE ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
ISP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF CallingProc_nm IS NULL OR CallingProc_nm = '' THEN SET CallingProc_nm = '';	END IF;
	IF Source_nm IS NULL OR Source_nm = '' THEN SET Source_nm = '';	END IF;
	IF Token_cd IS NULL OR Token_cd = '' THEN SET Token_cd = '';	END IF;
	IF Mode_cd IS NULL OR Mode_cd = '' THEN SET Mode_cd = 'R';	END IF;
	#######################################################################
	-- Verify Correct Use of Database Mode
	#######################################################################
	IF	Mode_cd	= 'R'
	OR	Mode_cd	= 'C'
	OR	Mode_cd	= 'H'
	OR	Mode_cd	= 'N'
	THEN
		SET ProcFailed_fg	= FALSE;
	ELSE
		SET ProcFailed_fg	= TRUE;
		CALL 	errFailedMode
		(
			@Proc_nm	:= Proc_nm
		,	@Mode_cd	:= Mode_cd
		,	@Action_nm	:= SYSRIGHT
		,	@Table_nm	:= 'tblReport'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspReport
	(
		@Report_id	:= Report_id
	,	@Report_tp	:= Report_tp

	,	@Key_cd		:= Key_cd
	,	@RowExists_fg
	);

	IF
		@RowExists_fg	= 1	-- If PK exists then return without error
	THEN
		IF
			CallingProc_nm	IS NULL OR CallingProc_nm = ''
		THEN
			SELECT
				Report_id
			,	Report_tp
			,	Report_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Alternate Key VIEW record exists
	#######################################################################
	SELECT
		Report_id	INTO _Report_id
	,	Report_tp	INTO _Report_tp

	FROM
		vwReport
	WHERE
		Report_tp	= Report_tp
	AND	Report_nm	= Report_nm

	;
	IF
		FOUND_ROWS()	> 0
	THEN
		IF
			CallingProc_nm	IS NULL OR CallingProc_nm = ''
		THEN
			SELECT
				Report_id
			,	Report_tp
			,	Report_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspReport
	(
		@Report_id	:= Report_id
	,	@Report_tp	:= Report_tp
	,	@Report_nm	:= Report_nm
	,	@Report_cd	:= Report_cd

	,	@SYSRIGHT	:= SYSRIGHT
	,	@Mode_cd	:= Mode_cd
	,	@IsValid_fg
	);
	IF	@IsValid_fg	= FALSE
	THEN
		SET ProcFailed_fg	= TRUE;
		CALL 	errFailedCall
		(
			@Proc_nm	:= Proc_nm
		,	@CallingProc_nm	:= CallingProc_nm
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Check Referential Integerity
	#######################################################################
	--
	--	RESTRICT MODE:	tblResource
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspResource
		(
			@Resrc_id	:= Report_id

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblResource not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 Report_id
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblResource'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblResource
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispResource
		(
			@Resrc_id	:= Report_id
		,	@Resrc_tx	:= Report_tx
		,	@ADD_dm	:= ReportADD_dm
		,	@ADD_nm	:= ReportADD_nm
		,	@UPD_dm	:= ReportUPD_dm
		,	@UPD_nm	:= ReportUPD_nm
		,	@DEL_dm	:= ReportDEL_dm
		,	@DEL_nm	:= ReportDEL_nm

		,	@CallingProc_nm	:= CallingProc_nm
		,	@Source_nm	:= Source_nm
		,	@Token_cd	:= Token_cd
		,	@Mode_cd	:= @Mode_cd
		);
	END IF;


	#######################################################################
	-- Insert values
	#######################################################################
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION 
	BEGIN
		SET ProcFailed_fg	= TRUE;
		CALL 	errFailedEvent
		(
			@Proc_nm	:= Proc_nm
		,	@Table_nm	:= SYSTABLE
		,	@Action_nm	:= SYSRIGHT
		);
	END;

	INSERT
	INTO	tblReport
	(
		Report_id
	,	Report_tp
	,	Report_nm
	,	Report_cd

	)
	VALUES
	(
		Report_id
	,	Report_tp
	,	Report_nm
	,	Report_cd

	);
END;
	#######################################################################
	-- Insert this transaction in the transaction history log
	#######################################################################

	#######################################################################
	-- Return the Primary Key to the front-end if this procedure
	-- was not called from another API stored procedure.
	#######################################################################
 	IF
 		CallingProc_nm	IS NULL OR CallingProc_nm = ''
 	THEN
		SELECT
			Report_id
		,	Report_tp
		,	Report_nm

 		;
 	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

