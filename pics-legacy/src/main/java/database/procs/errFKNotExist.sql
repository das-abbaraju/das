DROP PROCEDURE IF EXISTS `errFKNotExist`
;

DELIMITER //
CREATE PROCEDURE	errFKNotExist
(
	Proc_nm		VARCHAR(64)
,	Table_nm	VARCHAR(64)
,	Action_nm	VARCHAR(64)
,	Key_nm		VARCHAR(255)
)
BEGIN
/*
**	Name:		errFKNotExist
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
DECLARE FK_NOT_EXIST	CONDITION FOR SQLSTATE '99006';
DECLARE ERRORMSG	VARCHAR(80) DEFAULT
	CONCAT
			(
				'Error: '
			,	IFNULL(Proc_nm, '<Unknown>')
			,	' No matching Foreign Key in '
			,	IFNULL(Table_nm, '<Unknown>')
			,	' exists for FK - '
			,	IFNULL(Key_nm, '<Unknown>')
			,	' for this '
			,	IFNULL(Action_nm, '<Unknown>')
			);
###############################################################################
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
-- 	SHOW ERRORS;
-- 	SHOW WARNINGS;
	SIGNAL FK_NOT_EXIST
	SET MESSAGE_TEXT	= ERRORMSG
	,	MYSQL_ERRNO	= 9006
	,	TABLE_NAME	= Table_nm;  
	#######################################################################
###############################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

