DROP PROCEDURE IF EXISTS	`vspContext`
;

DELIMITER //
CREATE PROCEDURE	vspContext
(
	Context_id		int signed		
,	Context_tp		varchar(64)		
,	Context_nm		varchar(256)		
,	Context_cd		varchar(128)		

,	SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspContext
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblContext
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblContext';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'vspContext';
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
			tblContext
		WHERE
				tblContext.Context_id	= Context_id
			AND	tblContext.Context_tp	= Context_tp
			AND	tblContext.Context_nm	= Context_nm

	)
	THEN
		SET IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspContext
		(
			@Context_id	:= Context_id
		,	@Context_tp	:= Context_tp
		,	@Context_nm	:= Context_nm
		,	@Context_cd	:= Context_cd

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
			,	@Table_nm	:= 'tblContext'
			,	@AK		:= ', Context_tp, Context_nm'
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END	VSP
;
###############################################################################
END
//
DELIMITER ;
;

