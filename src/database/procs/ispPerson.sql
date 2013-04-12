DROP PROCEDURE IF EXISTS	`ispPerson`
;

DELIMITER //
CREATE PROCEDURE	ispPerson
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

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispPerson
**	Type:		DB API procedure: Insert
**	Purpose:	To insert Person data into tblPerson
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblPerson';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispPerson';
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
		,	@Table_nm	:= 'tblPerson'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspPerson
	(
		@Person_id	:= Person_id
	,	@Person_tp	:= Person_tp

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
				Person_id
			,	Person_tp
			,	Person_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Alternate Key VIEW record exists
	#######################################################################
	SELECT
		Person_id	INTO _Person_id
	,	Person_tp	INTO _Person_tp

	FROM
		vwPerson
	WHERE
		Person_tp	= Person_tp
	AND	Person_nm	= Person_nm

	;
	IF
		FOUND_ROWS()	> 0
	THEN
		IF
			CallingProc_nm	IS NULL OR CallingProc_nm = ''
		THEN
			SELECT
				Person_id
			,	Person_tp
			,	Person_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspPerson
	(
		@Person_id	:= Person_id
	,	@Person_tp	:= Person_tp
	,	@Person_nm	:= Person_nm
	,	@First_nm	:= First_nm
	,	@Middle_nm	:= Middle_nm
	,	@Last_nm	:= Last_nm
	,	@FirstSNDX_cd	:= FirstSNDX_cd
	,	@LastSNDX_cd	:= LastSNDX_cd
	,	@Birth_dm	:= Birth_dm
	,	@Gender_cd	:= Gender_cd

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
			@Resrc_tp	:= Person_tp
		,	@Resrc_id	:= Person_id
		,	@Resrc_tp	:= Person_tp

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
			,	@Key_nm		:= 			,		 Person_tp			,		 Person_id			,		 Person_tp
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
			@Resrc_tp	:= Person_tp
		,	@Resrc_id	:= Person_id
		,	@Resrc_tp	:= Person_tp
		,	@Resrc_tx	:= Person_tx
		,	@ADD_dm	:= PersonADD_dm
		,	@ADD_nm	:= PersonADD_nm
		,	@UPD_dm	:= PersonUPD_dm
		,	@UPD_nm	:= PersonUPD_nm
		,	@DEL_dm	:= PersonDEL_dm
		,	@DEL_nm	:= PersonDEL_nm

		,	@CallingProc_nm	:= CallingProc_nm
		,	@Source_nm	:= Source_nm
		,	@Token_cd	:= Token_cd
		,	@Mode_cd	:= @Mode_cd
		);
	END IF;

	--
	--	RESTRICT MODE:	tblPersonType
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspPersonType
		(
			@Person_tp	:= Person_tp
		,	@Person_tp	:= Person_tp

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblPersonType not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 Person_tp			,		 Person_tp
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblPersonType'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblPersonType
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispPersonType
		(
			@Person_tp	:= Person_tp
		,	@Person_tp	:= Person_tp
		,	@ParentPerson_tp	:= ParentPerson_tp
		,	@PersonType_tx	:= PersonType_tx
		,	@PersonTypeLeft_id	:= PersonTypeLeft_id
		,	@PersonTypeRight_id	:= PersonTypeRight_id
		,	@PersonTypeLevel_id	:= PersonTypeLevel_id
		,	@PersonTypeOrder_id	:= PersonTypeOrder_id

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
	INTO	tblPerson
	(
		Person_id
	,	Person_tp
	,	Person_nm
	,	First_nm
	,	Middle_nm
	,	Last_nm
	,	FirstSNDX_cd
	,	LastSNDX_cd
	,	Birth_dm
	,	Gender_cd

	)
	VALUES
	(
		Person_id
	,	Person_tp
	,	Person_nm
	,	First_nm
	,	Middle_nm
	,	Last_nm
	,	FirstSNDX_cd
	,	LastSNDX_cd
	,	Birth_dm
	,	Gender_cd

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
			Person_id
		,	Person_tp
		,	Person_nm

 		;
 	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

