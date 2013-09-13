

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

