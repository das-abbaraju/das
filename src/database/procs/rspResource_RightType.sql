DROP PROCEDURE IF EXISTS	`rspResource_RightType`
;

DELIMITER //
CREATE PROCEDURE	rspResource_RightType
(
	Resrc_id		int signed		
,	Resrc_tp		varchar(80)		
,	Right_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspResource_RightType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblResource_RightType
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
	IF Resrc_id IS NULL OR Resrc_id = 0 THEN SET Resrc_id =  -2147483647;	END IF;
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF Right_tp IS NULL OR Right_tp = '' THEN SET Right_tp = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblResource_RightType`
			WHERE
				Resrc_id	= Resrc_id
			AND	Resrc_tp	= Resrc_tp
			AND	Right_tp	= Right_tp

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
			FROM	`tblResource_RightType`
			WHERE
				Resrc_id	= Resrc_id
			AND	Resrc_tp	= Resrc_tp

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
			FROM	`tblResource_RightType`
			WHERE
				Right_tp	= Right_tp

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
			FROM	`tblResource_RightType`
			WHERE
				(
				Resrc_id	= Resrc_id
			OR	Resrc_id	=  -2147483647
				)
			AND	(
				Resrc_tp	= Resrc_tp
			OR	Resrc_tp	= '-2147483647'
				)
			AND	(
				Right_tp	= Right_tp
			OR	Right_tp	= '-2147483647'
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

