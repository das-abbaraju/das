DROP PROCEDURE IF EXISTS	`vspItemType`
;

DELIMITER //
CREATE PROCEDURE	vspItemType
(
	Item_tp		varchar(64)		

,	SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspItemType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblItemType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblItemType';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'vspItemType';
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
			tblItemType
		WHERE
				tblItemType.Item_tp	= Item_tp

	)
	THEN
		SET IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspItemType
		(
			@Item_tp	:= Item_tp

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
			,	@Table_nm	:= 'tblItemType'
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

