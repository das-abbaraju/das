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
DROP PROCEDURE IF EXISTS	`rspPersonType`
;

DELIMITER //
CREATE PROCEDURE	rspPersonType
(
	Person_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspPersonType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblPersonType
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
	IF Person_tp IS NULL OR Person_tp = '' THEN SET Person_tp = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblPersonType`
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
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblPersonType`
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
	-- Attribute lookup
	#######################################################################
	IF	_Key_cd	= 'AL'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblPersonType`
			WHERE
				(
				Person_tp	= Person_tp
			OR	Person_tp	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspReport`
;

DELIMITER //
CREATE PROCEDURE	rspReport
(
	Report_id		int signed		
,	Report_tp		varchar(80)		
,	Report_nm		varchar(128)		
,	Report_cd		varchar(48)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspReport
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblReport
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
	IF Report_id IS NULL OR Report_id = 0 THEN SET Report_id =  -2147483647;	END IF;
	IF Report_tp IS NULL OR Report_tp = '' THEN SET Report_tp = '-2147483647';	END IF;
	IF Report_nm IS NULL OR Report_nm = '' THEN SET Report_nm = '-2147483647';	END IF;
	IF Report_cd IS NULL OR Report_cd = '' THEN SET Report_cd = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblReport`
			WHERE
				Report_id	= Report_id
			AND	Report_tp	= Report_tp

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
			FROM	`tblReport`
			WHERE
				Report_id	= Report_id
			AND	Report_tp	= Report_tp

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
			FROM	`tblReport`
			WHERE
				Report_tp	= Report_tp

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
			FROM	`tblReport`
			WHERE
				Report_tp	= Report_tp
			AND	Report_nm	= Report_nm

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
			FROM	`tblReport`
			WHERE
				(
				Report_id	= Report_id
			OR	Report_id	=  -2147483647
				)
			AND	(
				Report_tp	= Report_tp
			OR	Report_tp	= '-2147483647'
				)
			AND	(
				Report_nm	= Report_nm
			OR	Report_nm	= '-2147483647'
				)
			AND	(
				Report_cd	= Report_cd
			OR	Report_cd	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspReportType`
;

DELIMITER //
CREATE PROCEDURE	rspReportType
(
	Report_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspReportType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblReportType
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
	IF Report_tp IS NULL OR Report_tp = '' THEN SET Report_tp = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblReportType`
			WHERE
				Report_tp	= Report_tp

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
			FROM	`tblReportType`
			WHERE
				Report_tp	= Report_tp

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
			FROM	`tblReportType`
			WHERE
				(
				Report_tp	= Report_tp
			OR	Report_tp	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspResourceType`
;

DELIMITER //
CREATE PROCEDURE	rspResourceType
(
	Resrc_tp		varchar(80)		
,	ParentResrc_tp		varchar(80)		
,	ResrcType_tx		mediumtext		
,	Left_id		int signed		
,	Right_id		int signed		
,	Level_id		int signed		
,	Order_id		int signed		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspResourceType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblResourceType
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
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF ParentResrc_tp IS NULL OR ParentResrc_tp = '' THEN SET ParentResrc_tp = '-2147483647';	END IF;
	IF ResrcType_tx IS NULL OR ResrcType_tx = '' THEN SET ResrcType_tx = '-2147483647';	END IF;
	IF Left_id IS NULL OR Left_id = 0 THEN SET Left_id =  -2147483647;	END IF;
	IF Right_id IS NULL OR Right_id = 0 THEN SET Right_id =  -2147483647;	END IF;
	IF Level_id IS NULL OR Level_id = 0 THEN SET Level_id =  -2147483647;	END IF;
	IF Order_id IS NULL OR Order_id = 0 THEN SET Order_id =  -2147483647;	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblResourceType`
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
			FROM	`tblResourceType`
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
	-- Attribute lookup
	#######################################################################
	IF	_Key_cd	= 'AL'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblResourceType`
			WHERE
				(
				Resrc_tp	= Resrc_tp
			OR	Resrc_tp	= '-2147483647'
				)
			AND	(
				ParentResrc_tp	= ParentResrc_tp
			OR	ParentResrc_tp	= '-2147483647'
				)
			AND	(
				ResrcType_tx	LIKE ResrcType_tx
			OR	ResrcType_tx	LIKE '-2147483647'
				)
			AND	(
				Left_id	= Left_id
			OR	Left_id	=  -2147483647
				)
			AND	(
				Right_id	= Right_id
			OR	Right_id	=  -2147483647
				)
			AND	(
				Level_id	= Level_id
			OR	Level_id	=  -2147483647
				)
			AND	(
				Order_id	= Order_id
			OR	Order_id	=  -2147483647
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
DROP PROCEDURE IF EXISTS	`rspRightType`
;

DELIMITER //
CREATE PROCEDURE	rspRightType
(
	Right_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspRightType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblRightType
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
			FROM	`tblRightType`
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
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblRightType`
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
			FROM	`tblRightType`
			WHERE
				(
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
DROP PROCEDURE IF EXISTS	`rspRoleType`
;

DELIMITER //
CREATE PROCEDURE	rspRoleType
(
	Role_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspRoleType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblRoleType
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
			FROM	`tblRoleType`
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
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblRoleType`
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
	-- Attribute lookup
	#######################################################################
	IF	_Key_cd	= 'AL'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblRoleType`
			WHERE
				(
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
DROP PROCEDURE IF EXISTS	`rspUserType`
;

DELIMITER //
CREATE PROCEDURE	rspUserType
(
	User_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspUserType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblUserType
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
	IF User_tp IS NULL OR User_tp = '' THEN SET User_tp = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUserType`
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
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUserType`
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
	-- Attribute lookup
	#######################################################################
	IF	_Key_cd	= 'AL'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUserType`
			WHERE
				(
				User_tp	= User_tp
			OR	User_tp	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`rspResourceType_RightType`
;

DELIMITER //
CREATE PROCEDURE	rspResourceType_RightType
(
	Resrc_tp		varchar(80)		
,	Right_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspResourceType_RightType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblResourceType_RightType
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
			FROM	`tblResourceType_RightType`
			WHERE
				Resrc_tp	= Resrc_tp
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
			FROM	`tblResourceType_RightType`
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
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK2'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblResourceType_RightType`
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
			FROM	`tblResourceType_RightType`
			WHERE
				(
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
DROP PROCEDURE IF EXISTS	`rspRole_Resource_RightType`
;

DELIMITER //
CREATE PROCEDURE	rspRole_Resource_RightType
(
	Role_id		int signed		
,	Role_tp		varchar(80)		
,	Resrc_id		int signed		
,	Resrc_tp		varchar(80)		
,	Right_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspRole_Resource_RightType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblRole_Resource_RightType
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
			FROM	`tblRole_Resource_RightType`
			WHERE
				Role_id	= Role_id
			AND	Role_tp	= Role_tp
			AND	Resrc_id	= Resrc_id
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
			FROM	`tblRole_Resource_RightType`
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
			FROM	`tblRole_Resource_RightType`
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
	IF	_Key_cd = 'FK3'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblRole_Resource_RightType`
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
	-- Attribute lookup
	#######################################################################
	IF	_Key_cd	= 'AL'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblRole_Resource_RightType`
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
DROP PROCEDURE IF EXISTS	`rspRole_ResourceType_RightType`
;

DELIMITER //
CREATE PROCEDURE	rspRole_ResourceType_RightType
(
	Role_id		int signed		
,	Role_tp		varchar(80)		
,	Resrc_tp		varchar(80)		
,	Right_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspRole_ResourceType_RightType
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblRole_ResourceType_RightType
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
			FROM	`tblRole_ResourceType_RightType`
			WHERE
				Role_id	= Role_id
			AND	Role_tp	= Role_tp
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
			FROM	`tblRole_ResourceType_RightType`
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
			FROM	`tblRole_ResourceType_RightType`
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
	-- Foreign Key lookup
	#######################################################################
	IF	_Key_cd = 'FK3'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblRole_ResourceType_RightType`
			WHERE
				Resrc_tp	= Resrc_tp
			AND	Right_tp	= Right_tp

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
			FROM	`tblRole_ResourceType_RightType`
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
DROP PROCEDURE IF EXISTS	`rspUser_Person`
;

DELIMITER //
CREATE PROCEDURE	rspUser_Person
(
	User_id		int signed		
,	User_tp		varchar(80)		
,	Person_id		int signed		
,	Person_tp		varchar(80)		

,		_Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	_RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspUser_Person
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblUser_Person
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
	IF Person_id IS NULL OR Person_id = 0 THEN SET Person_id =  -2147483647;	END IF;
	IF Person_tp IS NULL OR Person_tp = '' THEN SET Person_tp = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	_Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUser_Person`
			WHERE
				User_id	= User_id
			AND	User_tp	= User_tp
			AND	Person_id	= Person_id
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
			FROM	`tblUser_Person`
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
			FROM	`tblUser_Person`
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
	-- Attribute lookup
	#######################################################################
	IF	_Key_cd	= 'AL'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	`tblUser_Person`
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
				Person_id	= Person_id
			OR	Person_id	=  -2147483647
				)
			AND	(
				Person_tp	= Person_tp
			OR	Person_tp	= '-2147483647'
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

