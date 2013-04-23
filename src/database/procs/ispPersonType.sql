DROP PROCEDURE IF EXISTS	`ispPersonType`
;

DELIMITER //
CREATE PROCEDURE	ispPersonType
(
	Person_tp		varchar(80)		-- PK1 
,	ParentPerson_tp		varchar(80)	
,	PersonType_tx		mediumtext	
,	PersonTypeLeft_id		int signed	
,	PersonTypeRight_id		int signed	
,	PersonTypeLevel_id		int signed	
,	PersonTypeOrder_id		int signed	

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispPersonType
**	Type:		DB API procedure: Insert
**	Purpose:	To insert PersonType data into tblPersonType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblPersonType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispPersonType';
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
		,	@Table_nm	:= 'tblPersonType'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspPersonType
	(
		@Person_tp	:= Person_tp

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
				Person_tp

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Alternate Key VIEW record exists
	#######################################################################

	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspPersonType
	(
		@Person_tp	:= Person_tp

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
	--	RESTRICT MODE:	tblResourceType
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspResourceType
		(
			@Resrc_tp	:= Person_tp

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblResourceType not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 Person_tp
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblResourceType'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblResourceType
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispResourceType
		(
			@Resrc_tp	:= Person_tp
		,	@ParentResrc_tp	:= ParentPerson_tp
		,	@ResrcType_tx	:= PersonType_tx
		,	@Left_id	:= PersonTypeLeft_id
		,	@Right_id	:= PersonTypeRight_id
		,	@Level_id	:= PersonTypeLevel_id
		,	@Order_id	:= PersonTypeOrder_id

		,	@CallingProc_nm	:= CallingProc_nm
		,	@Source_nm	:= Source_nm
		,	@Token_cd	:= Token_cd
		,	@Mode_cd	:= 'R'	-- This Table Is Restricted and Does Not Allow A Cascade From an FK Table.
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
	INTO	tblPersonType
	(
		Person_tp

	)
	VALUES
	(
		Person_tp

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
			Person_tp

 		;
 	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

