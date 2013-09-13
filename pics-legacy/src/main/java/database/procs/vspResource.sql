DROP PROCEDURE IF EXISTS	`vspResource`
;

DELIMITER //
CREATE PROCEDURE	vspResource
(
	Resrc_id		INT SIGNED		
,	Resrc_tp		VARCHAR(80)		
,	Resrc_nm		VARCHAR(128)		
,	Resrc_tx		MEDIUMTEXT		
,	OUT ADD_dm		DATETIME		
,	OUT ADD_nm		VARCHAR(128)		
,	OUT UPD_dm		DATETIME		
,	OUT UPD_nm		VARCHAR(128)		
,	OUT DEL_dm		DATETIME		
,	OUT DEL_nm		VARCHAR(128)		

,	SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	IsValid_fg	BOOLEAN
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
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblResource';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'vspResource';
DECLARE	Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE RowExists_fg	TINYINT	DEFAULT 0;
DECLARE ProcFailed_fg	BOOLEAN DEFAULT FALSE;
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
				tblResource.Resrc_id	= Resrc_id
			AND	tblResource.Resrc_tp	= Resrc_tp
			AND	tblResource.Resrc_nm	= Resrc_nm

	)
	THEN
		SET IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspResource
		(
			@Resrc_id	:= Resrc_id
		,	@Resrc_tp	:= Resrc_tp
		,	@Resrc_nm	:= Resrc_nm
		,	@Resrc_tx	:= Resrc_tx
		,	@ADD_dm	:= ADD_dm
		,	@ADD_nm	:= ADD_nm
		,	@UPD_dm	:= UPD_dm
		,	@UPD_nm	:= UPD_nm
		,	@DEL_dm	:= DEL_dm
		,	@DEL_nm	:= DEL_nm

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= Proc_nm
			,	@Table_nm	:= 'tblResource'
			,	@AK		:= ', Resrc_tp, Resrc_nm'
			);
			LEAVE VSP;
		END IF;
	END IF;

	IF
		SYSRIGHT = 'INSERT'
	THEN
		IF	ADD_dm	IS NULL OR ADD_dm = '0000-00-00 00:00:00'
		THEN
			SET	ADD_dm	= UTC_TIMESTAMP();
		END IF;
		IF	ADD_nm	IS NULL
		THEN
			SET	ADD_nm	= CURRENT_USER();
		END IF;
		LEAVE VSP;
	END IF;

	IF
		SYSRIGHT = 'UPDATE'
	THEN
		IF	UPD_dm	IS NULL OR UPD_dm = '0000-00-00 00:00:00'
		THEN
			SET	UPD_dm	= UTC_TIMESTAMP();
		END IF;
		IF	UPD_nm	IS NULL
		THEN
			SET	UPD_nm	= CURRENT_USER();
		END IF;
		LEAVE VSP;
	END IF;

	IF
		SYSRIGHT = 'DELETE'
	THEN
		IF	ADD_dm	IS NULL OR ADD_dm = '0000-00-00 00:00:00'
		THEN
			SET	ADD_dm	= UTC_TIMESTAMP();
		END IF;
		IF	ADD_nm	IS NULL
		THEN
			SET	ADD_nm	= CURRENT_USER();
		END IF;
		LEAVE VSP;
	END IF;
	#######################################################################
END VSP;
###############################################################################
END
//
DELIMITER ;
;

