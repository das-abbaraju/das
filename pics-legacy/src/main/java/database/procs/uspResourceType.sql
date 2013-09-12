

CREATE PROCEDURE	[dbo].[uspResourceType]
(
	@Resrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK1 
,	@ParentResrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@ResrcType_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@Left_id		udtResrc_id		=  -2147483647	OUTPUT
,	@Right_id		udtResrc_id		=  -2147483647	OUTPUT
,	@Level_id		udtResrc_id		=  -2147483647	OUTPUT
,	@Order_id		udtResrc_id		=  -2147483647	OUTPUT

,	@CallingProc_nm		udtResrc_nm	= NULL		-- The sproc calling this proc
,	@Source_nm		udtResrc_nm	= NULL	OUTPUT	-- System name
,	@Tran_dm		udtResrc_dm	= NULL	OUTPUT	-- Transaction date (history)
,	@Token_cd		udtResrc_cd	= NULL		-- Security Token
,	@Mode_cd		udtKey_cd	= 'R'		-- Database cascade mode code
)
-- WITH ENCRYPTION
AS
/*
**	Name:		uspResourceType
**	Type:		DB API procedure: Update
**	Purpose:	To insert ResourceType data into tblResourceType
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
	,	@SYSTABLE	= 'tblResourceType'
	,	@SYSRIGHT	= 'UPDATE'
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
		@RETURN	= 0
	)
	BEGIN
		SET	@STATUS	= 0
	END
	ELSE
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
		,	@Table_nm	= 'tblResourceType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspResourceType]
		@Resrc_tp	= @Resrc_tp

	,	@Key_cd		= @Key_cd

	IF
	(
		@RETURN	= 1	-- PK row was found for update
	)
	BEGIN
		SET	@STATUS	= 0
	END
	ELSE
	BEGIN
		EXECUTE	@STATUS		= errPKNotExist
			@Proc_nm	= @Proc_nm
		,	@Table_nm	= @Systable
		,	@KEY1		= @Resrc_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspResourceType]
		@Resrc_tp	= @Resrc_tp	OUTPUT
	,	@ParentResrc_tp	= @ParentResrc_tp	OUTPUT
	,	@ResrcType_tx	= @ResrcType_tx	OUTPUT
	,	@Left_id	= @Left_id	OUTPUT
	,	@Right_id	= @Right_id	OUTPUT
	,	@Level_id	= @Level_id	OUTPUT
	,	@Order_id	= @Order_id	OUTPUT

	,	@SYSRIGHT	= @SYSRIGHT
	,	@Mode_cd	= @Mode_cd

	IF
	(
		@RETURN	= 0
	)
	BEGIN
		SET	@STATUS	= 0
	END
	ELSE
	BEGIN
		EXECUTE	@STATUS		= errAKExist
			@Proc_nm	= @Proc_nm
		,	@Table_nm	= @Systable

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check Referential Integerity
	-----------------------------------------------------------------------
	--   NO SUPER-SET OR PARENT FK TABLE FOR THIS OBJECT
	-----------------------------------------------------------------------
	-- Update NON-KEY values
	-----------------------------------------------------------------------
	-----------------------------------------------------------------------
	-- Invoke an UPDATE only when a non-key attribute is actually modified.
	-----------------------------------------------------------------------
	IF
	(
		@ParentResrc_tp	= '-2147483647'
	AND	@ResrcType_tx	LIKE '-2147483647'
	AND	@Left_id	=  -2147483647
	AND	@Right_id	=  -2147483647
	AND	@Level_id	=  -2147483647
	AND	@Order_id	=  -2147483647

	)
		GOTO	FINISHED
	-----------------------------------------------------------------------
	UPDATE
		[dbo].[tblResourceType]
	SET
		ParentResrc_tp		=
		CASE
			WHEN	@ParentResrc_tp	= '-2147483647'
			THEN	ParentResrc_tp
			ELSE	@ParentResrc_tp
		END
	,	ResrcType_tx		=
		CASE
			WHEN	@ResrcType_tx	LIKE '-2147483647'
			THEN	ResrcType_tx
			ELSE	@ResrcType_tx
		END
	,	Left_id		=
		CASE
			WHEN	@Left_id	=  -2147483647
			THEN	Left_id
			ELSE	@Left_id
		END
	,	Right_id		=
		CASE
			WHEN	@Right_id	=  -2147483647
			THEN	Right_id
			ELSE	@Right_id
		END
	,	Level_id		=
		CASE
			WHEN	@Level_id	=  -2147483647
			THEN	Level_id
			ELSE	@Level_id
		END
	,	Order_id		=
		CASE
			WHEN	@Order_id	=  -2147483647
			THEN	Order_id
			ELSE	@Order_id
		END

	WHERE
		Resrc_tp	= @Resrc_tp


	SET	@ERROR	= @@ERROR
	IF
	(
		@ERROR	<> 0
	)
	BEGIN
		EXECUTE	@STATUS		= errFailedEvent
			@Proc_nm	= @Proc_nm
		,	@Table_nm	= @Systable
		,	@Action_nm	= @Sysright
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Insert this transaction in the transaction history log
	-----------------------------------------------------------------------

	-----------------------------------------------------------------------
	FINISHED:
	-----------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	@STATUS
GO

