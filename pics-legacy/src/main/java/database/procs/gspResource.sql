

CREATE PROCEDURE	[dbo].[gspResource]
(
	@Resrc_id		udtResrc_id	=  -2147483647	-- PK1 
,	@Resrc_tp		udtResrc_tp	= '-2147483647'	-- PK2 AK2
,	@Resrc_nm		udtResrc_nm	= '-2147483647'	--  AK1
,	@Resrc_tx		udtResrc_tx	= '-2147483647'
,	@ADD_dm		udtResrc_dm	= '01/01/1754'
,	@ADD_nm		udtResrc_nm	= '-2147483647'
,	@UPD_dm		udtResrc_dm	= '01/01/1754'
,	@UPD_nm		udtResrc_nm	= '-2147483647'
,	@DEL_dm		udtResrc_dm	= '01/01/1754'
,	@DEL_nm		udtResrc_nm	= '-2147483647'
,	@ParentResrc_tp		udtResrc_tp	= '-2147483647'
,	@ResrcType_tx		udtResrc_tx	= '-2147483647'
,	@Left_id		udtResrc_id	=  -2147483647
,	@Right_id		udtResrc_id	=  -2147483647
,	@Level_id		udtResrc_id	=  -2147483647
,	@Order_id		udtResrc_id	=  -2147483647

,	@Key_cd			udtKey_cd		= 'PK'	-- Search key code
)
--WITH ENCRYPTION
AS
/*
**	Name:		gspResource
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwResource
**	Author:		Solomon S. Shacter
**	Company:	DataLabs, Inc. Copyright 2006. All Rights Reserved
**
**	Modified:	3/21/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
-------------------------------------------------------------------------------
SET	NOCOUNT	ON
-------------------------------------------------------------------------------
DECLARE	@RETURN		int
DECLARE	@STATUS		int
DECLARE	@ERROR		int
DECLARE	@SYSTABLE	varchar(255)
DECLARE	@SYSRIGHT	varchar(40)
DECLARE	@Proc_nm	varchar(255)
-------------------------------------------------------------------------------
BEGIN
	-----------------------------------------------------------------------
	-- Initialize
	-----------------------------------------------------------------------
	SELECT
 		@STATUS		= 0
	,	@RETURN		= 0
	,	@SYSTABLE	= 'vwResource'
	,	@SYSRIGHT	= 'SELECT'
	,	@Proc_nm	= OBJECT_NAME(@@PROCID)
	-----------------------------------------------------------------------
	-- Check Security
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= spSecurityCheck
		@SYSTABLE	= @SYSTABLE
	,	@SYSRIGHT	= @SYSRIGHT

	IF
	(
		@RETURN	<> 0
	)
	BEGIN
		EXECUTE	@STATUS		= errFailedSecurity
			@Proc_nm	= @Proc_nm
		,	@Table_nm	= @SYSTABLE
		,	@Action_nm	= @SYSRIGHT
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Primary Key lookup
	-----------------------------------------------------------------------
	IF
	(
		@Key_cd = 'PK'
	)
	BEGIN
		SELECT
			Resrc_id
		,	Resrc_tp
		,	Resrc_nm
		,	Resrc_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentResrc_tp
		,	ResrcType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwResource]
		WHERE
			Resrc_id	= @Resrc_id
		AND	Resrc_tp	= @Resrc_tp

		RETURN	0
	END
	-----------------------------------------------------------------------
	-- Foreign Key lookup
	-----------------------------------------------------------------------
	IF
	(
		@Key_cd	= 'FK1'
	)
	BEGIN
		SELECT
			Resrc_id
		,	Resrc_tp
		,	Resrc_nm
		,	Resrc_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentResrc_tp
		,	ResrcType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwResource]
		WHERE
			Resrc_tp	= @Resrc_tp

		RETURN	0
	END

	-----------------------------------------------------------------------
	-- Alternate Key lookup
	-----------------------------------------------------------------------
	IF
	(
		@Key_cd	= 'AK'
	)
	BEGIN
		SELECT
			Resrc_id
		,	Resrc_tp
		,	Resrc_nm
		,	Resrc_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentResrc_tp
		,	ResrcType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwResource]
		WHERE
			Resrc_tp	= @Resrc_tp
		AND	Resrc_nm	= @Resrc_nm

		RETURN	0
	END
	-----------------------------------------------------------------------
	-- Search Key lookup
	-----------------------------------------------------------------------
	--   NO UI SEARCH KEY(S) DEFINED FOR THIS OBJECT
	-----------------------------------------------------------------------
	-- Attribute lookup
	-----------------------------------------------------------------------
	IF
	(
		@Key_cd	= 'AL'
	)
	BEGIN
		SELECT
			Resrc_id
		,	Resrc_tp
		,	Resrc_nm
		,	Resrc_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentResrc_tp
		,	ResrcType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwResource]
		WHERE
			(
			Resrc_id	= @Resrc_id
		OR	@Resrc_id	=  -2147483647
			)
		AND	(
			Resrc_tp	= @Resrc_tp
		OR	@Resrc_tp	= '-2147483647'
			)
		AND	(
			Resrc_nm	= @Resrc_nm
		OR	@Resrc_nm	= '-2147483647'
			)
		AND	(
			Resrc_tx	LIKE @Resrc_tx
		OR	@Resrc_tx	LIKE '-2147483647'
			)
		AND	(
			ADD_dm	= @ADD_dm
		OR	@ADD_dm	= '01/01/1754'
			)
		AND	(
			ADD_nm	= @ADD_nm
		OR	@ADD_nm	= '-2147483647'
			)
		AND	(
			UPD_dm	= @UPD_dm
		OR	@UPD_dm	= '01/01/1754'
			)
		AND	(
			UPD_nm	= @UPD_nm
		OR	@UPD_nm	= '-2147483647'
			)
		AND	(
			DEL_dm	= @DEL_dm
		OR	@DEL_dm	= '01/01/1754'
			)
		AND	(
			DEL_nm	= @DEL_nm
		OR	@DEL_nm	= '-2147483647'
			)
		AND	(
			ParentResrc_tp	= @ParentResrc_tp
		OR	@ParentResrc_tp	= '-2147483647'
			)
		AND	(
			ResrcType_tx	LIKE @ResrcType_tx
		OR	@ResrcType_tx	LIKE '-2147483647'
			)
		AND	(
			Left_id	= @Left_id
		OR	@Left_id	=  -2147483647
			)
		AND	(
			Right_id	= @Right_id
		OR	@Right_id	=  -2147483647
			)
		AND	(
			Level_id	= @Level_id
		OR	@Level_id	=  -2147483647
			)
		AND	(
			Order_id	= @Order_id
		OR	@Order_id	=  -2147483647
			)

		RETURN	0
	END
	-----------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	0
GO

