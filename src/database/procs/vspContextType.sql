DROP PROCEDURE IF EXISTS	`vspContextType`
;

DELIMITER //
CREATE PROCEDURE	vspContextType
(
	Context_tp		varchar(64)		

,	SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspContextType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblContextType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblContextType';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'vspContextType';
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
			tblContextType
		WHERE
				tblContextType.Context_tp	= Context_tp

	)
	THEN
		SET IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspContextType
		(
			@Context_tp	:= Context_tp

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
			,	@Table_nm	:= 'tblContextType'
			,	@AK		:= ''
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

