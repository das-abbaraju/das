

CREATE PROCEDURE	[dbo].[gspResource_RightType]
(
	@Resrc_id		udtResrc_id	=  -2147483647	-- PK1 
,	@Resrc_tp		udtResrc_tp	= '-2147483647'	-- PK2 AK2
,	@Right_tp		udtResrc_tp	= '-2147483647'	-- PK3 AK3
,	@Resrc_nm		udtResrc_nm	= '-2147483647'	--  AK1
,	@Resrc_tx		udtResrc_tx	= '-2147483647'
,	@ParentResrc_tp		udtResrc_tp	= '-2147483647'
,	@ResrcType_tx		udtResrc_tx	= '-2147483647'
,	@ParentRight_tp		udtResrc_tp	= '-2147483647'
,	@RightType_tx		udtResrc_tx	= '-2147483647'

,	@Key_cd			udtKey_cd		= 'PK'	-- Search key code
)
--WITH ENCRYPTION
AS
/*
**	Name:		gspResource_RightType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwResource_RightType
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
	,	@SYSTABLE	= 'vwResource_RightType'
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
		,	Right_tp
		,	Resrc_nm
		,	Resrc_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwResource_RightType]
		WHERE
			Resrc_id	= @Resrc_id
		AND	Resrc_tp	= @Resrc_tp
		AND	Right_tp	= @Right_tp

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
		,	Right_tp
		,	Resrc_nm
		,	Resrc_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwResource_RightType]
		WHERE
			Resrc_id	= @Resrc_id
		AND	Resrc_tp	= @Resrc_tp

		RETURN	0
	END
	IF
	(
		@Key_cd	= 'FK2'
	)
	BEGIN
		SELECT
			Resrc_id
		,	Resrc_tp
		,	Right_tp
		,	Resrc_nm
		,	Resrc_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwResource_RightType]
		WHERE
			Right_tp	= @Right_tp

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
		,	Right_tp
		,	Resrc_nm
		,	Resrc_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwResource_RightType]
		WHERE
			Resrc_tp	= @Resrc_tp
		AND	Right_tp	= @Right_tp
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
		,	Right_tp
		,	Resrc_nm
		,	Resrc_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwResource_RightType]
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
			Right_tp	= @Right_tp
		OR	@Right_tp	= '-2147483647'
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
			ParentResrc_tp	= @ParentResrc_tp
		OR	@ParentResrc_tp	= '-2147483647'
			)
		AND	(
			ResrcType_tx	LIKE @ResrcType_tx
		OR	@ResrcType_tx	LIKE '-2147483647'
			)
		AND	(
			ParentRight_tp	= @ParentRight_tp
		OR	@ParentRight_tp	= '-2147483647'
			)
		AND	(
			RightType_tx	LIKE @RightType_tx
		OR	@RightType_tx	LIKE '-2147483647'
			)

		RETURN	0
	END
	-----------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	0
GO

