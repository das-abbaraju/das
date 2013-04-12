DROP PROCEDURE IF EXISTS	`vspResource`
;

DELIMITER //
CREATE PROCEDURE	vspResource
(
	Resrc_id		int signed		
,	Resrc_tp		varchar(80)		
,	Resrc_nm		varchar(128)		
,	Resrc_tx		mediumtext		
,	ADD_dm		datetime		
,	ADD_nm		varchar(128)		
,	UPD_dm		datetime		
,	UPD_nm		varchar(128)		
,	DEL_dm		datetime		
,	DEL_nm		varchar(128)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspResource
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblResource
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblResource';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspResource';
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
			tblResource
		WHERE
				Resrc_id	= _Resrc_id
			AND	Resrc_tp	= _Resrc_tp
			AND	Resrc_nm	= _Resrc_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspResource
		(
			@Resrc_id	:= _Resrc_id
		,	@Resrc_tp	:= _Resrc_tp
		,	@Resrc_nm	:= _Resrc_nm
		,	@Resrc_tx	:= _Resrc_tx
		,	@ADD_dm	:= _ADD_dm
		,	@ADD_nm	:= _ADD_nm
		,	@UPD_dm	:= _UPD_dm
		,	@UPD_nm	:= _UPD_nm
		,	@DEL_dm	:= _DEL_dm
		,	@DEL_nm	:= _DEL_nm

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
			,	@Table_nm	:= 'tblResource'
			,	@Key_nm		:= ', Resrc_tp, Resrc_nm'
			);
			LEAVE VSP;
		END IF;
	END IF;

	IF
		_SYSRIGHT = 'INSERT'
	THEN
		IF	_ADD_dm	IS NULL
		THEN
			SET _ADD_dm	= UTC_TIMESTAMP();
		END IF;
		IF	_ADD_nm	IS NULL
		THEN
			SET	_ADD_nm	= CURRENT_USER();
		END IF;
	END IF;

	IF
		_SYSRIGHT = 'UPDATE'
	THEN
		IF	_UPD_dm	IS NULL
		THEN
			SET	_UPD_dm	= UTC_TIMESTAMP();
		END IF;
		IF	_UPD_nm	IS NULL
		THEN
			SET	_UPD_nm	= CURRENT_USER();
		END IF;
	END IF;

	IF
		_SYSRIGHT = 'DELETE'
	THEN
		IF	_ADD_dm	IS NULL
		THEN
			SET	_ADD_dm	= UTC_TIMESTAMP();
		END IF;
		IF	_ADD_nm	IS NULL
		THEN
			SET	_ADD_nm	= CURRENT_USER();
		END IF;
	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

