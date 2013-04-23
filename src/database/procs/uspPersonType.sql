

CREATE PROCEDURE	[dbo].[uspPersonType]
(
	@Person_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK1 
,	@ParentPerson_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@PersonType_tx		udtResrc_tx		= '-2147483647'	OUTPUT
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
**	Name:		uspPersonType
**	Type:		DB API procedure: Update
**	Purpose:	To insert PersonType data into tblPersonType
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
	,	@SYSTABLE	= 'tblPersonType'
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
		,	@Table_nm	= 'tblPersonType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspPersonType]
		@Person_tp	= @Person_tp

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
		,	@KEY1		= @Person_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspPersonType]
		@Person_tp	= @Person_tp	OUTPUT

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
	--
	--	RESTRICT MODE:	tblResourceType
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspResourceType]
			@Resrc_tp	= @Person_tp

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
			,	@KEY1		= @Person_tp


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
			@Resrc_tp	= @Person_tp	OUTPUT
		,	@ParentResrc_tp	= @ParentPerson_tp	OUTPUT
		,	@ResrcType_tx	= @PersonType_tx	OUTPUT
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
	-- THERE WERE NO NON-KEY FIELDS TO UPDATE FOR THIS OBJECT
	-----------------------------------------------------------------------
	-- Insert this transaction in the transaction history log
	-----------------------------------------------------------------------
	-- NO NON-KEY FIELDS IN THIS OBJECT TO RECORD IN HISTORY
	-----------------------------------------------------------------------
	FINISHED:
	-----------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	@STATUS
GO

