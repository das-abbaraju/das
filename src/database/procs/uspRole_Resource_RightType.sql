

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

