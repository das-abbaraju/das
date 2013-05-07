DROP PROCEDURE IF EXISTS `errPKExist`;

DELIMITER //
CREATE PROCEDURE	errPKExist
(
	_Proc_nm		VARCHAR(30)
,	_Table_nm		VARCHAR(80)
,	_Key1			VARCHAR(40)
,	_Key2			VARCHAR(40)
,	_Key3			VARCHAR(40)
,	_Key4			VARCHAR(40)
,	_Key5			VARCHAR(40)
,	_Key6			VARCHAR(40)
,	_Key7			VARCHAR(40)
,	_Key8			VARCHAR(40)
,	_Key9			VARCHAR(40)
,	_Key10			VARCHAR(40)
,	_Key11			VARCHAR(40)
,	_Key12			VARCHAR(40)
,	_Key13			VARCHAR(40)
,	_Key14			VARCHAR(40)
,	_Key15			VARCHAR(40)
,	_Key16			VARCHAR(40)
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
DECLARE	_STATUS		INT DEFAULT 0;
DECLARE _ERRORMSG	VARCHAR(80);
###############################################################################
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF _Key1 IS NULL THEN SET _Key1 = '';	END IF;
	IF _Key2 IS NULL THEN SET _Key2 = '';	END IF;
	IF _Key3 IS NULL THEN SET _Key3 = '';	END IF;
	IF _Key4 IS NULL THEN SET _Key4 = '';	END IF;
	IF _Key5 IS NULL THEN SET _Key5 = '';	END IF;
	IF _Key6 IS NULL THEN SET _Key6 = '';	END IF;
	IF _Key7 IS NULL THEN SET _Key7 = '';	END IF;
	IF _Key8 IS NULL THEN SET _Key8 = '';	END IF;
	IF _Key9 IS NULL THEN SET _Key9 = '';	END IF;
	IF _Key10 IS NULL THEN SET _Key10 = '';	END IF;
	IF _Key11 IS NULL THEN SET _Key11 = '';	END IF;
	IF _Key12 IS NULL THEN SET _Key12 = '';	END IF;
	IF _Key13 IS NULL THEN SET _Key13 = '';	END IF;
	IF _Key14 IS NULL THEN SET _Key14 = '';	END IF;
	IF _Key15 IS NULL THEN SET _Key15 = '';	END IF;
	IF _Key16 IS NULL THEN SET _Key16 = '';	END IF;

	SET _STATUS	= -1;
	SET @ERRORMSG	= CONCAT
		(
			'CALL nullProc (`[ PICS '
		,	_Proc_nm
		,	' Error: A row with Primary Key --> '
		,	_Key1,', '
		,	_Key2,', '
		,	_Key3,', '
		,	_Key4,', '
		,	_Key5,', '
		,	_Key6,', '
		,	_Key7,', '
		,	_Key8,', '
		,	_Key9,', '
		,	_Key10,', '
		,	_Key11,', '
		,	_Key12,', '
		,	_Key13,', '
		,	_Key14,', '
		,	_Key15,', '
		,	_Key16,', '
		,	' EXISTS in table '
		,	_Table_nm
		,	' ]`)'
		);
	#######################################################################
	PREPARE _SIGNAL FROM @ERRORMSG;
 	EXECUTE _SIGNAL;
 	DEALLOCATE PREPARE _SIGNAL;
###############################################################################
 END;
###############################################################################
END
//
;






