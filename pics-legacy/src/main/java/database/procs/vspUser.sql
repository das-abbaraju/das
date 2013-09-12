DROP PROCEDURE IF EXISTS	`vspUser`
;

DELIMITER //
CREATE PROCEDURE	vspUser
(
	User_id		int signed		
,	User_tp		varchar(80)		
,	User_nm		varchar(128)		
,	Password_cd		varchar(48)		
,	Domain_nm		varchar(128)		
,	Email_tx		mediumtext		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspUser
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblUser
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblUser';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspUser';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblUser
		WHERE
				User_id	= _User_id
			AND	User_tp	= _User_tp
			AND	User_nm	= _User_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspUser
		(
			@User_id	:= _User_id
		,	@User_tp	:= _User_tp
		,	@User_nm	:= _User_nm
		,	@Password_cd	:= _Password_cd
		,	@Domain_nm	:= _Domain_nm
		,	@Email_tx	:= _Email_tx

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblUser'
			,	@Key_nm		:= ', User_tp, User_nm'
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

