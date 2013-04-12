DROP PROCEDURE IF EXISTS	`rspResource`
;

DELIMITER //
CREATE PROCEDURE	rspResource
(
	Resrc_id		int signed		
,	Resrc_tp		varchar(80)		
,	Resrc_nm		varchar(128)		
,	Resrc_tx		mediumtext		
,	ADD_dm		datetime		
,	ADD_nm		varchar(128)		
,	UPD_dm		datetime		
,	UPD_nm		varchar(128)		
,	DEL_dm		datetime		
,	DEL_nm		varchar(128)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspResource
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblResource
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
	IF Resrc_nm IS NULL OR Resrc_nm = '' THEN SET Resrc_nm = '-2147483647';	END IF;
	IF Resrc_tx IS NULL OR Resrc_tx = '' THEN SET Resrc_tx = '-2147483647';	END IF;
	IF ADD_dm IS NULL OR ADD_dm = '' THEN SET ADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ADD_nm IS NULL OR ADD_nm = '' THEN SET ADD_nm = '-2147483647';	END IF;
	IF UPD_dm IS NULL OR UPD_dm = '' THEN SET UPD_dm = '0000-00-00 00:00:00';	END IF;
	IF UPD_nm IS NULL OR UPD_nm = '' THEN SET UPD_nm = '-2147483647';	END IF;
	IF DEL_dm IS NULL OR DEL_dm = '' THEN SET DEL_dm = '0000-00-00 00:00:00';	END IF;
	IF DEL_nm IS NULL OR DEL_nm = '' THEN SET DEL_nm = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblResource`
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
	IF	_Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblResource`
			WHERE
				Resrc_tp	= Resrc_tp

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
			FROM	`tblResource`
			WHERE
				Resrc_tp	= Resrc_tp
			AND	Resrc_nm	= Resrc_nm

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
			FROM	`tblResource`
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
				Resrc_nm	= Resrc_nm
			OR	Resrc_nm	= '-2147483647'
				)
			AND	(
				Resrc_tx	LIKE Resrc_tx
			OR	Resrc_tx	LIKE '-2147483647'
				)
			AND	(
				ADD_dm	= ADD_dm
			OR	ADD_dm	= '0000-00-00 00:00:00'
				)
			AND	(
				ADD_nm	= ADD_nm
			OR	ADD_nm	= '-2147483647'
				)
			AND	(
				UPD_dm	= UPD_dm
			OR	UPD_dm	= '0000-00-00 00:00:00'
				)
			AND	(
				UPD_nm	= UPD_nm
			OR	UPD_nm	= '-2147483647'
				)
			AND	(
				DEL_dm	= DEL_dm
			OR	DEL_dm	= '0000-00-00 00:00:00'
				)
			AND	(
				DEL_nm	= DEL_nm
			OR	DEL_nm	= '-2147483647'
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

