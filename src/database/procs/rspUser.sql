DROP PROCEDURE IF EXISTS	`rspUser`
;

DELIMITER //
CREATE PROCEDURE	rspUser
(
	User_id		int signed		
,	User_tp		varchar(80)		
,	User_nm		varchar(128)		
,	Password_cd		varchar(48)		
,	Domain_nm		varchar(128)		
,	Email_tx		mediumtext		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspUser
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblUser
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
	IF User_nm IS NULL OR User_nm = '' THEN SET User_nm = '-2147483647';	END IF;
	IF Password_cd IS NULL OR Password_cd = '' THEN SET Password_cd = '-2147483647';	END IF;
	IF Domain_nm IS NULL OR Domain_nm = '' THEN SET Domain_nm = '-2147483647';	END IF;
	IF Email_tx IS NULL OR Email_tx = '' THEN SET Email_tx = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUser`
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
	IF	_Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUser`
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
			FROM	`tblUser`
			WHERE
				User_tp	= User_tp

		)
		THEN
			SET _RowExists_fg	= 1;
		ELSE
			SET _RowExists_fg	= 0;
		END IF;
	END IF;


	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	_Key_cd = 'AK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUser`
			WHERE
				User_tp	= User_tp
			AND	User_nm	= User_nm

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
			FROM	`tblUser`
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
				User_nm	= User_nm
			OR	User_nm	= '-2147483647'
				)
			AND	(
				Password_cd	= Password_cd
			OR	Password_cd	= '-2147483647'
				)
			AND	(
				Domain_nm	= Domain_nm
			OR	Domain_nm	= '-2147483647'
				)
			AND	(
				Email_tx	LIKE Email_tx
			OR	Email_tx	LIKE '-2147483647'
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

