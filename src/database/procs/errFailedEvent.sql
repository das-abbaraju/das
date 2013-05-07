DROP PROCEDURE IF EXISTS `errFailedEvent`
;

DELIMITER //
CREATE PROCEDURE	errFailedEvent
(
	_Proc_nm		VARCHAR(64)
,	_Table_nm		VARCHAR(64)
,	_Action_nm		VARCHAR(64)
)
BEGIN
/*
**	Name:		errFailedEvent
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

DECLARE _FAILED_EVENT	CONDITION FOR SQLSTATE '99001';
DECLARE _ERRORMSG	VARCHAR(80) DEFAULT
	CONCAT
			(
				'Error: '
			,	_Proc_nm
			,	' Failed to '
			,	_Action_nm
			,	' record(s) for table '
			,	_Table_nm
			);
###############################################################################
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
 	SHOW ERRORS;
-- 	SHOW WARNINGS;
	SIGNAL _FAILED_EVENT
	SET MESSAGE_TEXT	= _ERRORMSG
	,	MYSQL_ERRNO	= 9001
	,	TABLE_NAME	= _Table_nm;  
	#######################################################################
###############################################################################
 END;
###############################################################################
END
//
DELIMITER ;
;


