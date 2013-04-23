DROP PROCEDURE IF EXISTS	`vspResourceType`
;

DELIMITER //
CREATE PROCEDURE	vspResourceType
(
	Resrc_tp		varchar(80)		
,	ParentResrc_tp		varchar(80)		
,	ResrcType_tx		mediumtext		
,	Left_id		int signed		
,	Right_id		int signed		
,	Level_id		int signed		
,	Order_id		int signed		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspResourceType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblResourceType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblResourceType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspResourceType';
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
			tblResourceType
		WHERE
				Resrc_tp	= _Resrc_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspResourceType
		(
			@Resrc_tp	:= _Resrc_tp
		,	@ParentResrc_tp	:= _ParentResrc_tp
		,	@ResrcType_tx	:= _ResrcType_tx
		,	@Left_id	:= _Left_id
		,	@Right_id	:= _Right_id
		,	@Level_id	:= _Level_id
		,	@Order_id	:= _Order_id

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
			,	@Table_nm	:= 'tblResourceType'
			,	@Key_nm		:= ', Resrc_tp'
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

