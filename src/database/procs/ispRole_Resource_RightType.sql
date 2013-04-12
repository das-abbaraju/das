DROP PROCEDURE IF EXISTS	`ispRole_Resource_RightType`
;

DELIMITER //
CREATE PROCEDURE	ispRole_Resource_RightType
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

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispRole_Resource_RightType
**	Type:		DB API procedure: Insert
**	Purpose:	To insert Role_Resource_RightType data into tblRole_Resource_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblRole_Resource_RightType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispRole_Resource_RightType';
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
		,	@Table_nm	:= 'tblRole_Resource_RightType'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspRole_Resource_RightType
	(
		@Role_id	:= Role_id
	,	@Role_tp	:= Role_tp
	,	@Resrc_id	:= Resrc_id
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
				Role_id
			,	Role_tp
			,	Role_nm
			,	Resrc_id
			,	Resrc_tp
			,	Resrc_nm
			,	Right_tp

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Alternate Key VIEW record exists
	#######################################################################
	SELECT
		Role_id	INTO _Role_id
	,	Role_tp	INTO _Role_tp
	,	Resrc_id	INTO _Resrc_id
	,	Resrc_tp	INTO _Resrc_tp
	,	Right_tp	INTO _Right_tp

	FROM
		vwRole_Resource_RightType
	WHERE
		Role_tp	= Role_tp
	AND	Role_nm	= Role_nm
	AND	Resrc_id	= Resrc_id
	AND	Resrc_tp	= Resrc_tp
	AND	Resrc_nm	= Resrc_nm
	AND	Right_tp	= Right_tp

	;
	IF
		FOUND_ROWS()	> 0
	THEN
		IF
			CallingProc_nm	IS NULL OR CallingProc_nm = ''
		THEN
			SELECT
				Role_id
			,	Role_tp
			,	Role_nm
			,	Resrc_id
			,	Resrc_tp
			,	Resrc_nm
			,	Right_tp

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspRole_Resource_RightType
	(
		@Role_id	:= Role_id
	,	@Role_tp	:= Role_tp
	,	@Resrc_id	:= Resrc_id
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
	--	RESTRICT MODE:	tblRole
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspRole
		(
			@Role_id	:= Role_id
		,	@Role_tp	:= Role_tp

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblRole not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 Role_id			,		 Role_tp
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblRole'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblRole
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispRole
		(
			@Role_id	:= Role_id
		,	@Role_tp	:= Role_tp
		,	@Role_nm	:= Role_nm
		,	@Role_cd	:= Role_cd
		,	@Role_tx	:= Role_tx

		,	@CallingProc_nm	:= CallingProc_nm
		,	@Source_nm	:= Source_nm
		,	@Token_cd	:= Token_cd
		,	@Mode_cd	:= @Mode_cd
		);
	END IF;

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

		,	@CallingProc_nm	:= CallingProc_nm
		,	@Source_nm	:= Source_nm
		,	@Token_cd	:= Token_cd
		,	@Mode_cd	:= @Mode_cd
		);
	END IF;

	--
	--	RESTRICT MODE:	tblResource_RightType
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspResource_RightType
		(
			@Resrc_id	:= Resrc_id
		,	@Resrc_tp	:= Resrc_tp
		,	@Right_tp	:= Right_tp

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblResource_RightType not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 Resrc_id			,		 Resrc_tp			,		 Right_tp
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblResource_RightType'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblResource_RightType
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispResource_RightType
		(
			@Resrc_id	:= Resrc_id
		,	@Resrc_tp	:= Resrc_tp
		,	@Right_tp	:= Right_tp
		,	@Resrc_nm	:= Resrc_nm
		,	@Resrc_tx	:= Resrc_tx
		,	@ParentResrc_tp	:= ParentResrc_tp
		,	@ResrcType_tx	:= ResrcType_tx
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
	INTO	tblRole_Resource_RightType
	(
		Role_id
	,	Role_tp
	,	Resrc_id
	,	Resrc_tp
	,	Right_tp

	)
	VALUES
	(
		Role_id
	,	Role_tp
	,	Resrc_id
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
			Role_id
		,	Role_tp
		,	Role_nm
		,	Resrc_id
		,	Resrc_tp
		,	Resrc_nm
		,	Right_tp

 		;
 	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

