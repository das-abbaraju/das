DROP PROCEDURE IF EXISTS	`vspItem_Locale`
;

DELIMITER //
CREATE PROCEDURE	vspItem_Locale
(
	Item_id		int signed		
,	Item_tp		varchar(64)		
,	Locale_cd		varchar(128)		
,	Entry_tp		varchar(64)		
,	Entry_tx		text		
,	EFF_dm		datetime		
,	USE_dm		datetime		

,	SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspItem_Locale
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblItem_Locale
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'tblItem_Locale';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'vspItem_Locale';
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
			tblItem_Locale
		WHERE
				tblItem_Locale.Item_id	= Item_id
			AND	tblItem_Locale.Item_tp	= Item_tp
			AND	tblItem_Locale.Locale_cd	= Locale_cd

	)
	THEN
		SET IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspItem_Locale
		(
			@Item_id	:= Item_id
		,	@Item_tp	:= Item_tp
		,	@Locale_cd	:= Locale_cd
		,	@Entry_tp	:= Entry_tp
		,	@Entry_tx	:= Entry_tx
		,	@EFF_dm	:= EFF_dm
		,	@USE_dm	:= USE_dm

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
			,	@Table_nm	:= 'tblItem_Locale'
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

