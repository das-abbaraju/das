DROP PROCEDURE IF EXISTS	`vspRole_Resource_RightType`
;

DELIMITER //
CREATE PROCEDURE	vspRole_Resource_RightType
(
	Role_id		int signed		
,	Role_tp		varchar(80)		
,	Resrc_id		int signed		
,	Resrc_tp		varchar(80)		
,	Right_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspRole_Resource_RightType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblRole_Resource_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblRole_Resource_RightType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspRole_Resource_RightType';
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
			tblRole_Resource_RightType
		WHERE
				Role_id	= _Role_id
			AND	Role_tp	= _Role_tp
			AND	Resrc_id	= _Resrc_id
			AND	Resrc_tp	= _Resrc_tp
			AND	Right_tp	= _Right_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspRole_Resource_RightType
		(
			@Role_id	:= _Role_id
		,	@Role_tp	:= _Role_tp
		,	@Resrc_id	:= _Resrc_id
		,	@Resrc_tp	:= _Resrc_tp
		,	@Right_tp	:= _Right_tp

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
			,	@Table_nm	:= 'tblRole_Resource_RightType'
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

