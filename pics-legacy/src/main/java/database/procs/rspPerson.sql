DROP PROCEDURE IF EXISTS	`rspPerson`
;

DELIMITER //
CREATE PROCEDURE	rspPerson
(
	Person_id		int signed		
,	Person_tp		varchar(80)		
,	Person_nm		varchar(128)		
,	First_nm		varchar(128)		
,	Last_nm		varchar(128)		
,	Middle_nm		varchar(128)		
,	Gender_cd		varchar(48)		
,	FirstSNDX_cd		varchar(48)		
,	LastSNDX_cd		varchar(48)		
,	Birth_dm		datetime		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspPerson
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblPerson
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
	IF Person_id IS NULL OR Person_id = 0 THEN SET Person_id =  -2147483647;	END IF;
	IF Person_tp IS NULL OR Person_tp = '' THEN SET Person_tp = '-2147483647';	END IF;
	IF Person_nm IS NULL OR Person_nm = '' THEN SET Person_nm = '-2147483647';	END IF;
	IF First_nm IS NULL OR First_nm = '' THEN SET First_nm = '-2147483647';	END IF;
	IF Last_nm IS NULL OR Last_nm = '' THEN SET Last_nm = '-2147483647';	END IF;
	IF Middle_nm IS NULL OR Middle_nm = '' THEN SET Middle_nm = '-2147483647';	END IF;
	IF Gender_cd IS NULL OR Gender_cd = '' THEN SET Gender_cd = '-2147483647';	END IF;
	IF FirstSNDX_cd IS NULL OR FirstSNDX_cd = '' THEN SET FirstSNDX_cd = '-2147483647';	END IF;
	IF LastSNDX_cd IS NULL OR LastSNDX_cd = '' THEN SET LastSNDX_cd = '-2147483647';	END IF;
	IF Birth_dm IS NULL OR Birth_dm = '' THEN SET Birth_dm = '0000-00-00 00:00:00';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblPerson`
			WHERE
				Person_id	= Person_id
			AND	Person_tp	= Person_tp

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
			FROM	`tblPerson`
			WHERE
				Person_id	= Person_id
			AND	Person_tp	= Person_tp

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
			FROM	`tblPerson`
			WHERE
				Person_tp	= Person_tp

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
			FROM	`tblPerson`
			WHERE
				Person_tp	= Person_tp
			AND	Person_nm	= Person_nm

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
			FROM	`tblPerson`
			WHERE
				(
				Person_id	= Person_id
			OR	Person_id	=  -2147483647
				)
			AND	(
				Person_tp	= Person_tp
			OR	Person_tp	= '-2147483647'
				)
			AND	(
				Person_nm	= Person_nm
			OR	Person_nm	= '-2147483647'
				)
			AND	(
				First_nm	= First_nm
			OR	First_nm	= '-2147483647'
				)
			AND	(
				Last_nm	= Last_nm
			OR	Last_nm	= '-2147483647'
				)
			AND	(
				Middle_nm	= Middle_nm
			OR	Middle_nm	= '-2147483647'
				)
			AND	(
				Gender_cd	= Gender_cd
			OR	Gender_cd	= '-2147483647'
				)
			AND	(
				FirstSNDX_cd	= FirstSNDX_cd
			OR	FirstSNDX_cd	= '-2147483647'
				)
			AND	(
				LastSNDX_cd	= LastSNDX_cd
			OR	LastSNDX_cd	= '-2147483647'
				)
			AND	(
				Birth_dm	= Birth_dm
			OR	Birth_dm	= '0000-00-00 00:00:00'
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

