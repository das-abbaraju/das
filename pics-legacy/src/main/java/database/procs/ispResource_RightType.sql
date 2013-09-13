DROP PROCEDURE IF EXISTS	`ispResource_RightType`
;

DELIMITER //
CREATE PROCEDURE	ispResource_RightType
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

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispResource_RightType
**	Type:		DB API procedure: Insert
**	Purpose:	To insert Resource_RightType data into tblResource_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblResource_RightType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispResource_RightType';
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
		,	@Table_nm	:= 'tblResource_RightType'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspResource_RightType
	(
		@Resrc_id	:= Resrc_id
	,	@Resrc_tp	:= Resrc_tp
	,	@Right_tp	:= Right_tp

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
				Resrc_id
			,	Resrc_tp
			,	Right_tp
			,	Resrc_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Alternate Key VIEW record exists
	#######################################################################
	SELECT
		Resrc_id	INTO _Resrc_id
	,	Resrc_tp	INTO _Resrc_tp
	,	Right_tp	INTO _Right_tp

	FROM
		vwResource_RightType
	WHERE
		Resrc_tp	= Resrc_tp
	AND	Right_tp	= Right_tp
	AND	Resrc_nm	= Resrc_nm

	;
	IF
		FOUND_ROWS()	> 0
	THEN
		IF
			CallingProc_nm	IS NULL OR CallingProc_nm = ''
		THEN
			SELECT
				Resrc_id
			,	Resrc_tp
			,	Right_tp
			,	Resrc_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspResource_RightType
	(
		@Resrc_id	:= Resrc_id
	,	@Resrc_tp	:= Resrc_tp
	,	@Right_tp	:= Right_tp

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
			@Resrc_id	:= Resrc_id
		,	@Resrc_tp	:= Resrc_tp

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
			,	@Key_nm		:= 			,		 Resrc_id			,		 Resrc_tp
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
			@Resrc_id	:= Resrc_id
		,	@Resrc_tp	:= Resrc_tp
		,	@Resrc_nm	:= Resrc_nm
		,	@Resrc_tx	:= Resrc_tx
		,	@ParentResrc_tp	:= ParentResrc_tp
		,	@ResrcType_tx	:= ResrcType_tx
		,	@ADD_dm	:= ADD_dm
		,	@ADD_nm	:= ADD_nm
		,	@UPD_dm	:= UPD_dm
		,	@UPD_nm	:= UPD_nm
		,	@DEL_dm	:= DEL_dm
		,	@DEL_nm	:= DEL_nm

		,	@CallingProc_nm	:= CallingProc_nm
		,	@Source_nm	:= Source_nm
		,	@Token_cd	:= Token_cd
		,	@Mode_cd	:= @Mode_cd
		);
	END IF;

	--
	--	RESTRICT MODE:	tblRightType
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspRightType
		(
			@Right_tp	:= Right_tp

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblRightType not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 Right_tp
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblRightType'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblRightType
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispRightType
		(
			@Right_tp	:= Right_tp
		,	@ParentRight_tp	:= ParentRight_tp
		,	@RightType_tx	:= RightType_tx

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
	INTO	tblResource_RightType
	(
		Resrc_id
	,	Resrc_tp
	,	Right_tp

	)
	VALUES
	(
		Resrc_id
	,	Resrc_tp
	,	Right_tp

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
			Resrc_id
		,	Resrc_tp
		,	Right_tp
		,	Resrc_nm

 		;
 	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

