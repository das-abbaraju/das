

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


CREATE PROCEDURE	[dbo].[dspPersonType]
(
	@Person_tp		udtResrc_tp	= NULL	-- PK1 
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
**	Name:		dspPersonType
**	Type:		DB API procedure: Delete
**	Purpose:	To delete PersonType data From tblPersonType
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
		,	@Table_nm	= 'tblPersonType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Do NOT Delete If No Arguments Sent 
	-----------------------------------------------------------------------
	IF
	(
		@Person_tp	IS NULL
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
		-- tblPerson
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspPerson]
			@Person_tp	= @Person_tp

		,	@Key_cd		= 'FK2'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblPerson'
			,	@KEY1		= @Person_tp

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
		-- vwPerson
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspPerson]
			@Person_tp	= @Person_tp

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
			,	@CallingProc_nm	= '[dbo].[dspPerson]'
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
		[dbo].[tblPersonType]
	WHERE
	(
		Person_tp	= @Person_tp
	OR	@Person_tp	IS NULL
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


CREATE PROCEDURE	[dbo].[dspResource]
(
	@Resrc_id		udtResrc_id	= NULL	-- PK1 
,	@Resrc_tp		udtResrc_tp	= NULL	-- PK2 AK2
,	@Resrc_nm		udtResrc_nm	= NULL	--  AK1
,	@Resrc_tx		udtResrc_tx	= NULL
,	@ADD_dm		udtResrc_dm	= NULL
,	@ADD_nm		udtResrc_nm	= NULL
,	@UPD_dm		udtResrc_dm	= NULL
,	@UPD_nm		udtResrc_nm	= NULL
,	@DEL_dm		udtResrc_dm	= NULL
,	@DEL_nm		udtResrc_nm	= NULL
,	@ParentResrc_tp		udtResrc_tp	= NULL
,	@ResrcType_tx		udtResrc_tx	= NULL
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
**	Name:		dspResource
**	Type:		DB API procedure: Delete
**	Purpose:	To delete Resource data From tblResource
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
		,	@Table_nm	= 'tblResource'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Do NOT Delete If No Arguments Sent 
	-----------------------------------------------------------------------
	IF
	(
		@Resrc_id	IS NULL
	AND	@Resrc_tp	IS NULL
	AND	@Resrc_nm	IS NULL
	AND	@Resrc_tx	IS NULL
	AND	@ADD_dm	IS NULL
	AND	@ADD_nm	IS NULL
	AND	@UPD_dm	IS NULL
	AND	@UPD_nm	IS NULL
	AND	@DEL_dm	IS NULL
	AND	@DEL_nm	IS NULL
	AND	@ParentResrc_tp	IS NULL
	AND	@ResrcType_tx	IS NULL
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
		-- tblPerson
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspPerson]
			@Person_id	= @Resrc_id
		,	@Person_tp	= @Resrc_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblPerson'
			,	@KEY1		= @Resrc_id
			,	@KEY2		= @Resrc_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblResource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspResource_RightType]
			@Resrc_id	= @Resrc_id
		,	@Resrc_tp	= @Resrc_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblResource_RightType'
			,	@KEY1		= @Resrc_id
			,	@KEY2		= @Resrc_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblRole
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspRole]
			@Role_id	= @Resrc_id
		,	@Role_tp	= @Resrc_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblRole'
			,	@KEY1		= @Resrc_id
			,	@KEY2		= @Resrc_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblRole_Resource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspRole_Resource_RightType]
			@Resrc_id	= @Resrc_id
		,	@Resrc_tp	= @Resrc_tp

		,	@Key_cd		= 'FK2'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblRole_Resource_RightType'
			,	@KEY1		= @Resrc_id
			,	@KEY2		= @Resrc_tp

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
		-- vwPerson
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspPerson]
			@Person_id	= @Resrc_id
		,	@Person_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspPerson]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwResource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspResource_RightType]
			@Resrc_id	= @Resrc_id
		,	@Resrc_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspResource_RightType]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwRole
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspRole]
			@Role_id	= @Resrc_id
		,	@Role_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspRole]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwRole_Resource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspRole_Resource_RightType]
			@Resrc_id	= @Resrc_id
		,	@Resrc_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspRole_Resource_RightType]'
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
		[dbo].[tblResource]
	WHERE
	(
		Resrc_id	= @Resrc_id
	OR	@Resrc_id	IS NULL
	)
	AND
	(
		Resrc_tp	= @Resrc_tp
	OR	@Resrc_tp	IS NULL
	)
	AND
	(
		Resrc_nm	= @Resrc_nm
	OR	@Resrc_nm	IS NULL
	)
	AND
	(
		Resrc_tx	LIKE @Resrc_tx
	OR	@Resrc_tx	IS NULL
	)
	AND
	(
		ADD_dm	= @ADD_dm
	OR	@ADD_dm	IS NULL
	)
	AND
	(
		ADD_nm	= @ADD_nm
	OR	@ADD_nm	IS NULL
	)
	AND
	(
		UPD_dm	= @UPD_dm
	OR	@UPD_dm	IS NULL
	)
	AND
	(
		UPD_nm	= @UPD_nm
	OR	@UPD_nm	IS NULL
	)
	AND
	(
		DEL_dm	= @DEL_dm
	OR	@DEL_dm	IS NULL
	)
	AND
	(
		DEL_nm	= @DEL_nm
	OR	@DEL_nm	IS NULL
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


CREATE PROCEDURE	[dbo].[dspResource_RightType]
(
	@Resrc_id		udtResrc_id	= NULL	-- PK1 
,	@Resrc_tp		udtResrc_tp	= NULL	-- PK2 AK2
,	@Right_tp		udtResrc_tp	= NULL	-- PK3 AK3
,	@Resrc_nm		udtResrc_nm	= NULL	--  AK1
,	@Resrc_tx		udtResrc_tx	= NULL
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
**	Name:		dspResource_RightType
**	Type:		DB API procedure: Delete
**	Purpose:	To delete Resource_RightType data From tblResource_RightType
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
		,	@Table_nm	= 'tblResource_RightType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Do NOT Delete If No Arguments Sent 
	-----------------------------------------------------------------------
	IF
	(
		@Resrc_id	IS NULL
	AND	@Resrc_tp	IS NULL
	AND	@Right_tp	IS NULL
	AND	@Resrc_nm	IS NULL
	AND	@Resrc_tx	IS NULL
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
		---------------------------------------------------------------
		-- tblRole_Resource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspRole_Resource_RightType]
			@Resrc_id	= @Resrc_id
		,	@Resrc_tp	= @Resrc_tp
		,	@Right_tp	= @Right_tp

		,	@Key_cd		= 'FK3'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblRole_Resource_RightType'
			,	@KEY1		= @Resrc_id
			,	@KEY2		= @Resrc_tp
			,	@KEY3		= @Right_tp

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
		-- vwRole_Resource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspRole_Resource_RightType]
			@Resrc_id	= @Resrc_id
		,	@Resrc_tp	= @Resrc_tp
		,	@Right_tp	= @Right_tp

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
			,	@CallingProc_nm	= '[dbo].[dspRole_Resource_RightType]'
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
		[dbo].[tblResource_RightType]
	WHERE
	(
		Resrc_id	= @Resrc_id
	OR	@Resrc_id	IS NULL
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


CREATE PROCEDURE	[dbo].[dspResourceType]
(
	@Resrc_tp		udtResrc_tp	= NULL	-- PK1 
,	@ParentResrc_tp		udtResrc_tp	= NULL
,	@ResrcType_tx		udtResrc_tx	= NULL
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
**	Name:		dspResourceType
**	Type:		DB API procedure: Delete
**	Purpose:	To delete ResourceType data From tblResourceType
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
		,	@Table_nm	= 'tblResourceType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Do NOT Delete If No Arguments Sent 
	-----------------------------------------------------------------------
	IF
	(
		@Resrc_tp	IS NULL
	AND	@ParentResrc_tp	IS NULL
	AND	@ResrcType_tx	IS NULL
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
		-- tblPersonType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspPersonType]
			@Person_tp	= @Resrc_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblPersonType'
			,	@KEY1		= @Resrc_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblResource
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspResource]
			@Resrc_tp	= @Resrc_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblResource'
			,	@KEY1		= @Resrc_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspResourceType_RightType]
			@Resrc_tp	= @Resrc_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblResourceType_RightType'
			,	@KEY1		= @Resrc_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblRightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspRightType]
			@Right_tp	= @Resrc_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblRightType'
			,	@KEY1		= @Resrc_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblRole_ResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspRole_ResourceType_RightType]
			@Resrc_tp	= @Resrc_tp

		,	@Key_cd		= 'FK2'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblRole_ResourceType_RightType'
			,	@KEY1		= @Resrc_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblRoleType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspRoleType]
			@Role_tp	= @Resrc_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblRoleType'
			,	@KEY1		= @Resrc_tp

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
		-- vwPersonType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspPersonType]
			@Person_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspPersonType]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwResource
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspResource]
			@Resrc_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspResource]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspResourceType_RightType]
			@Resrc_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspResourceType_RightType]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwRightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspRightType]
			@Right_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspRightType]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwRole_ResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspRole_ResourceType_RightType]
			@Resrc_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspRole_ResourceType_RightType]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwRoleType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspRoleType]
			@Role_tp	= @Resrc_tp

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
			,	@CallingProc_nm	= '[dbo].[dspRoleType]'
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
		[dbo].[tblResourceType]
	WHERE
	(
		Resrc_tp	= @Resrc_tp
	OR	@Resrc_tp	IS NULL
	)
	AND
	(
		ParentResrc_tp	= @ParentResrc_tp
	OR	@ParentResrc_tp	IS NULL
	)
	AND
	(
		ResrcType_tx	LIKE @ResrcType_tx
	OR	@ResrcType_tx	IS NULL
	)
	AND
	(
		Left_id	= @Left_id
	OR	@Left_id	IS NULL
	)
	AND
	(
		Right_id	= @Right_id
	OR	@Right_id	IS NULL
	)
	AND
	(
		Level_id	= @Level_id
	OR	@Level_id	IS NULL
	)
	AND
	(
		Order_id	= @Order_id
	OR	@Order_id	IS NULL
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


CREATE PROCEDURE	[dbo].[dspResourceType_RightType]
(
	@Resrc_tp		udtResrc_tp	= NULL	-- PK1 
,	@Right_tp		udtResrc_tp	= NULL	-- PK2 
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
**	Name:		dspResourceType_RightType
**	Type:		DB API procedure: Delete
**	Purpose:	To delete ResourceType_RightType data From tblResourceType_RightType
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
		,	@Table_nm	= 'tblResourceType_RightType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Do NOT Delete If No Arguments Sent 
	-----------------------------------------------------------------------
	IF
	(
		@Resrc_tp	IS NULL
	AND	@Right_tp	IS NULL
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
		---------------------------------------------------------------
		-- tblRole_ResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspRole_ResourceType_RightType]
			@Resrc_tp	= @Resrc_tp
		,	@Right_tp	= @Right_tp

		,	@Key_cd		= 'FK3'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblRole_ResourceType_RightType'
			,	@KEY1		= @Resrc_tp
			,	@KEY2		= @Right_tp

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
		-- vwRole_ResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspRole_ResourceType_RightType]
			@Resrc_tp	= @Resrc_tp
		,	@Right_tp	= @Right_tp

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
			,	@CallingProc_nm	= '[dbo].[dspRole_ResourceType_RightType]'
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
		[dbo].[tblResourceType_RightType]
	WHERE
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


CREATE PROCEDURE	[dbo].[dspRightType]
(
	@Right_tp		udtResrc_tp	= NULL	-- PK1 
,	@ParentRight_tp		udtResrc_tp	= NULL
,	@RightType_tx		udtResrc_tx	= NULL
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
**	Name:		dspRightType
**	Type:		DB API procedure: Delete
**	Purpose:	To delete RightType data From tblRightType
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
		,	@Table_nm	= 'tblRightType'
		RETURN	@STATUS
	END
	-----------------------------------------------------------------------
	-- Do NOT Delete If No Arguments Sent 
	-----------------------------------------------------------------------
	IF
	(
		@Right_tp	IS NULL
	AND	@ParentRight_tp	IS NULL
	AND	@RightType_tx	IS NULL
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
		-- tblResource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspResource_RightType]
			@Right_tp	= @Right_tp

		,	@Key_cd		= 'FK2'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblResource_RightType'
			,	@KEY1		= @Right_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspResourceType_RightType]
			@Right_tp	= @Right_tp

		,	@Key_cd		= 'FK2'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblResourceType_RightType'
			,	@KEY1		= @Right_tp

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
		-- vwResource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspResource_RightType]
			@Right_tp	= @Right_tp

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
			,	@CallingProc_nm	= '[dbo].[dspResource_RightType]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspResourceType_RightType]
			@Right_tp	= @Right_tp

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
			,	@CallingProc_nm	= '[dbo].[dspResourceType_RightType]'
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
		[dbo].[tblRightType]
	WHERE
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


CREATE PROCEDURE	[dbo].[dspRole]
(
	@Role_id		udtResrc_id	= NULL	-- PK1 
,	@Role_tp		udtResrc_tp	= NULL	-- PK2 AK2
,	@Role_nm		udtResrc_nm	= NULL	--  AK1
,	@Role_cd		udtResrc_cd	= NULL
,	@Role_tx		udtResrc_tx	= NULL
,	@ADD_dm		udtResrc_dm	= NULL
,	@ADD_nm		udtResrc_nm	= NULL
,	@UPD_dm		udtResrc_dm	= NULL
,	@UPD_nm		udtResrc_nm	= NULL
,	@DEL_dm		udtResrc_dm	= NULL
,	@DEL_nm		udtResrc_nm	= NULL
,	@ParentRole_tp		udtResrc_tp	= NULL
,	@RoleType_tx		udtResrc_tx	= NULL
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
**	Name:		dspRole
**	Type:		DB API procedure: Delete
**	Purpose:	To delete Role data From tblRole
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
		,	@Table_nm	= 'tblRole'
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
	AND	@Role_tx	IS NULL
	AND	@ADD_dm	IS NULL
	AND	@ADD_nm	IS NULL
	AND	@UPD_dm	IS NULL
	AND	@UPD_nm	IS NULL
	AND	@DEL_dm	IS NULL
	AND	@DEL_nm	IS NULL
	AND	@ParentRole_tp	IS NULL
	AND	@RoleType_tx	IS NULL
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
		-- tblRole_Resource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspRole_Resource_RightType]
			@Role_id	= @Role_id
		,	@Role_tp	= @Role_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblRole_Resource_RightType'
			,	@KEY1		= @Role_id
			,	@KEY2		= @Role_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblRole_ResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspRole_ResourceType_RightType]
			@Role_id	= @Role_id
		,	@Role_tp	= @Role_tp

		,	@Key_cd		= 'FK1'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblRole_ResourceType_RightType'
			,	@KEY1		= @Role_id
			,	@KEY2		= @Role_tp

			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- tblUser_Role
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[rspUser_Role]
			@Role_id	= @Role_id
		,	@Role_tp	= @Role_tp

		,	@Key_cd		= 'FK2'

		IF
		(
			@RETURN	= 1
		)
		BEGIN
			EXECUTE	@STATUS		= errFKExist
				@Proc_nm	= @Proc_nm
			,	@Table_nm	= 'tblUser_Role'
			,	@KEY1		= @Role_id
			,	@KEY2		= @Role_tp

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
		-- vwRole_Resource_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspRole_Resource_RightType]
			@Role_id	= @Role_id
		,	@Role_tp	= @Role_tp

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
			,	@CallingProc_nm	= '[dbo].[dspRole_Resource_RightType]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwRole_ResourceType_RightType
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspRole_ResourceType_RightType]
			@Role_id	= @Role_id
		,	@Role_tp	= @Role_tp

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
			,	@CallingProc_nm	= '[dbo].[dspRole_ResourceType_RightType]'
			RETURN	@STATUS
		END
		---------------------------------------------------------------
		-- vwUser_Role
		---------------------------------------------------------------
		EXECUTE	@RETURN		= [dbo].[dspUser_Role]
			@Role_id	= @Role_id
		,	@Role_tp	= @Role_tp

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
			,	@CallingProc_nm	= '[dbo].[dspUser_Role]'
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
		[dbo].[tblRole]
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
		Role_cd	= @Role_cd
	OR	@Role_cd	IS NULL
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


CREATE PROCEDURE	[dbo].[dspRole_Resource_RightType]
(
	@Role_id		udtResrc_id	= NULL	-- PK1 
,	@Role_tp		udtResrc_tp	= NULL	-- PK2 AK2
,	@Role_nm		udtResrc_nm	= NULL	--  AK1
,	@Role_cd		udtResrc_cd	= NULL
,	@Resrc_id		udtResrc_id	= NULL	-- PK3 AK3
,	@Resrc_tp		udtResrc_tp	= NULL	-- PK4 AK4
,	@Resrc_nm		udtResrc_nm	= NULL	--  AK6
,	@Right_tp		udtResrc_tp	= NULL	-- PK5 AK5
,	@Role_tx		udtResrc_tx	= NULL
,	@Resrc_tx		udtResrc_tx	= NULL
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
**	Name:		dspRole_Resource_RightType
**	Type:		DB API procedure: Delete
**	Purpose:	To delete Role_Resource_RightType data From tblRole_Resource_RightType
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
		,	@Table_nm	= 'tblRole_Resource_RightType'
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
	AND	@Resrc_id	IS NULL
	AND	@Resrc_tp	IS NULL
	AND	@Resrc_nm	IS NULL
	AND	@Right_tp	IS NULL
	AND	@Role_tx	IS NULL
	AND	@Resrc_tx	IS NULL
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
		[dbo].[tblRole_Resource_RightType]
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
		Resrc_id	= @Resrc_id
	OR	@Resrc_id	IS NULL
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

