

CREATE PROCEDURE	[dbo].[gspRole_ResourceType_RightType]
(
	@Role_id		udtResrc_id	=  -2147483647	-- PK1 
,	@Role_tp		udtResrc_tp	= '-2147483647'	-- PK2 AK2
,	@Role_nm		udtResrc_nm	= '-2147483647'	--  AK1
,	@Role_cd		udtResrc_cd	= '-2147483647'
,	@Resrc_tp		udtResrc_tp	= '-2147483647'	-- PK3 AK3
,	@Right_tp		udtResrc_tp	= '-2147483647'	-- PK4 AK4
,	@Role_tx		udtResrc_tx	= '-2147483647'
,	@ParentResrc_tp		udtResrc_tp	= '-2147483647'
,	@ResrcType_tx		udtResrc_tx	= '-2147483647'
,	@ParentRight_tp		udtResrc_tp	= '-2147483647'
,	@RightType_tx		udtResrc_tx	= '-2147483647'

,	@Key_cd			udtKey_cd		= 'PK'	-- Search key code
)
--WITH ENCRYPTION
AS
/*
**	Name:		gspRole_ResourceType_RightType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwRole_ResourceType_RightType
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
	,	@SYSTABLE	= 'vwRole_ResourceType_RightType'
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
			Role_id
		,	Role_tp
		,	Role_nm
		,	Role_cd
		,	Resrc_tp
		,	Right_tp
		,	Role_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwRole_ResourceType_RightType]
		WHERE
			Role_id	= @Role_id
		AND	Role_tp	= @Role_tp
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
			Role_id
		,	Role_tp
		,	Role_nm
		,	Role_cd
		,	Resrc_tp
		,	Right_tp
		,	Role_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwRole_ResourceType_RightType]
		WHERE
			Role_id	= @Role_id
		AND	Role_tp	= @Role_tp

		RETURN	0
	END
	IF
	(
		@Key_cd	= 'FK2'
	)
	BEGIN
		SELECT
			Role_id
		,	Role_tp
		,	Role_nm
		,	Role_cd
		,	Resrc_tp
		,	Right_tp
		,	Role_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwRole_ResourceType_RightType]
		WHERE
			Resrc_tp	= @Resrc_tp

		RETURN	0
	END
	IF
	(
		@Key_cd	= 'FK3'
	)
	BEGIN
		SELECT
			Role_id
		,	Role_tp
		,	Role_nm
		,	Role_cd
		,	Resrc_tp
		,	Right_tp
		,	Role_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwRole_ResourceType_RightType]
		WHERE
			Resrc_tp	= @Resrc_tp
		AND	Right_tp	= @Right_tp

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
			Role_id
		,	Role_tp
		,	Role_nm
		,	Role_cd
		,	Resrc_tp
		,	Right_tp
		,	Role_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwRole_ResourceType_RightType]
		WHERE
			Role_tp	= @Role_tp
		AND	Role_nm	= @Role_nm
		AND	Resrc_tp	= @Resrc_tp
		AND	Right_tp	= @Right_tp

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
			Role_id
		,	Role_tp
		,	Role_nm
		,	Role_cd
		,	Resrc_tp
		,	Right_tp
		,	Role_tx
		,	ParentResrc_tp
		,	ResrcType_tx
		,	ParentRight_tp
		,	RightType_tx
		FROM
			[dbo].[vwRole_ResourceType_RightType]
		WHERE
			(
			Role_id	= @Role_id
		OR	@Role_id	=  -2147483647
			)
		AND	(
			Role_tp	= @Role_tp
		OR	@Role_tp	= '-2147483647'
			)
		AND	(
			Role_nm	= @Role_nm
		OR	@Role_nm	= '-2147483647'
			)
		AND	(
			Role_cd	= @Role_cd
		OR	@Role_cd	= '-2147483647'
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
			Role_tx	LIKE @Role_tx
		OR	@Role_tx	LIKE '-2147483647'
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

