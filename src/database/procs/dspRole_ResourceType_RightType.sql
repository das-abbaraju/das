

CREATE PROCEDURE	[dbo].[dspRole_ResourceType_RightType]
(
	@Role_id		udtResrc_id	= NULL	-- PK1 
,	@Role_tp		udtResrc_tp	= NULL	-- PK2 AK2
,	@Role_nm		udtResrc_nm	= NULL	--  AK1
,	@Role_cd		udtResrc_cd	= NULL
,	@Resrc_tp		udtResrc_tp	= NULL	-- PK3 AK3
,	@Right_tp		udtResrc_tp	= NULL	-- PK4 AK4
,	@Role_tx		udtResrc_tx	= NULL
,	@ParentResrc_tp		udtResrc_tp	= NULL
,	@ResrcType_tx		udtResrc_tx	= NULL
,	@ParentRight_tp		udtResrc_tp	= NULL
,	@RightType_tx		udtResrc_tx	= NULL

,	@CallingProc_nm		udtResrc_nm	= NULL		-- The sproc calling this proc
,	@Source_nm		udtResrc_nm	= NULL	OUTPUT	-- System name
,	@Tran_dm		udtResrc_dm	= NULL	OUTPUT	-- Transaction date (history)
,	@Token_cd		udtResrc_cd	= NULL		-- Security Token
,	@Mode_cd		udtKey_cd	= 'R'		-- Database cascade mode code
)
-- WITH ENCRYPTION
AS
/*
**	Name:		dspRole_ResourceType_RightType
**	Type:		DB API procedure: Delete
**	Purpose:	To delete Role_ResourceType_RightType data From tblRole_ResourceType_RightType
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
DECLARE	@Key_cd		udtKey_cd

DECLARE	@Query_tx	varchar(1000)
-------------------------------------------------------------------------------
BEGIN
	-----------------------------------------------------------------------
	-- Initialize
	-----------------------------------------------------------------------
	SELECT
 		@STATUS		= 0
	,	@RETURN		= 0
	,	@SYSTABLE	= 'tblRole_ResourceType_RightType'
	,	@SYSRIGHT	= 'DELETE'
	,	@Proc_nm	= OBJECT_NAME(@@PROCID)
	,	@Key_cd		= 'PK'
	-----------------------------------------------------------------------
	-- Check Security
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= spSecurityCheck
		@SYSTABLE	= @SYSTABLE
	,	@SYSRIGHT	= @SYSRIGHT
	,	@Token_cd	= @Token_cd

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
	-- Verify Correct Use of Database Mode
	-----------------------------------------------------------------------
	IF
	(
		@Mode_cd	= 'R'
	OR	@Mode_cd	= 'C'
	)
	BEGIN
		SET	@STATUS	= 0
	END
	ELSE
	BEGIN
		EXECUTE	@STATUS		= errFailedMode
			@Proc_nm	= @Proc_nm
		,	@Mode_cd	= @Mode_cd
		,	@Action_nm	= @SYSRIGHT
		,	@Table_nm	= 'tblRole_ResourceType_RightType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Do NOT Delete If No Arguments Sent 
	-----------------------------------------------------------------------
	IF
	(
		@Role_id	IS NULL
	AND	@Role_tp	IS NULL
	AND	@Role_nm	IS NULL
	AND	@Role_cd	IS NULL
	AND	@Resrc_tp	IS NULL
	AND	@Right_tp	IS NULL
	AND	@Role_tx	IS NULL
	AND	@ParentResrc_tp	IS NULL
	AND	@ResrcType_tx	IS NULL
	AND	@ParentRight_tp	IS NULL
	AND	@RightType_tx	IS NULL

	)
	BEGIN
		EXECUTE	@STATUS		= errNoArg
			@Proc_nm	= @Proc_nm
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check Dependent Foreign Key References On Restrict.
	-----------------------------------------------------------------------
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		SET  @Mode_cd    = 'R'
	END
	-----------------------------------------------------------------------
	-- Delete Dependent Foreign Key References On Cascade.
	-----------------------------------------------------------------------
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		SET  @Mode_cd    = 'C'
	END
	-----------------------------------------------------------------------
	-- Insert this transaction in the transaction history log before the
	-- row is deleted (if transaction hisory is turned on)
	-----------------------------------------------------------------------

	-----------------------------------------------------------------------
	-- DELETE
	-----------------------------------------------------------------------
	DELETE
	FROM
		[dbo].[tblRole_ResourceType_RightType]
	WHERE
	(
		Role_id	= @Role_id
	OR	@Role_id	IS NULL
	)
	AND
	(
		Role_tp	= @Role_tp
	OR	@Role_tp	IS NULL
	)
	AND
	(
		Resrc_tp	= @Resrc_tp
	OR	@Resrc_tp	IS NULL
	)
	AND
	(
		Right_tp	= @Right_tp
	OR	@Right_tp	IS NULL
	)


	SET	@ERROR	= @@ERROR
	IF
	(
		@ERROR	<> 0
	)
	BEGIN
		EXECUTE	@STATUS		= errFailedEvent
			@Proc_nm	= @Proc_nm
		,	@Table_nm	= @SYSTABLE
		,	@Action_nm	= @SYSRIGHT
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	@STATUS
GO

