

CREATE PROCEDURE	[dbo].[gspResourceType]
(
	@Resrc_tp		udtResrc_tp	= '-2147483647'	-- PK1 
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
**	Name:		gspResourceType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwResourceType
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
	,	@SYSTABLE	= 'vwResourceType'
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
			Resrc_tp
		,	ParentResrc_tp
		,	ResrcType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwResourceType]
		WHERE
			Resrc_tp	= @Resrc_tp

		RETURN	0
	END
	-----------------------------------------------------------------------
	-- Foreign Key lookup
	-----------------------------------------------------------------------
	     --   NO SUPER-SET OR PARENT TABLE FOR THIS OBJECT TO REFERENCE
	-----------------------------------------------------------------------
	-- Alternate Key lookup
	-----------------------------------------------------------------------
	-- NO ALTERNATE KEY DEFINED FOR THIS OBJECT
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
			Resrc_tp
		,	ParentResrc_tp
		,	ResrcType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwResourceType]
		WHERE
			(
			Resrc_tp	= @Resrc_tp
		OR	@Resrc_tp	= '-2147483647'
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

