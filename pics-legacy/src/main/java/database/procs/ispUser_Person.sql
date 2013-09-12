DROP PROCEDURE IF EXISTS	`ispUser_Person`
;

DELIMITER //
CREATE PROCEDURE	ispUser_Person
(
	User_id		int signed		-- PK1 
,	User_tp		varchar(80)		-- PK2 AK1
,	User_nm		varchar(128)		--  AK2
,	Domain_nm		varchar(128)	
,	Password_cd		varchar(48)	
,	Email_tx		mediumtext	
,	Person_id		int signed		-- PK3 
,	Person_tp		varchar(80)		-- PK4 AK3
,	Person_nm		varchar(128)		--  AK4
,	First_nm		varchar(128)	
,	Middle_nm		varchar(128)	
,	Last_nm		varchar(128)	
,	FirstSNDX_cd		varchar(48)	
,	LastSNDX_cd		varchar(48)	
,	Birth_dm		datetime	
,	Gender_cd		varchar(48)	
,	User_tx		mediumtext	
,	Person_tx		mediumtext	
,	UserADD_dm		datetime	
,	UserADD_nm		varchar(128)	
,	UserUPD_dm		datetime	
,	UserUPD_nm		varchar(128)	
,	UserDEL_dm		datetime	
,	UserDEL_nm		varchar(128)	
,	ParentUser_tp		varchar(80)	
,	UserType_tx		mediumtext	
,	PersonADD_dm		datetime	
,	PersonADD_nm		varchar(128)	
,	PersonUPD_dm		datetime	
,	PersonUPD_nm		varchar(128)	
,	PersonDEL_dm		datetime	
,	PersonDEL_nm		varchar(128)	
,	ParentPerson_tp		varchar(80)	
,	PersonType_tx		mediumtext	

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispUser_Person
**	Type:		DB API procedure: Insert
**	Purpose:	To insert User_Person data into tblUser_Person
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblUser_Person';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispUser_Person';
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
		,	@Table_nm	:= 'tblUser_Person'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspUser_Person
	(
		@User_id	:= User_id
	,	@User_tp	:= User_tp
	,	@Person_id	:= Person_id
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
				User_id
			,	User_tp
			,	User_nm
			,	Person_id
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
		User_id	INTO _User_id
	,	User_tp	INTO _User_tp
	,	Person_id	INTO _Person_id
	,	Person_tp	INTO _Person_tp

	FROM
		vwUser_Person
	WHERE
		User_tp	= User_tp
	AND	User_nm	= User_nm
	AND	Person_tp	= Person_tp
	AND	Person_nm	= Person_nm

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
			,	Person_id
			,	Person_tp
			,	Person_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspUser_Person
	(
		@User_id	:= User_id
	,	@User_tp	:= User_tp
	,	@Person_id	:= Person_id
	,	@Person_tp	:= Person_tp

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
	--	RESTRICT MODE:	tblUser
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspUser
		(
			@User_id	:= User_id
		,	@User_tp	:= User_tp

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblUser not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 User_id			,		 User_tp
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblUser'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblUser
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispUser
		(
			@User_id	:= User_id
		,	@User_tp	:= User_tp
		,	@User_nm	:= User_nm
		,	@Domain_nm	:= Domain_nm
		,	@Password_cd	:= Password_cd
		,	@Email_tx	:= Email_tx
		,	@User_tx	:= User_tx
		,	@UserADD_dm	:= UserADD_dm
		,	@UserADD_nm	:= UserADD_nm
		,	@UserUPD_dm	:= UserUPD_dm
		,	@UserUPD_nm	:= UserUPD_nm
		,	@UserDEL_dm	:= UserDEL_dm
		,	@UserDEL_nm	:= UserDEL_nm
		,	@ParentUser_tp	:= ParentUser_tp
		,	@UserType_tx	:= UserType_tx

		,	@CallingProc_nm	:= CallingProc_nm
		,	@Source_nm	:= Source_nm
		,	@Token_cd	:= Token_cd
		,	@Mode_cd	:= @Mode_cd
		);
	END IF;

	--
	--	RESTRICT MODE:	tblPerson
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspPerson
		(
			@Person_id	:= Person_id
		,	@Person_tp	:= Person_tp

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblPerson not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 Person_id			,		 Person_tp
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblPerson'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblPerson
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispPerson
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
		,	@Person_tx	:= Person_tx
		,	@PersonADD_dm	:= PersonADD_dm
		,	@PersonADD_nm	:= PersonADD_nm
		,	@PersonUPD_dm	:= PersonUPD_dm
		,	@PersonUPD_nm	:= PersonUPD_nm
		,	@PersonDEL_dm	:= PersonDEL_dm
		,	@PersonDEL_nm	:= PersonDEL_nm
		,	@ParentPerson_tp	:= ParentPerson_tp
		,	@PersonType_tx	:= PersonType_tx

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
	INTO	tblUser_Person
	(
		User_id
	,	User_tp
	,	Person_id
	,	Person_tp

	)
	VALUES
	(
		User_id
	,	User_tp
	,	Person_id
	,	Person_tp

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
		,	Person_id
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

