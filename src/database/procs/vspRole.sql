DROP PROCEDURE IF EXISTS	`vspRole`
;

DELIMITER //
CREATE PROCEDURE	vspRole
(
	Role_id		int signed		
,	Role_tp		varchar(80)		
,	Role_nm		varchar(128)		
,	Role_cd		varchar(48)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspRole
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblRole
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblRole';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspRole';
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
			tblRole
		WHERE
				Role_id	= _Role_id
			AND	Role_tp	= _Role_tp
			AND	Role_nm	= _Role_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspRole
		(
			@Role_id	:= _Role_id
		,	@Role_tp	:= _Role_tp
		,	@Role_nm	:= _Role_nm
		,	@Role_cd	:= _Role_cd

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
			,	@Table_nm	:= 'tblRole'
			,	@Key_nm		:= ', Role_tp, Role_nm'
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

