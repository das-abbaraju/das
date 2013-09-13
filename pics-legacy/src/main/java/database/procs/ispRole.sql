DROP PROCEDURE IF EXISTS	`ispRole`
;

DELIMITER //
CREATE PROCEDURE	ispRole
(
	Role_id		int signed		-- PK1 
,	Role_tp		varchar(80)		-- PK2 AK1
,	Role_nm		varchar(128)		--  AK2
,	Role_cd		varchar(48)	
,	Role_tx		mediumtext	
,	RoleADD_dm		datetime	
,	RoleADD_nm		varchar(128)	
,	RoleUPD_dm		datetime	
,	RoleUPD_nm		varchar(128)	
,	RoleDEL_dm		datetime	
,	RoleDEL_nm		varchar(128)	
,	ParentRole_tp		varchar(80)	
,	RoleType_tx		mediumtext	
,	RoleTypeLeft_id		int signed	
,	RoleTypeRight_id		int signed	
,	RoleTypeLevel_id		int signed	
,	RoleTypeOrder_id		int signed	

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispRole
**	Type:		DB API procedure: Insert
**	Purpose:	To insert Role data into tblRole
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblRole';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispRole';
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
		,	@Table_nm	:= 'tblRole'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspRole
	(
		@Role_id	:= Role_id
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
				Role_id
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
		Role_id	INTO _Role_id
	,	Role_tp	INTO _Role_tp

	FROM
		vwRole
	WHERE
		Role_tp	= Role_tp
	AND	Role_nm	= Role_nm

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

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspRole
	(
		@Role_id	:= Role_id
	,	@Role_tp	:= Role_tp
	,	@Role_nm	:= Role_nm
	,	@Role_cd	:= Role_cd

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
			@Resrc_tp	:= Role_tp
		,	@Resrc_id	:= Role_id
		,	@Resrc_tp	:= Role_tp

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
			,	@Key_nm		:= 			,		 Role_tp			,		 Role_id			,		 Role_tp
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
			@Resrc_tp	:= Role_tp
		,	@Resrc_id	:= Role_id
		,	@Resrc_tp	:= Role_tp
		,	@Resrc_tx	:= Role_tx
		,	@ADD_dm	:= RoleADD_dm
		,	@ADD_nm	:= RoleADD_nm
		,	@UPD_dm	:= RoleUPD_dm
		,	@UPD_nm	:= RoleUPD_nm
		,	@DEL_dm	:= RoleDEL_dm
		,	@DEL_nm	:= RoleDEL_nm

		,	@CallingProc_nm	:= CallingProc_nm
		,	@Source_nm	:= Source_nm
		,	@Token_cd	:= Token_cd
		,	@Mode_cd	:= @Mode_cd
		);
	END IF;

	--
	--	RESTRICT MODE:	tblRoleType
	--
	IF
		@Mode_cd	= 'R'
	THEN
		CALL	rspRoleType
		(
			@Role_tp	:= Role_tp
		,	@Role_tp	:= Role_tp

		,	@Key_cd		:= Key_cd
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 0   -- Foreign key in tblRoleType not found!
		THEN
			SET ProcFailed_fg	= TRUE;
			CALL 	errFKNotExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= SYSTABLE
			,	@Action_nm	:= SYSRIGHT
			,	@Key_nm		:= 			,		 Role_tp			,		 Role_tp
			);

			CALL 	errFailedMode
			(
				@Proc_nm	:= Proc_nm
			,	@Mode_cd	:= Mode_cd
			,	@Action_nm	:= SYSRIGHT
			,	@Table_nm	:= 'tblRoleType'
			);
			LEAVE	ISP;
		END IF;
	END IF;
	--
	--	CASCADE MODE:	tblRoleType
	--
	IF
		@Mode_cd	= 'C'
	THEN
		CALL	ispRoleType
		(
			@Role_tp	:= Role_tp
		,	@Role_tp	:= Role_tp
		,	@ParentRole_tp	:= ParentRole_tp
		,	@RoleType_tx	:= RoleType_tx
		,	@RoleTypeLeft_id	:= RoleTypeLeft_id
		,	@RoleTypeRight_id	:= RoleTypeRight_id
		,	@RoleTypeLevel_id	:= RoleTypeLevel_id
		,	@RoleTypeOrder_id	:= RoleTypeOrder_id

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
	INTO	tblRole
	(
		Role_id
	,	Role_tp
	,	Role_nm
	,	Role_cd

	)
	VALUES
	(
		Role_id
	,	Role_tp
	,	Role_nm
	,	Role_cd

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

 		;
 	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

