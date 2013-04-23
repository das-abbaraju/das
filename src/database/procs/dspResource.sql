

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

