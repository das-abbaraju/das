DROP PROCEDURE IF EXISTS `errFailedMode`
;

DELIMITER //
CREATE PROCEDURE	errFailedMode
(
	_Proc_nm		VARCHAR(64)
,	_Mode_cd		VARCHAR(48)
,	_Action_nm		VARCHAR(64)
,	_Table_nm		VARCHAR(64)
)
BEGIN
/*
**	Name:		errFailedMode
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
###############################################################################
DECLARE	_RETURN		INT DEFAULT 0;
DECLARE	_STATUS		VARCHAR(40) DEFAULT 'SUCCESS';

DECLARE _FAILED_MODE	CONDITION FOR SQLSTATE '99002'; -- '01099'
DECLARE _ERRORMSG	VARCHAR(80) DEFAULT
	CONCAT
			(
				'Error: '
			,	_Proc_nm
			,	' Database MODE is set to '
			,	_Mode_cd
			,	'. '
			,	_Action_nm
			,	' is not allowed on table '
			,	_Table_nm
			,	' for this MODE setting.'
			);
###############################################################################
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
-- 	SHOW ERRORS;
-- 	SHOW WARNINGS;
	SIGNAL _FAILED_MODE
	SET MESSAGE_TEXT	= _ERRORMSG
	,	MYSQL_ERRNO	= 9002
	,	TABLE_NAME	= _Table_nm;  
	#######################################################################
###############################################################################
END;
###############################################################################
END
//
DELIMITER ;
;


