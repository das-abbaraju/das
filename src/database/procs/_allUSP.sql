

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


CREATE PROCEDURE	[dbo].[uspResource_RightType]
(
	@Resrc_id		udtResrc_id		=  -2147483647	OUTPUT	-- PK1 
,	@Resrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK2 AK2
,	@Right_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK3 AK3
,	@Resrc_nm		udtResrc_nm		= '-2147483647'	OUTPUT	--  AK1
,	@Resrc_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ParentResrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@ResrcType_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ParentRight_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@RightType_tx		udtResrc_tx		= '-2147483647'	OUTPUT

,	@CallingProc_nm		udtResrc_nm	= NULL		-- The sproc calling this proc
,	@Source_nm		udtResrc_nm	= NULL	OUTPUT	-- System name
,	@Tran_dm		udtResrc_dm	= NULL	OUTPUT	-- Transaction date (history)
,	@Token_cd		udtResrc_cd	= NULL		-- Security Token
,	@Mode_cd		udtKey_cd	= 'R'		-- Database cascade mode code
)
-- WITH ENCRYPTION
AS
/*
**	Name:		uspResource_RightType
**	Type:		DB API procedure: Update
**	Purpose:	To insert Resource_RightType data into tblResource_RightType
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
	,	@SYSTABLE	= 'tblResource_RightType'
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
		,	@Table_nm	= 'tblResource_RightType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspResource_RightType]
		@Resrc_id	= @Resrc_id
	,	@Resrc_tp	= @Resrc_tp
	,	@Right_tp	= @Right_tp

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
		,	@KEY3		= @Right_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspResource_RightType]
		@Resrc_id	= @Resrc_id	OUTPUT
	,	@Resrc_tp	= @Resrc_tp	OUTPUT
	,	@Right_tp	= @Right_tp	OUTPUT

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
		,	@KEY2		= @Right_tp
		,	@KEY3		= @Resrc_nm

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
			@Resrc_id	= @Resrc_id
		,	@Resrc_tp	= @Resrc_tp

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
			,	@KEY1		= @Resrc_id
			,	@KEY2		= @Resrc_tp


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
			@Resrc_id	= @Resrc_id	OUTPUT
		,	@Resrc_tp	= @Resrc_tp	OUTPUT
		,	@Resrc_nm	= @Resrc_nm	OUTPUT
		,	@Resrc_tx	= @Resrc_tx	OUTPUT
		,	@ParentResrc_tp	= @ParentResrc_tp	OUTPUT
		,	@ResrcType_tx	= @ResrcType_tx	OUTPUT

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
	--	RESTRICT MODE:	tblRightType
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspRightType]
			@Right_tp	= @Right_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblRightType
		)
		BEGIN
			SET	@STATUS	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFKNotExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= @SYSTABLE
			,	@KEY1		= @Right_tp


			EXECUTE	@STATUS		= errFailedMode
				@Proc_nm	= @Proc_nm
			,	@Mode_cd	= @Mode_cd
			,	@Action_nm	= @SYSRIGHT
			,	@Table_nm	= 'tblRightType'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblRightType
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspRightType]
			@Right_tp	= @Right_tp	OUTPUT
		,	@ParentRight_tp	= @ParentRight_tp	OUTPUT
		,	@RightType_tx	= @RightType_tx	OUTPUT

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
			,	@CallingProc_nm	= '[dbo].[uspRightType]'
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


CREATE PROCEDURE	[dbo].[uspResourceType_RightType]
(
	@Resrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK1 
,	@Right_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK2 
,	@ParentResrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@ResrcType_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ParentRight_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@RightType_tx		udtResrc_tx		= '-2147483647'	OUTPUT

,	@CallingProc_nm		udtResrc_nm	= NULL		-- The sproc calling this proc
,	@Source_nm		udtResrc_nm	= NULL	OUTPUT	-- System name
,	@Tran_dm		udtResrc_dm	= NULL	OUTPUT	-- Transaction date (history)
,	@Token_cd		udtResrc_cd	= NULL		-- Security Token
,	@Mode_cd		udtKey_cd	= 'R'		-- Database cascade mode code
)
-- WITH ENCRYPTION
AS
/*
**	Name:		uspResourceType_RightType
**	Type:		DB API procedure: Update
**	Purpose:	To insert ResourceType_RightType data into tblResourceType_RightType
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
	,	@SYSTABLE	= 'tblResourceType_RightType'
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
		,	@Table_nm	= 'tblResourceType_RightType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspResourceType_RightType]
		@Resrc_tp	= @Resrc_tp
	,	@Right_tp	= @Right_tp

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
		,	@KEY2		= @Right_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspResourceType_RightType]
		@Resrc_tp	= @Resrc_tp	OUTPUT
	,	@Right_tp	= @Right_tp	OUTPUT

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

	--
	--	RESTRICT MODE:	tblRightType
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspRightType]
			@Right_tp	= @Right_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblRightType
		)
		BEGIN
			SET	@STATUS	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFKNotExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= @SYSTABLE
			,	@KEY1		= @Right_tp


			EXECUTE	@STATUS		= errFailedMode
				@Proc_nm	= @Proc_nm
			,	@Mode_cd	= @Mode_cd
			,	@Action_nm	= @SYSRIGHT
			,	@Table_nm	= 'tblRightType'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblRightType
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspRightType]
			@Right_tp	= @Right_tp	OUTPUT
		,	@ParentRight_tp	= @ParentRight_tp	OUTPUT
		,	@RightType_tx	= @RightType_tx	OUTPUT

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
			,	@CallingProc_nm	= '[dbo].[uspRightType]'
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


CREATE PROCEDURE	[dbo].[uspRightType]
(
	@Right_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK1 
,	@ParentRight_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@RightType_tx		udtResrc_tx		= '-2147483647'	OUTPUT
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
**	Name:		uspRightType
**	Type:		DB API procedure: Update
**	Purpose:	To insert RightType data into tblRightType
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
	,	@SYSTABLE	= 'tblRightType'
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
		,	@Table_nm	= 'tblRightType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspRightType]
		@Right_tp	= @Right_tp

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
		,	@KEY1		= @Right_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspRightType]
		@Right_tp	= @Right_tp	OUTPUT

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
			@Resrc_tp	= @Right_tp

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
			,	@KEY1		= @Right_tp


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
			@Resrc_tp	= @Right_tp	OUTPUT
		,	@ParentResrc_tp	= @ParentRight_tp	OUTPUT
		,	@ResrcType_tx	= @RightType_tx	OUTPUT
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


CREATE PROCEDURE	[dbo].[uspRole]
(
	@Role_id		udtResrc_id		=  -2147483647	OUTPUT	-- PK1 
,	@Role_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK2 AK2
,	@Role_nm		udtResrc_nm		= '-2147483647'	OUTPUT	--  AK1
,	@Role_cd		udtResrc_cd		= '-2147483647'	OUTPUT
,	@Role_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ADD_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@ADD_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@UPD_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@UPD_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@DEL_dm		udtResrc_dm		= '01/01/1754'	OUTPUT
,	@DEL_nm		udtResrc_nm		= '-2147483647'	OUTPUT
,	@ParentRole_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@RoleType_tx		udtResrc_tx		= '-2147483647'	OUTPUT
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
**	Name:		uspRole
**	Type:		DB API procedure: Update
**	Purpose:	To insert Role data into tblRole
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
	,	@SYSTABLE	= 'tblRole'
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
		,	@Table_nm	= 'tblRole'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspRole]
		@Role_id	= @Role_id
	,	@Role_tp	= @Role_tp

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
		,	@KEY1		= @Role_id
		,	@KEY2		= @Role_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspRole]
		@Role_id	= @Role_id	OUTPUT
	,	@Role_tp	= @Role_tp	OUTPUT
	,	@Role_cd	= @Role_cd	OUTPUT

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
		,	@KEY1		= @Role_tp
		,	@KEY2		= @Role_nm

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
			@Resrc_id	= @Role_id
		,	@Resrc_tp	= @Role_tp

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
			,	@KEY1		= @Role_id
			,	@KEY2		= @Role_tp


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
			@Resrc_id	= @Role_id	OUTPUT
		,	@Resrc_tp	= @Role_tp	OUTPUT
		,	@Resrc_nm	= @Role_nm	OUTPUT
		,	@Resrc_tx	= @Role_tx	OUTPUT
		,	@ADD_dm	= @ADD_dm	OUTPUT
		,	@ADD_nm	= @ADD_nm	OUTPUT
		,	@UPD_dm	= @UPD_dm	OUTPUT
		,	@UPD_nm	= @UPD_nm	OUTPUT
		,	@DEL_dm	= @DEL_dm	OUTPUT
		,	@DEL_nm	= @DEL_nm	OUTPUT
		,	@ParentResrc_tp	= @ParentRole_tp	OUTPUT
		,	@ResrcType_tx	= @RoleType_tx	OUTPUT
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
	--	RESTRICT MODE:	tblRoleType
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspRoleType]
			@Role_tp	= @Role_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblRoleType
		)
		BEGIN
			SET	@STATUS	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFKNotExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= @SYSTABLE
			,	@KEY1		= @Role_tp


			EXECUTE	@STATUS		= errFailedMode
				@Proc_nm	= @Proc_nm
			,	@Mode_cd	= @Mode_cd
			,	@Action_nm	= @SYSRIGHT
			,	@Table_nm	= 'tblRoleType'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblRoleType
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspRoleType]
			@Role_tp	= @Role_tp	OUTPUT
		,	@ParentRole_tp	= @ParentRole_tp	OUTPUT
		,	@RoleType_tx	= @RoleType_tx	OUTPUT
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
			,	@CallingProc_nm	= '[dbo].[uspRoleType]'
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
		@Role_cd	= '-2147483647'

	)
		GOTO	FINISHED
	-----------------------------------------------------------------------
	UPDATE
		[dbo].[tblRole]
	SET
		Role_cd		=
		CASE
			WHEN	@Role_cd	= '-2147483647'
			THEN	Role_cd
			ELSE	@Role_cd
		END

	WHERE
		Role_id	= @Role_id
	AND	Role_tp	= @Role_tp


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


CREATE PROCEDURE	[dbo].[uspRole_Resource_RightType]
(
	@Role_id		udtResrc_id		=  -2147483647	OUTPUT	-- PK1 
,	@Role_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK2 AK2
,	@Role_nm		udtResrc_nm		= '-2147483647'	OUTPUT	--  AK1
,	@Role_cd		udtResrc_cd		= '-2147483647'	OUTPUT
,	@Resrc_id		udtResrc_id		=  -2147483647	OUTPUT	-- PK3 AK3
,	@Resrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK4 AK4
,	@Resrc_nm		udtResrc_nm		= '-2147483647'	OUTPUT	--  AK6
,	@Right_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK5 AK5
,	@Role_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@Resrc_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ParentResrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@ResrcType_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ParentRight_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@RightType_tx		udtResrc_tx		= '-2147483647'	OUTPUT

,	@CallingProc_nm		udtResrc_nm	= NULL		-- The sproc calling this proc
,	@Source_nm		udtResrc_nm	= NULL	OUTPUT	-- System name
,	@Tran_dm		udtResrc_dm	= NULL	OUTPUT	-- Transaction date (history)
,	@Token_cd		udtResrc_cd	= NULL		-- Security Token
,	@Mode_cd		udtKey_cd	= 'R'		-- Database cascade mode code
)
-- WITH ENCRYPTION
AS
/*
**	Name:		uspRole_Resource_RightType
**	Type:		DB API procedure: Update
**	Purpose:	To insert Role_Resource_RightType data into tblRole_Resource_RightType
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
	,	@SYSTABLE	= 'tblRole_Resource_RightType'
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
		,	@Table_nm	= 'tblRole_Resource_RightType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspRole_Resource_RightType]
		@Role_id	= @Role_id
	,	@Role_tp	= @Role_tp
	,	@Resrc_id	= @Resrc_id
	,	@Resrc_tp	= @Resrc_tp
	,	@Right_tp	= @Right_tp

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
		,	@KEY1		= @Role_id
		,	@KEY2		= @Role_tp
		,	@KEY3		= @Resrc_id
		,	@KEY4		= @Resrc_tp
		,	@KEY5		= @Right_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspRole_Resource_RightType]
		@Role_id	= @Role_id	OUTPUT
	,	@Role_tp	= @Role_tp	OUTPUT
	,	@Resrc_id	= @Resrc_id	OUTPUT
	,	@Resrc_tp	= @Resrc_tp	OUTPUT
	,	@Right_tp	= @Right_tp	OUTPUT

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
		,	@KEY1		= @Role_tp
		,	@KEY2		= @Role_nm
		,	@KEY3		= @Resrc_id
		,	@KEY4		= @Resrc_tp
		,	@KEY5		= @Resrc_nm
		,	@KEY6		= @Right_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check Referential Integerity
	-----------------------------------------------------------------------
	--
	--	RESTRICT MODE:	tblRole
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspRole]
			@Role_id	= @Role_id
		,	@Role_tp	= @Role_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblRole
		)
		BEGIN
			SET	@STATUS	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFKNotExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= @SYSTABLE
			,	@KEY1		= @Role_id
			,	@KEY2		= @Role_tp


			EXECUTE	@STATUS		= errFailedMode
				@Proc_nm	= @Proc_nm
			,	@Mode_cd	= @Mode_cd
			,	@Action_nm	= @SYSRIGHT
			,	@Table_nm	= 'tblRole'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblRole
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspRole]
			@Role_id	= @Role_id	OUTPUT
		,	@Role_tp	= @Role_tp	OUTPUT
		,	@Role_nm	= @Role_nm	OUTPUT
		,	@Role_cd	= @Role_cd	OUTPUT
		,	@Role_tx	= @Role_tx	OUTPUT

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
			,	@CallingProc_nm	= '[dbo].[uspRole]'
			RETURN	@STATUS
		END
	END

	--
	--	RESTRICT MODE:	tblResource
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspResource]
			@Resrc_id	= @Resrc_id
		,	@Resrc_tp	= @Resrc_tp

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
			,	@KEY1		= @Resrc_id
			,	@KEY2		= @Resrc_tp


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
			@Resrc_id	= @Resrc_id	OUTPUT
		,	@Resrc_tp	= @Resrc_tp	OUTPUT
		,	@Resrc_nm	= @Resrc_nm	OUTPUT
		,	@Resrc_tx	= @Resrc_tx	OUTPUT
		,	@ParentResrc_tp	= @ParentResrc_tp	OUTPUT
		,	@ResrcType_tx	= @ResrcType_tx	OUTPUT

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
	--	RESTRICT MODE:	tblResource_RightType
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspResource_RightType]
			@Resrc_id	= @Resrc_id
		,	@Resrc_tp	= @Resrc_tp
		,	@Right_tp	= @Right_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblResource_RightType
		)
		BEGIN
			SET	@STATUS	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFKNotExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= @SYSTABLE
			,	@KEY1		= @Resrc_id
			,	@KEY2		= @Resrc_tp
			,	@KEY3		= @Right_tp


			EXECUTE	@STATUS		= errFailedMode
				@Proc_nm	= @Proc_nm
			,	@Mode_cd	= @Mode_cd
			,	@Action_nm	= @SYSRIGHT
			,	@Table_nm	= 'tblResource_RightType'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblResource_RightType
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspResource_RightType]
			@Resrc_id	= @Resrc_id	OUTPUT
		,	@Resrc_tp	= @Resrc_tp	OUTPUT
		,	@Right_tp	= @Right_tp	OUTPUT
		,	@Resrc_nm	= @Resrc_nm	OUTPUT
		,	@Resrc_tx	= @Resrc_tx	OUTPUT
		,	@ParentResrc_tp	= @ParentResrc_tp	OUTPUT
		,	@ResrcType_tx	= @ResrcType_tx	OUTPUT
		,	@ParentRight_tp	= @ParentRight_tp	OUTPUT
		,	@RightType_tx	= @RightType_tx	OUTPUT

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
			,	@CallingProc_nm	= '[dbo].[uspResource_RightType]'
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


CREATE PROCEDURE	[dbo].[uspRole_ResourceType_RightType]
(
	@Role_id		udtResrc_id		=  -2147483647	OUTPUT	-- PK1 
,	@Role_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK2 AK2
,	@Role_nm		udtResrc_nm		= '-2147483647'	OUTPUT	--  AK1
,	@Role_cd		udtResrc_cd		= '-2147483647'	OUTPUT
,	@Resrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK3 AK3
,	@Right_tp		udtResrc_tp		= '-2147483647'	OUTPUT	-- PK4 AK4
,	@Role_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ParentResrc_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@ResrcType_tx		udtResrc_tx		= '-2147483647'	OUTPUT
,	@ParentRight_tp		udtResrc_tp		= '-2147483647'	OUTPUT
,	@RightType_tx		udtResrc_tx		= '-2147483647'	OUTPUT

,	@CallingProc_nm		udtResrc_nm	= NULL		-- The sproc calling this proc
,	@Source_nm		udtResrc_nm	= NULL	OUTPUT	-- System name
,	@Tran_dm		udtResrc_dm	= NULL	OUTPUT	-- Transaction date (history)
,	@Token_cd		udtResrc_cd	= NULL		-- Security Token
,	@Mode_cd		udtKey_cd	= 'R'		-- Database cascade mode code
)
-- WITH ENCRYPTION
AS
/*
**	Name:		uspRole_ResourceType_RightType
**	Type:		DB API procedure: Update
**	Purpose:	To insert Role_ResourceType_RightType data into tblRole_ResourceType_RightType
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
		,	@Table_nm	= 'tblRole_ResourceType_RightType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check to make sure the record to be updated actually exists!
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[rspRole_ResourceType_RightType]
		@Role_id	= @Role_id
	,	@Role_tp	= @Role_tp
	,	@Resrc_tp	= @Resrc_tp
	,	@Right_tp	= @Right_tp

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
		,	@KEY1		= @Role_id
		,	@KEY2		= @Role_tp
		,	@KEY3		= @Resrc_tp
		,	@KEY4		= @Right_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Validate attributes
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= [dbo].[vspRole_ResourceType_RightType]
		@Role_id	= @Role_id	OUTPUT
	,	@Role_tp	= @Role_tp	OUTPUT
	,	@Resrc_tp	= @Resrc_tp	OUTPUT
	,	@Right_tp	= @Right_tp	OUTPUT

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
		,	@KEY1		= @Role_tp
		,	@KEY2		= @Role_nm
		,	@KEY3		= @Resrc_tp
		,	@KEY4		= @Right_tp

		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Check Referential Integerity
	-----------------------------------------------------------------------
	--
	--	RESTRICT MODE:	tblRole
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspRole]
			@Role_id	= @Role_id
		,	@Role_tp	= @Role_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblRole
		)
		BEGIN
			SET	@STATUS	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFKNotExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= @SYSTABLE
			,	@KEY1		= @Role_id
			,	@KEY2		= @Role_tp


			EXECUTE	@STATUS		= errFailedMode
				@Proc_nm	= @Proc_nm
			,	@Mode_cd	= @Mode_cd
			,	@Action_nm	= @SYSRIGHT
			,	@Table_nm	= 'tblRole'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblRole
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspRole]
			@Role_id	= @Role_id	OUTPUT
		,	@Role_tp	= @Role_tp	OUTPUT
		,	@Role_nm	= @Role_nm	OUTPUT
		,	@Role_cd	= @Role_cd	OUTPUT
		,	@Role_tx	= @Role_tx	OUTPUT

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
			,	@CallingProc_nm	= '[dbo].[uspRole]'
			RETURN	@STATUS
		END
	END

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

	--
	--	RESTRICT MODE:	tblResourceType_RightType
	--
	IF
	(
		@Mode_cd	= 'R'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[rspResourceType_RightType]
			@Resrc_tp	= @Resrc_tp
		,	@Right_tp	= @Right_tp

		,	@Key_cd	    	= @Key_cd

		IF
		(
			@RETURN	= 1	-- FK was found in tblResourceType_RightType
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
			,	@KEY2		= @Right_tp


			EXECUTE	@STATUS		= errFailedMode
				@Proc_nm	= @Proc_nm
			,	@Mode_cd	= @Mode_cd
			,	@Action_nm	= @SYSRIGHT
			,	@Table_nm	= 'tblResourceType_RightType'
			RETURN	@STATUS
		END
	END
	--
	--	CASCADE MODE:	tblResourceType_RightType
	--
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		EXECUTE	@RETURN		= [dbo].[uspResourceType_RightType]
			@Resrc_tp	= @Resrc_tp	OUTPUT
		,	@Right_tp	= @Right_tp	OUTPUT
		,	@ParentResrc_tp	= @ParentResrc_tp	OUTPUT
		,	@ResrcType_tx	= @ResrcType_tx	OUTPUT
		,	@ParentRight_tp	= @ParentRight_tp	OUTPUT
		,	@RightType_tx	= @RightType_tx	OUTPUT

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
			,	@CallingProc_nm	= '[dbo].[uspResourceType_RightType]'
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

