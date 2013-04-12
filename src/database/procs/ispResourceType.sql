DROP PROCEDURE IF EXISTS	`ispResourceType`
;

DELIMITER //
CREATE PROCEDURE	ispResourceType
(
	Resrc_tp		varchar(80)		-- PK1 AK1
,	ParentResrc_tp		varchar(80)	
,	ResrcType_tx		mediumtext	
,	Left_id		int signed	
,	Right_id		int signed	
,	Level_id		int signed	
,	Order_id		int signed	

,	CallingProc_nm	VARCHAR(128)	-- CallingProc	The sproc calling this proc
,	Source_nm	VARCHAR(128)	-- SourceSystem	System name
,	Token_cd	VARCHAR(48)	-- Token	Security Token
,	Mode_cd		VARCHAR(16)	-- Mode	Database cascade mode code
)
BEGIN
/*
**	Name:		ispResourceType
**	Type:		DB API procedure: Insert
**	Purpose:	To insert ResourceType data into tblResourceType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblResourceType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'INSERT';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'ispResourceType';
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
		,	@Table_nm	:= 'tblResourceType'
		);
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Primary Key TABLE record exists
	#######################################################################
	CALL	rspResourceType
	(
		@Resrc_tp	:= Resrc_tp

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
				Resrc_tp

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Return if Alternate Key VIEW record exists
	#######################################################################
	SELECT
		Resrc_tp	INTO _Resrc_tp

	FROM
		vwResourceType
	WHERE
		Resrc_tp	= Resrc_tp

	;
	IF
		FOUND_ROWS()	> 0
	THEN
		IF
			CallingProc_nm	IS NULL OR CallingProc_nm = ''
		THEN
			SELECT
				Resrc_tp

			;
		END IF;
		LEAVE	ISP;
	END IF;
	#######################################################################
	-- Validate attributes
	#######################################################################
	CALL	vspResourceType
	(
		@Resrc_tp	:= Resrc_tp
	,	@ParentResrc_tp	:= ParentResrc_tp
	,	@ResrcType_tx	:= ResrcType_tx
	,	@Left_id	:= Left_id
	,	@Right_id	:= Right_id
	,	@Level_id	:= Level_id
	,	@Order_id	:= Order_id

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
	--   NO SUPER-SET OR PARENT FK TABLE FOR THIS OBJECT
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
	INTO	tblResourceType
	(
		Resrc_tp
	,	ParentResrc_tp
	,	ResrcType_tx
	,	Left_id
	,	Right_id
	,	Level_id
	,	Order_id

	)
	VALUES
	(
		Resrc_tp
	,	ParentResrc_tp
	,	ResrcType_tx
	,	Left_id
	,	Right_id
	,	Level_id
	,	Order_id

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
			Resrc_tp

 		;
 	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

