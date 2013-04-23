DROP PROCEDURE IF EXISTS	`vspUser_Person`
;

DELIMITER //
CREATE PROCEDURE	vspUser_Person
(
	User_id		int signed		
,	User_tp		varchar(80)		
,	Person_id		int signed		
,	Person_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspUser_Person
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblUser_Person
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblUser_Person';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspUser_Person';
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
			tblUser_Person
		WHERE
				User_id	= _User_id
			AND	User_tp	= _User_tp
			AND	Person_id	= _Person_id
			AND	Person_tp	= _Person_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspUser_Person
		(
			@User_id	:= _User_id
		,	@User_tp	:= _User_tp
		,	@Person_id	:= _Person_id
		,	@Person_tp	:= _Person_tp

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
			,	@Table_nm	:= 'tblUser_Person'
			,	@Key_nm		:= ''
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

