DROP PROCEDURE IF EXISTS	`vspItem`
;

DELIMITER //
CREATE PROCEDURE	vspItem
(
	Item_id		int signed		
,	Item_tp		varchar(64)		
,	Item_nm		varchar(256)		
,	Item_cd		varchar(128)		

,	SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspItem
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblItem
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblItem';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'vspItem';
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
			tblItem
		WHERE
				tblItem.Item_id	= Item_id
			AND	tblItem.Item_tp	= Item_tp
			AND	tblItem.Item_nm	= Item_nm

	)
	THEN
		SET IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspItem
		(
			@Item_id	:= Item_id
		,	@Item_tp	:= Item_tp
		,	@Item_nm	:= Item_nm
		,	@Item_cd	:= Item_cd

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
			,	@Table_nm	:= 'tblItem'
			,	@AK		:= ', Item_tp, Item_nm'
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

