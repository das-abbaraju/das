IF
EXISTS
(
	SELECT	1
	FROM	sysobjects
	WHERE	type	= 'P'
	AND	id	= OBJECT_ID ('dbo.[errNoArg]')
)
	DROP
	PROCEDURE	dbo.[errNoArg]
GO

CREATE PROCEDURE	dbo.[errNoArg]
(
	@Proc_nm		varchar(30)
)
-- WITH ENCRYPTION
AS
/*
**	Name:		errNoArg
**	Type:		Special Procedure
**	Purpose:	To raise an error and print message based on arguments
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
		'%s Error: No Arguments passed to %s'
	,	16
	,	-1
	,	@Proc_nm
	,	@Proc_nm
	)
-------------------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	@STATUS
GO







