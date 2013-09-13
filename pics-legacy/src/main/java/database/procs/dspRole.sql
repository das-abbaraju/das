

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

