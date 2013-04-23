DROP PROCEDURE IF EXISTS	`vspReport`
;

DELIMITER //
CREATE PROCEDURE	vspReport
(
	Report_id		int signed		
,	Report_tp		varchar(80)		
,	Report_nm		varchar(128)		
,	Report_cd		varchar(48)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspReport
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblReport
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblReport';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspReport';
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
			tblReport
		WHERE
				Report_id	= _Report_id
			AND	Report_tp	= _Report_tp
			AND	Report_nm	= _Report_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspReport
		(
			@Report_id	:= _Report_id
		,	@Report_tp	:= _Report_tp
		,	@Report_nm	:= _Report_nm
		,	@Report_cd	:= _Report_cd

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
			,	@Table_nm	:= 'tblReport'
			,	@Key_nm		:= ', Report_tp, Report_nm'
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

