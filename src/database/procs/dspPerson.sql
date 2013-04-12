

CREATE PROCEDURE	[dbo].[dspPerson]
(
	@Person_id		udtResrc_id	= NULL	-- PK1 
,	@Person_tp		udtResrc_tp	= NULL	-- PK2 AK2
,	@Person_nm		udtResrc_nm	= NULL	--  AK1
,	@First_nm		udtResrc_nm	= NULL
,	@Middle_nm		udtResrc_nm	= NULL
,	@Last_nm		udtResrc_nm	= NULL
,	@FirstSNDX_cd		udtResrc_cd	= NULL
,	@LastSNDX_cd		udtResrc_cd	= NULL
,	@Birth_dm		udtResrc_dm	= NULL
,	@Gender_cd		udtResrc_cd	= NULL
,	@Person_tx		udtResrc_tx	= NULL
,	@ADD_dm		udtResrc_dm	= NULL
,	@ADD_nm		udtResrc_nm	= NULL
,	@UPD_dm		udtResrc_dm	= NULL
,	@UPD_nm		udtResrc_nm	= NULL
,	@DEL_dm		udtResrc_dm	= NULL
,	@DEL_nm		udtResrc_nm	= NULL
,	@ParentPerson_tp		udtResrc_tp	= NULL
,	@PersonType_tx		udtResrc_tx	= NULL
,	@Left_id		udtResrc_id	= NULL
,	@Right_id		udtResrc_id	= NULL
,	@Level_id		udtResrc_id	= NULL
,	@Order_id		udtResrc_id	= NULL

,	@CallingProc_nm		udtResrc_nm	= NULL		-- The sproc calling this proc
,	@Source_nm		udtResrc_nm	= NULL	OUTPUT	-- System name
,	@Tran_dm		udtResrc_dm	= NULL	OUTPUT	-- Transaction date (history)
,	@Token_cd		udtResrc_cd	= NULL		-- Security Token
,	@Mode_cd		udtKey_cd	= 'R'		-- Database cascade mode code
)
-- WITH ENCRYPTION
AS
/*
**	Name:		dspPerson
**	Type:		DB API procedure: Delete
**	Purpose:	To delete Person data From tblPerson
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
		,	@Table_nm	= 'tblPerson'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Do NOT Delete If No Arguments Sent 
	-----------------------------------------------------------------------
	IF
	(
		@Person_id	IS NULL
	AND	@Person_tp	IS NULL
	AND	@Person_nm	IS NULL
	AND	@First_nm	IS NULL
	AND	@Middle_nm	IS NULL
	AND	@Last_nm	IS NULL
	AND	@FirstSNDX_cd	IS NULL
	AND	@LastSNDX_cd	IS NULL
	AND	@Birth_dm	IS NULL
	AND	@Gender_cd	IS NULL
	AND	@Person_tx	IS NULL
	AND	@ADD_dm	IS NULL
	AND	@ADD_nm	IS NULL
	AND	@UPD_dm	IS NULL
	AND	@UPD_nm	IS NULL
	AND	@DEL_dm	IS NULL
	AND	@DEL_nm	IS NULL
	AND	@ParentPerson_tp	IS NULL
	AND	@PersonType_tx	IS NULL
	AND	@Left_id	IS NULL
	AND	@Right_id	IS NULL
	AND	@Level_id	IS NULL
	AND	@Order_id	IS NULL

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
		---------------------------------------------------------------
		-- tblUser
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspUser]
			@User_id	= @Person_id
		,	@User_tp	= @Person_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblUser'
			,	@KEY1		= @Person_id
			,	@KEY2		= @Person_tp

			RETURN	@STATUS
		END

	END
	-----------------------------------------------------------------------
	-- Delete Dependent Foreign Key References On Cascade.
	-----------------------------------------------------------------------
	IF
	(
		@Mode_cd	= 'C'
	)
	BEGIN
		---------------------------------------------------------------
		-- vwUser
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspUser]
			@User_id	= @Person_id
		,	@User_tp	= @Person_tp

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
			SET	@ERROR	= 0
		END
		ELSE
		BEGIN
			EXECUTE	@STATUS		= errFailedCall
				@Proc_nm	= @Proc_nm
			,	@CallingProc_nm	= '[dbo].[dspUser]'
			RETURN	@STATUS
		END

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
		[dbo].[tblPerson]
	WHERE
	(
		Person_id	= @Person_id
	OR	@Person_id	IS NULL
	)
	AND
	(
		Person_tp	= @Person_tp
	OR	@Person_tp	IS NULL
	)
	AND
	(
		First_nm	= @First_nm
	OR	@First_nm	IS NULL
	)
	AND
	(
		Middle_nm	= @Middle_nm
	OR	@Middle_nm	IS NULL
	)
	AND
	(
		Last_nm	= @Last_nm
	OR	@Last_nm	IS NULL
	)
	AND
	(
		FirstSNDX_cd	= @FirstSNDX_cd
	OR	@FirstSNDX_cd	IS NULL
	)
	AND
	(
		LastSNDX_cd	= @LastSNDX_cd
	OR	@LastSNDX_cd	IS NULL
	)
	AND
	(
		Birth_dm	= @Birth_dm
	OR	@Birth_dm	IS NULL
	)
	AND
	(
		Gender_cd	= @Gender_cd
	OR	@Gender_cd	IS NULL
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

