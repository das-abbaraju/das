DROP PROCEDURE IF EXISTS	`ispUser_Role`
;

DELIMITER //
CREATE PROCEDURE	ispUser_Role
(
	User_id		int signed		-- PK1 
,	User_tp		varchar(80)		-- PK2 AK1
,	User_nm		varchar(128)		--  AK2
,	Domain_nm		varchar(128)	
,	Password_cd		varchar(48)	
,	Email_tx		mediumtext	
,	Role_id		int signed		-- PK3 
,	Role_tp		varchar(80)		-- PK4 AK3
,	Role_nm		varchar(128)		--  AK4
,	Role_cd		varchar(48)	
,	User_tx		mediumtext	
,	Role_tx		mediumtext	
,	UserADD_dm		datetime	
,	UserADD_nm		varchar(128)	
,	UserUPD_dm		datetime	
,	UserUPD_nm		varchar(128)	
,	UserDEL_dm		datetime	
,	UserDEL_nm		varchar(128)	
,	ParentUser_tp		varchar(80)	
,	UserType_tx		mediumtext	
,	RoleADD_dm		datetime	
,	RoleADD_nm		varchar(128)	
,	RoleUPD_dm		datetime	
,	RoleUPD_nm		varchar(128)	
,	RoleDEL_dm		datetime	
,	RoleDEL_nm		varchar(128)	
,	ParentRole_tp		varchar(80)	
,	RoleType_tx		mediumtext	

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispUser_Role
**	Type:		DB API procedure: Insert
**	Purpose:	To insert User_Role data into tblUser_Role
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblUser_Role';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispUser_Role';
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
		,	@Table_nm	:= 'tblUser_Role'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspUser_Role
	(
		@User_id	:= User_id
	,	@User_tp	:= User_tp
	,	@Role_id	:= Role_id
	,	@Role_tp	:= Role_tp

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
			,	Role_id
			,	Role_tp
			,	Role_nm

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
	,	Role_id	INTO _Role_id
	,	Role_tp	INTO _Role_tp

	FROM
		vwUser_Role
	WHERE
		User_tp	= User_tp
	AND	User_nm	= User_nm
	AND	Role_tp	= Role_tp
	AND	Role_nm	= Role_nm

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
			,	Role_id
			,	Role_tp
			,	Role_nm

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspUser_Role
	(
		@User_id	:= User_id
	,	@User_tp	:= User_tp
	,	@Role_id	:= Role_id
	,	@Role_tp	:= Role_tp

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
		,	@RoleADD_dm	:= RoleADD_dm
		,	@RoleADD_nm	:= RoleADD_nm
		,	@RoleUPD_dm	:= RoleUPD_dm
		,	@RoleUPD_nm	:= RoleUPD_nm
		,	@RoleDEL_dm	:= RoleDEL_dm
		,	@RoleDEL_nm	:= RoleDEL_nm
		,	@ParentRole_tp	:= ParentRole_tp
		,	@RoleType_tx	:= RoleType_tx

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
	INTO	tblUser_Role
	(
		User_id
	,	User_tp
	,	Role_id
	,	Role_tp

	)
	VALUES
	(
		User_id
	,	User_tp
	,	Role_id
	,	Role_tp

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
		,	Role_id
		,	Role_tp
		,	Role_nm

 		;
 	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

