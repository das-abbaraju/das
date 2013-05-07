IF
EXISTS
(
	SELECT	1
	FROM	sysobjects
	WHERE	type	= 'P'
	AND	id	= OBJECT_ID ('dbo.[errFailedSecurity]')
)
	DROP
	PROCEDURE	dbo.[errFailedSecurity]
GO

CREATE PROCEDURE	dbo.[errFailedSecurity]
(
	@Proc_nm		varchar(30)
,	@Table_nm		varchar(30)
,	@Action_nm		varchar(80)
)
-- WITH ENCRYPTION
AS
/*
**	Name:		errFailedSecurity
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
		@STATUS	= -1
	,	@Error_id	= 99999
	-----------------------------------------------------------------------
	RAISERROR
	(
		'%s Error: Insufficient Rights. Cannot Perform %s On Object %s.'
	,	16
	,	-1
	,	@Proc_nm
	,	@Action_nm
	,	@Table_nm
	)
	-----------------------------------------------------------------------
END
-------------------------------------------------------------------------------
RETURN	@STATUS
GO







