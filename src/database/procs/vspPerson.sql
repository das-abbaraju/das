DROP PROCEDURE IF EXISTS	`vspPerson`
;

DELIMITER //
CREATE PROCEDURE	vspPerson
(
	Person_id		int signed		
,	Person_tp		varchar(80)		
,	Person_nm		varchar(128)		
,	First_nm		varchar(128)		
,	Last_nm		varchar(128)		
,	Middle_nm		varchar(128)		
,	Gender_cd		varchar(48)		
,	FirstSNDX_cd		varchar(48)		
,	LastSNDX_cd		varchar(48)		
,	Birth_dm		datetime		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspPerson
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblPerson
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblPerson';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspPerson';
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
			tblPerson
		WHERE
				Person_id	= _Person_id
			AND	Person_tp	= _Person_tp
			AND	Person_nm	= _Person_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspPerson
		(
			@Person_id	:= _Person_id
		,	@Person_tp	:= _Person_tp
		,	@Person_nm	:= _Person_nm
		,	@First_nm	:= _First_nm
		,	@Last_nm	:= _Last_nm
		,	@Middle_nm	:= _Middle_nm
		,	@Gender_cd	:= _Gender_cd
		,	@FirstSNDX_cd	:= _FirstSNDX_cd
		,	@LastSNDX_cd	:= _LastSNDX_cd
		,	@Birth_dm	:= _Birth_dm

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
			,	@Table_nm	:= 'tblPerson'
			,	@Key_nm		:= ', Person_tp, Person_nm'
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

