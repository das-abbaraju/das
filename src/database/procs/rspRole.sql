DROP PROCEDURE IF EXISTS	`rspRole`
;

DELIMITER //
CREATE PROCEDURE	rspRole
(
	Role_id		int signed		
,	Role_tp		varchar(80)		
,	Role_nm		varchar(128)		
,	Role_cd		varchar(48)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspRole
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblRole
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
	IF Role_id IS NULL OR Role_id = 0 THEN SET Role_id =  -2147483647;	END IF;
	IF Role_tp IS NULL OR Role_tp = '' THEN SET Role_tp = '-2147483647';	END IF;
	IF Role_nm IS NULL OR Role_nm = '' THEN SET Role_nm = '-2147483647';	END IF;
	IF Role_cd IS NULL OR Role_cd = '' THEN SET Role_cd = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblRole`
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
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblRole`
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
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK2'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblRole`
			WHERE
				Role_tp	= Role_tp

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
			FROM	`tblRole`
			WHERE
				Role_tp	= Role_tp
			AND	Role_nm	= Role_nm

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
			FROM	`tblRole`
			WHERE
				(
				Role_id	= Role_id
			OR	Role_id	=  -2147483647
				)
			AND	(
				Role_tp	= Role_tp
			OR	Role_tp	= '-2147483647'
				)
			AND	(
				Role_nm	= Role_nm
			OR	Role_nm	= '-2147483647'
				)
			AND	(
				Role_cd	= Role_cd
			OR	Role_cd	= '-2147483647'
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

