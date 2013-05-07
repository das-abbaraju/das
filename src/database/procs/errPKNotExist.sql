IF
EXISTS
(
	SELECT	1
	FROM	sysobjects
	WHERE	type	= 'P'
	AND	id	= OBJECT_ID ('dbo.[errPKNotExist]')
)
	DROP
	PROCEDURE	dbo.[errPKNotExist]
GO

CREATE PROCEDURE	dbo.[errPKNotExist]
(
	@Proc_nm		varchar(30)
,	@Table_nm		varchar(30)
,	@KEY1			varchar(40)	= ''
,	@KEY2			varchar(40)	= ''
,	@KEY3			varchar(40)	= ''
,	@KEY4			varchar(40)	= ''
,	@KEY5			varchar(40)	= ''
,	@KEY6			varchar(40)	= ''
,	@KEY7			varchar(40)	= ''
,	@KEY8			varchar(40)	= ''
,	@KEY9			varchar(40)	= ''
,	@KEY10			varchar(40)	= ''
,	@KEY11			varchar(40)	= ''
,	@KEY12			varchar(40)	= ''
,	@KEY13			varchar(40)	= ''
,	@KEY14			varchar(40)	= ''
,	@KEY15			varchar(40)	= ''
,	@KEY16			varchar(40)	= ''
)
-- WITH ENCRYPTION
AS
/*
**	Name:		errPKNotExist
**	Type:		Special Procedure
**	Purpose:	To raise an error and prvarchar(20) message based on arguments
**			passed to this procedure.
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**	Date:		03/15/03
**
**	Modified:	03/15/03
**	Modnumber:	00
**	Modification:	Original
**
*/
-------------------------------------------------------------------------------
DECLARE	@STATUS		int
DECLARE	@Error_id	int
DECLARE	@Key_nm		varchar(255)
DECLARE	@ErrorMsg_nm	varchar(255)
-------------------------------------------------------------------------------
BEGIN
	-----------------------------------------------------------------------
	-- Initialize
	-----------------------------------------------------------------------
	SELECT
		@STATUS		= -1
	,	@Error_id	= 99999
	-----------------------------------------------------------------------
	RAISERROR
	(
		'%s Error: Primary Key -> %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
does NOT EXIST in %s.'
	,	16
	,	-1
	,	@Proc_nm
	,	@KEY1
	,	@KEY2
	,	@KEY3
	,	@KEY4
	,	@KEY5
	,	@KEY6
	,	@KEY7
	,	@KEY8
	,	@KEY9
	,	@KEY10
	,	@KEY11
	,	@KEY12
	,	@KEY13
	,	@KEY14
	,	@KEY15
	,	@KEY16
	,	@Table_nm
	)
	-----------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	@STATUS
GO






