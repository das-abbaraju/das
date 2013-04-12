

CREATE PROCEDURE	[dbo].[uspPerson]
(
	@Person_id		udtResrc_id		=  -2147483647	OUTPUT	-- PK1 
,	@Person_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK2 AK2
,	@Person_nm		udtResrc_nm		= '-2147483647'	OUTPUT	--  AK1
,	@First_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@Middle_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@Last_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@FirstSNDX_cd		udtResrc_cd		= '-2147483647'	OUTPUT
,	@LastSNDX_cd		udtResrc_cd		= '-2147483647'	OUTPUT
,	@Birth_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@Gender_cd		udtResrc_cd		= '-2147483647'	OUTPUT
,	@Person_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ADD_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@ADD_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@UPD_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@UPD_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@DEL_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@DEL_nm		udtResrc_nm		= '-2147483647'	OUTPUT
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
**	Name:		uspPerson
**	Type:		DB API procedure: Update
**	Purpose:	To insert Person data into tblPerson
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
	,	@SYSTABLE	= 'tblPerson'
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
		,	@Table_nm	= 'tblPerson'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspPerson]
		@Person_id	= @Person_id
	,	@Person_tp	= @Person_tp

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
		,	@KEY1		= @Person_id
		,	@KEY2		= @Person_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspPerson]
		@Person_id	= @Person_id	OUTPUT
	,	@Person_tp	= @Person_tp	OUTPUT
	,	@First_nm	= @First_nm	OUTPUT
	,	@Middle_nm	= @Middle_nm	OUTPUT
	,	@Last_nm	= @Last_nm	OUTPUT
	,	@FirstSNDX_cd	= @FirstSNDX_cd	OUTPUT
	,	@LastSNDX_cd	= @LastSNDX_cd	OUTPUT
	,	@Birth_dm	= @Birth_dm	OUTPUT
	,	@Gender_cd	= @Gender_cd	OUTPUT

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
		,	@KEY1		= @Person_tp
		,	@KEY2		= @Person_nm

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check Referential Integerity
	-----------------------------------------------------------------------
	--
	--	RESTRICT MODE:	tblResource
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspResource]
			@Resrc_id	= @Person_id
		,	@Resrc_tp	= @Person_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblResource
		)
		BEGIN
			SET	@STATUS	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFKNotExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= @SYSTABLE
			,	@KEY1		= @Person_id
			,	@KEY2		= @Person_tp


			EXECUTE	@STATUS		= errFailedMode
				@Proc_nm	= @Proc_nm
			,	@Mode_cd	= @Mode_cd
			,	@Action_nm	= @SYSRIGHT
			,	@Table_nm	= 'tblResource'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblResource
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspResource]
			@Resrc_id	= @Person_id	OUTPUT
		,	@Resrc_tp	= @Person_tp	OUTPUT
		,	@Resrc_nm	= @Person_nm	OUTPUT
		,	@Resrc_tx	= @Person_tx	OUTPUT
		,	@ADD_dm	= @ADD_dm	OUTPUT
		,	@ADD_nm	= @ADD_nm	OUTPUT
		,	@UPD_dm	= @UPD_dm	OUTPUT
		,	@UPD_nm	= @UPD_nm	OUTPUT
		,	@DEL_dm	= @DEL_dm	OUTPUT
		,	@DEL_nm	= @DEL_nm	OUTPUT
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
			EXECUTE	@STATUS		= errFailedCall
				@Proc_nm	= @Proc_nm
			,	@CallingProc_nm	= '[dbo].[uspResource]'
			RETURN	@STATUS
		END
	END

	--
	--	RESTRICT MODE:	tblPersonType
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspPersonType]
			@Person_tp	= @Person_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblPersonType
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
			,	@Table_nm	= 'tblPersonType'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblPersonType
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspPersonType]
			@Person_tp	= @Person_tp	OUTPUT
		,	@ParentPerson_tp	= @ParentPerson_tp	OUTPUT
		,	@PersonType_tx	= @PersonType_tx	OUTPUT
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
			,	@CallingProc_nm	= '[dbo].[uspPersonType]'
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
		@First_nm	= '-2147483647'
	AND	@Middle_nm	= '-2147483647'
	AND	@Last_nm	= '-2147483647'
	AND	@FirstSNDX_cd	= '-2147483647'
	AND	@LastSNDX_cd	= '-2147483647'
	AND	@Birth_dm	= '01/01/1754'
	AND	@Gender_cd	= '-2147483647'

	)
		GOTO	FINISHED
	-----------------------------------------------------------------------
	UPDATE
		[dbo].[tblPerson]
	SET
		First_nm		=
		CASE
			WHEN	@First_nm	= '-2147483647'
			THEN	First_nm
			ELSE	@First_nm
		END
	,	Middle_nm		=
		CASE
			WHEN	@Middle_nm	= '-2147483647'
			THEN	Middle_nm
			ELSE	@Middle_nm
		END
	,	Last_nm		=
		CASE
			WHEN	@Last_nm	= '-2147483647'
			THEN	Last_nm
			ELSE	@Last_nm
		END
	,	FirstSNDX_cd		=
		CASE
			WHEN	@FirstSNDX_cd	= '-2147483647'
			THEN	FirstSNDX_cd
			ELSE	@FirstSNDX_cd
		END
	,	LastSNDX_cd		=
		CASE
			WHEN	@LastSNDX_cd	= '-2147483647'
			THEN	LastSNDX_cd
			ELSE	@LastSNDX_cd
		END
	,	Birth_dm		=
		CASE
			WHEN	@Birth_dm	= '01/01/1754'
			THEN	Birth_dm
			ELSE	@Birth_dm
		END
	,	Gender_cd		=
		CASE
			WHEN	@Gender_cd	= '-2147483647'
			THEN	Gender_cd
			ELSE	@Gender_cd
		END

	WHERE
		Person_id	= @Person_id
	AND	Person_tp	= @Person_tp


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

