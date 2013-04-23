

CREATE PROCEDURE	[dbo].[gspPerson]
(
	@Person_id		udtResrc_id	=  -2147483647	-- PK1 
,	@Person_tp		udtResrc_tp	= '-2147483647'	-- PK2 AK2
,	@Person_nm		udtResrc_nm	= '-2147483647'	--  AK1
,	@First_nm		udtResrc_nm	= '-2147483647'
,	@Middle_nm		udtResrc_nm	= '-2147483647'
,	@Last_nm		udtResrc_nm	= '-2147483647'
,	@FirstSNDX_cd		udtResrc_cd	= '-2147483647'
,	@LastSNDX_cd		udtResrc_cd	= '-2147483647'
,	@Birth_dm		udtResrc_dm	= '01/01/1754'
,	@Gender_cd		udtResrc_cd	= '-2147483647'
,	@Person_tx		udtResrc_tx	= '-2147483647'
,	@ADD_dm		udtResrc_dm	= '01/01/1754'
,	@ADD_nm		udtResrc_nm	= '-2147483647'
,	@UPD_dm		udtResrc_dm	= '01/01/1754'
,	@UPD_nm		udtResrc_nm	= '-2147483647'
,	@DEL_dm		udtResrc_dm	= '01/01/1754'
,	@DEL_nm		udtResrc_nm	= '-2147483647'
,	@ParentPerson_tp		udtResrc_tp	= '-2147483647'
,	@PersonType_tx		udtResrc_tx	= '-2147483647'
,	@Left_id		udtResrc_id	=  -2147483647
,	@Right_id		udtResrc_id	=  -2147483647
,	@Level_id		udtResrc_id	=  -2147483647
,	@Order_id		udtResrc_id	=  -2147483647

,	@Key_cd			udtKey_cd		= 'PK'	-- Search key code
)
--WITH ENCRYPTION
AS
/*
**	Name:		gspPerson
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwPerson
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
-------------------------------------------------------------------------------
BEGIN
	-----------------------------------------------------------------------
	-- Initialize
	-----------------------------------------------------------------------
	SELECT
 		@STATUS		= 0
	,	@RETURN		= 0
	,	@SYSTABLE	= 'vwPerson'
	,	@SYSRIGHT	= 'SELECT'
	,	@Proc_nm	= OBJECT_NAME(@@PROCID)
	-----------------------------------------------------------------------
	-- Check Security
	-----------------------------------------------------------------------
	EXECUTE	@RETURN		= spSecurityCheck
		@SYSTABLE	= @SYSTABLE
	,	@SYSRIGHT	= @SYSRIGHT

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
	-- Primary Key lookup
	-----------------------------------------------------------------------
	IF
	(
		@Key_cd = 'PK'
	)
	BEGIN
		SELECT
			Person_id
		,	Person_tp
		,	Person_nm
		,	First_nm
		,	Middle_nm
		,	Last_nm
		,	FirstSNDX_cd
		,	LastSNDX_cd
		,	Birth_dm
		,	Gender_cd
		,	Person_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentPerson_tp
		,	PersonType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwPerson]
		WHERE
			Person_id	= @Person_id
		AND	Person_tp	= @Person_tp

		RETURN	0
	END
	-----------------------------------------------------------------------
	-- Foreign Key lookup
	-----------------------------------------------------------------------
	IF
	(
		@Key_cd	= 'FK1'
	)
	BEGIN
		SELECT
			Person_id
		,	Person_tp
		,	Person_nm
		,	First_nm
		,	Middle_nm
		,	Last_nm
		,	FirstSNDX_cd
		,	LastSNDX_cd
		,	Birth_dm
		,	Gender_cd
		,	Person_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentPerson_tp
		,	PersonType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwPerson]
		WHERE
			Person_id	= @Person_id
		AND	Person_tp	= @Person_tp

		RETURN	0
	END
	IF
	(
		@Key_cd	= 'FK2'
	)
	BEGIN
		SELECT
			Person_id
		,	Person_tp
		,	Person_nm
		,	First_nm
		,	Middle_nm
		,	Last_nm
		,	FirstSNDX_cd
		,	LastSNDX_cd
		,	Birth_dm
		,	Gender_cd
		,	Person_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentPerson_tp
		,	PersonType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwPerson]
		WHERE
			Person_tp	= @Person_tp

		RETURN	0
	END

	-----------------------------------------------------------------------
	-- Alternate Key lookup
	-----------------------------------------------------------------------
	IF
	(
		@Key_cd	= 'AK'
	)
	BEGIN
		SELECT
			Person_id
		,	Person_tp
		,	Person_nm
		,	First_nm
		,	Middle_nm
		,	Last_nm
		,	FirstSNDX_cd
		,	LastSNDX_cd
		,	Birth_dm
		,	Gender_cd
		,	Person_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentPerson_tp
		,	PersonType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwPerson]
		WHERE
			Person_tp	= @Person_tp
		AND	Person_nm	= @Person_nm

		RETURN	0
	END
	-----------------------------------------------------------------------
	-- Search Key lookup
	-----------------------------------------------------------------------
	IF
	(
		@Key_cd	= 'SK1'
	)
	BEGIN
		SELECT
			Person_id
		,	Person_tp
		,	Person_nm
		,	First_nm
		,	Middle_nm
		,	Last_nm
		,	FirstSNDX_cd
		,	LastSNDX_cd
		,	Birth_dm
		,	Gender_cd
		,	Person_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentPerson_tp
		,	PersonType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwPerson]
		WHERE
			Person_tp	= @Person_tp
		AND	First_nm	= @First_nm
		AND	Last_nm	= @Last_nm

		RETURN	0
	END

	-----------------------------------------------------------------------
	-- Attribute lookup
	-----------------------------------------------------------------------
	IF
	(
		@Key_cd	= 'AL'
	)
	BEGIN
		SELECT
			Person_id
		,	Person_tp
		,	Person_nm
		,	First_nm
		,	Middle_nm
		,	Last_nm
		,	FirstSNDX_cd
		,	LastSNDX_cd
		,	Birth_dm
		,	Gender_cd
		,	Person_tx
		,	ADD_dm
		,	ADD_nm
		,	UPD_dm
		,	UPD_nm
		,	DEL_dm
		,	DEL_nm
		,	ParentPerson_tp
		,	PersonType_tx
		,	Left_id
		,	Right_id
		,	Level_id
		,	Order_id
		FROM
			[dbo].[vwPerson]
		WHERE
			(
			Person_id	= @Person_id
		OR	@Person_id	=  -2147483647
			)
		AND	(
			Person_tp	= @Person_tp
		OR	@Person_tp	= '-2147483647'
			)
		AND	(
			Person_nm	= @Person_nm
		OR	@Person_nm	= '-2147483647'
			)
		AND	(
			First_nm	= @First_nm
		OR	@First_nm	= '-2147483647'
			)
		AND	(
			Middle_nm	= @Middle_nm
		OR	@Middle_nm	= '-2147483647'
			)
		AND	(
			Last_nm	= @Last_nm
		OR	@Last_nm	= '-2147483647'
			)
		AND	(
			FirstSNDX_cd	= @FirstSNDX_cd
		OR	@FirstSNDX_cd	= '-2147483647'
			)
		AND	(
			LastSNDX_cd	= @LastSNDX_cd
		OR	@LastSNDX_cd	= '-2147483647'
			)
		AND	(
			Birth_dm	= @Birth_dm
		OR	@Birth_dm	= '01/01/1754'
			)
		AND	(
			Gender_cd	= @Gender_cd
		OR	@Gender_cd	= '-2147483647'
			)
		AND	(
			Person_tx	LIKE @Person_tx
		OR	@Person_tx	LIKE '-2147483647'
			)
		AND	(
			ADD_dm	= @ADD_dm
		OR	@ADD_dm	= '01/01/1754'
			)
		AND	(
			ADD_nm	= @ADD_nm
		OR	@ADD_nm	= '-2147483647'
			)
		AND	(
			UPD_dm	= @UPD_dm
		OR	@UPD_dm	= '01/01/1754'
			)
		AND	(
			UPD_nm	= @UPD_nm
		OR	@UPD_nm	= '-2147483647'
			)
		AND	(
			DEL_dm	= @DEL_dm
		OR	@DEL_dm	= '01/01/1754'
			)
		AND	(
			DEL_nm	= @DEL_nm
		OR	@DEL_nm	= '-2147483647'
			)
		AND	(
			ParentPerson_tp	= @ParentPerson_tp
		OR	@ParentPerson_tp	= '-2147483647'
			)
		AND	(
			PersonType_tx	LIKE @PersonType_tx
		OR	@PersonType_tx	LIKE '-2147483647'
			)
		AND	(
			Left_id	= @Left_id
		OR	@Left_id	=  -2147483647
			)
		AND	(
			Right_id	= @Right_id
		OR	@Right_id	=  -2147483647
			)
		AND	(
			Level_id	= @Level_id
		OR	@Level_id	=  -2147483647
			)
		AND	(
			Order_id	= @Order_id
		OR	@Order_id	=  -2147483647
			)

		RETURN	0
	END
	-----------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	0
GO

