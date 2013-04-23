DROP PROCEDURE IF EXISTS	`ispUser`
;

DELIMITER //
CREATE PROCEDURE	ispUser
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

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispUser
**	Type:		DB API procedure: Insert
**	Purpose:	To insert User data into tblUser
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblUser';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispUser';
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
		,	@Table_nm	:= 'tblUser'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspUser
	(
		@User_id	:= User_id
	,	@User_tp	:= User_tp

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
				User_id
			,	User_tp
			,	User_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Alternate Key VIEW record exists
	#######################################################################
	SELECT
		User_id	INTO _User_id
	,	User_tp	INTO _User_tp

	FROM
		vwUser
	WHERE
		User_tp	= User_tp
	AND	User_nm	= User_nm

	;
	IF
		FOUND_ROWS()	> 0
	THEN
		IF
			CallingProc_nm	IS NULL OR CallingProc_nm = ''
		THEN
			SELECT
				User_id
			,	User_tp
			,	User_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspUser
	(
		@User_id	:= User_id
	,	@User_tp	:= User_tp
	,	@User_nm	:= User_nm
	,	@Domain_nm	:= Domain_nm
	,	@Password_cd	:= Password_cd
	,	@Email_tx	:= Email_tx

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
			@Resrc_tp	:= User_tp
		,	@Resrc_id	:= User_id
		,	@Resrc_tp	:= User_tp

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
			,	@Key_nm		:= 			,		 User_tp			,		 User_id			,		 User_tp
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
			@Resrc_tp	:= User_tp
		,	@Resrc_id	:= User_id
		,	@Resrc_tp	:= User_tp
		,	@Resrc_tx	:= User_tx
		,	@ADD_dm	:= UserADD_dm
		,	@ADD_nm	:= UserADD_nm
		,	@UPD_dm	:= UserUPD_dm
		,	@UPD_nm	:= UserUPD_nm
		,	@DEL_dm	:= UserDEL_dm
		,	@DEL_nm	:= UserDEL_nm

		,	@CallingProc_nm	:= CallingProc_nm
		,	@Source_nm	:= Source_nm
		,	@Token_cd	:= Token_cd
		,	@Mode_cd	:= @Mode_cd
		);
	END IF;

	--
	--	RESTRICT MODE:	tblUserType
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspUserType
		(
			@User_tp	:= User_tp
		,	@User_tp	:= User_tp

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblUserType not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 User_tp			,		 User_tp
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblUserType'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblUserType
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispUserType
		(
			@User_tp	:= User_tp
		,	@User_tp	:= User_tp
		,	@ParentUser_tp	:= ParentUser_tp
		,	@UserType_tx	:= UserType_tx
		,	@UserTypeLeft_id	:= UserTypeLeft_id
		,	@UserTypeRight_id	:= UserTypeRight_id
		,	@UserTypeLevel_id	:= UserTypeLevel_id
		,	@UserTypeOrder_id	:= UserTypeOrder_id

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
	INTO	tblUser
	(
		User_id
	,	User_tp
	,	User_nm
	,	Domain_nm
	,	Password_cd
	,	Email_tx

	)
	VALUES
	(
		User_id
	,	User_tp
	,	User_nm
	,	Domain_nm
	,	Password_cd
	,	Email_tx

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
			User_id
		,	User_tp
		,	User_nm

 		;
 	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

