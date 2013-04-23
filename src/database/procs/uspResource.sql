

CREATE PROCEDURE	[dbo].[uspResource]
(
	@Resrc_id		udtResrc_id		=  -2147483647	OUTPUT	-- PK1 
,	@Resrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK2 AK2
,	@Resrc_nm		udtResrc_nm		= '-2147483647'	OUTPUT	--  AK1
,	@Resrc_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ADD_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@ADD_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@UPD_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@UPD_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@DEL_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@DEL_nm		udtResrc_nm		= '-2147483647'	OUTPUT
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
**	Name:		uspResource
**	Type:		DB API procedure: Update
**	Purpose:	To insert Resource data into tblResource
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
	,	@SYSTABLE	= 'tblResource'
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
		,	@Table_nm	= 'tblResource'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspResource]
		@Resrc_id	= @Resrc_id
	,	@Resrc_tp	= @Resrc_tp

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
		,	@KEY1		= @Resrc_id
		,	@KEY2		= @Resrc_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspResource]
		@Resrc_id	= @Resrc_id	OUTPUT
	,	@Resrc_tp	= @Resrc_tp	OUTPUT
	,	@Resrc_nm	= @Resrc_nm	OUTPUT
	,	@Resrc_tx	= @Resrc_tx	OUTPUT
	,	@ADD_dm	= @ADD_dm	OUTPUT
	,	@ADD_nm	= @ADD_nm	OUTPUT
	,	@UPD_dm	= @UPD_dm	OUTPUT
	,	@UPD_nm	= @UPD_nm	OUTPUT
	,	@DEL_dm	= @DEL_dm	OUTPUT
	,	@DEL_nm	= @DEL_nm	OUTPUT

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
		,	@KEY1		= @Resrc_tp
		,	@KEY2		= @Resrc_nm

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check Referential Integerity
	-----------------------------------------------------------------------
	--
	--	RESTRICT MODE:	tblResourceType
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspResourceType]
			@Resrc_tp	= @Resrc_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblResourceType
		)
		BEGIN
			SET	@STATUS	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFKNotExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= @SYSTABLE
			,	@KEY1		= @Resrc_tp


			EXECUTE	@STATUS		= errFailedMode
				@Proc_nm	= @Proc_nm
			,	@Mode_cd	= @Mode_cd
			,	@Action_nm	= @SYSRIGHT
			,	@Table_nm	= 'tblResourceType'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblResourceType
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspResourceType]
			@Resrc_tp	= @Resrc_tp	OUTPUT
		,	@ParentResrc_tp	= @ParentResrc_tp	OUTPUT
		,	@ResrcType_tx	= @ResrcType_tx	OUTPUT
		,	@Left_id	= @Left_id	OUTPUT
		,	@Right_id	= @Right_id	OUTPUT
		,	@Level_id	= @Level_id	OUTPUT
		,	@Order_id	= @Order_id	OUTPUT

		,	@CallingProc_nm	= @CallingProc_nm
		,	@Source_nm	= @Source_nm	OUTPUT
		,	@Tran_dm	= @Tran_dm	OUTPUT
		,	@Token_cd	= @Token_cd
		,	@Mode_cd	= 'R'	-- This Table Is Restricted and Does Not Allow A Cascade From an FK Table.

		IF
		(
			@RETURN	= 0
		)
		BEGIN
			SET	@STATUS	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFailedCall
				@Proc_nm	= @Proc_nm
			,	@CallingProc_nm	= '[dbo].[uspResourceType]'
			RETURN	@STATUS
		END
	END


	-----------------------------------------------------------------------
	-- Update NON-KEY values
	-----------------------------------------------------------------------
	-----------------------------------------------------------------------
	-- Invoke an UPDATE only when a non-key attribute is actually modified.
	-----------------------------------------------------------------------
	IF
	(
		@Resrc_nm	= '-2147483647'
	AND	@Resrc_tx	LIKE '-2147483647'
	AND	@ADD_dm	= '01/01/1754'
	AND	@ADD_nm	= '-2147483647'
	AND	@UPD_dm	= '01/01/1754'
	AND	@UPD_nm	= '-2147483647'
	AND	@DEL_dm	= '01/01/1754'
	AND	@DEL_nm	= '-2147483647'

	)
		GOTO	FINISHED
	-----------------------------------------------------------------------
	UPDATE
		[dbo].[tblResource]
	SET
		Resrc_nm		=
		CASE
			WHEN	@Resrc_nm	= '-2147483647'
			THEN	Resrc_nm
			ELSE	@Resrc_nm
		END
	,	Resrc_tx		=
		CASE
			WHEN	@Resrc_tx	LIKE '-2147483647'
			THEN	Resrc_tx
			ELSE	@Resrc_tx
		END
	,	ADD_dm		=
		CASE
			WHEN	@ADD_dm	= '01/01/1754'
			THEN	ADD_dm
			ELSE	@ADD_dm
		END
	,	ADD_nm		=
		CASE
			WHEN	@ADD_nm	= '-2147483647'
			THEN	ADD_nm
			ELSE	@ADD_nm
		END
	,	UPD_dm		=
		CASE
			WHEN	@UPD_dm	= '01/01/1754'
			THEN	UPD_dm
			ELSE	@UPD_dm
		END
	,	UPD_nm		=
		CASE
			WHEN	@UPD_nm	= '-2147483647'
			THEN	UPD_nm
			ELSE	@UPD_nm
		END
	,	DEL_dm		=
		CASE
			WHEN	@DEL_dm	= '01/01/1754'
			THEN	DEL_dm
			ELSE	@DEL_dm
		END
	,	DEL_nm		=
		CASE
			WHEN	@DEL_nm	= '-2147483647'
			THEN	DEL_nm
			ELSE	@DEL_nm
		END

	WHERE
		Resrc_id	= @Resrc_id
	AND	Resrc_tp	= @Resrc_tp


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

