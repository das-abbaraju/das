DROP PROCEDURE IF EXISTS	`rspUser_Role`
;

DELIMITER //
CREATE PROCEDURE	rspUser_Role
(
	User_id		int signed		
,	User_tp		varchar(80)		
,	Role_id		int signed		
,	Role_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspUser_Role
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblUser_Role
**	Author:		Solomon S. Shacter
**	Generated:	4/12/2013
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	_RETURN		INT DEFAULT 0;
###############################################################################
BEGIN
	IF _Key_cd IS NULL OR _Key_cd = '' THEN SET _Key_cd = 'PK';	END IF;
	IF User_id IS NULL OR User_id = 0 THEN SET User_id =  -2147483647;	END IF;
	IF User_tp IS NULL OR User_tp = '' THEN SET User_tp = '-2147483647';	END IF;
	IF Role_id IS NULL OR Role_id = 0 THEN SET Role_id =  -2147483647;	END IF;
	IF Role_tp IS NULL OR Role_tp = '' THEN SET Role_tp = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUser_Role`
			WHERE
				User_id	= User_id
			AND	User_tp	= User_tp
			AND	Role_id	= Role_id
			AND	Role_tp	= Role_tp

		)
		THEN
			SET _RowExists_fg	= 1;
		ELSE
			SET _RowExists_fg	= 0;
		END IF;
	END IF;

	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUser_Role`
			WHERE
				User_id	= User_id
			AND	User_tp	= User_tp

		)
		THEN
			SET _RowExists_fg	= 1;
		ELSE
			SET _RowExists_fg	= 0;
		END IF;
	END IF;

	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK2'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUser_Role`
			WHERE
				Role_id	= Role_id
			AND	Role_tp	= Role_tp

		)
		THEN
			SET _RowExists_fg	= 1;
		ELSE
			SET _RowExists_fg	= 0;
		END IF;
	END IF;


	#######################################################################
	-- Attribute lookup
	#######################################################################
	IF	_Key_cd	= 'AL'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUser_Role`
			WHERE
				(
				User_id	= User_id
			OR	User_id	=  -2147483647
				)
			AND	(
				User_tp	= User_tp
			OR	User_tp	= '-2147483647'
				)
			AND	(
				Role_id	= Role_id
			OR	Role_id	=  -2147483647
				)
			AND	(
				Role_tp	= Role_tp
			OR	Role_tp	= '-2147483647'
				)

		)
		THEN
			SET _RowExists_fg	= 1;
		ELSE
			SET _RowExists_fg	= 0;
		END IF;
	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

