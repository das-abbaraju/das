DROP PROCEDURE IF EXISTS	`rspContext`
;

DELIMITER //
CREATE PROCEDURE	rspContext
(
	Context_id		int signed		
,	Context_tp		varchar(64)		
,	Context_nm		varchar(256)		
,	Context_cd		varchar(128)		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspContext
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblContext
**	Author:		Solomon S. Shacter
**	Generated:	4/29/2013
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
###############################################################################
RSP:
BEGIN
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'PK';	END IF;
	IF Context_id IS NULL OR Context_id = 0 THEN SET Context_id =  -2147483647;	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF Context_nm IS NULL OR Context_nm = '' THEN SET Context_nm = '-2147483647';	END IF;
	IF Context_cd IS NULL OR Context_cd = '' THEN SET Context_cd = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblContext
			WHERE
				tblContext.Context_id	= Context_id
			AND	tblContext.Context_tp	= Context_tp

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;

	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd = 'FK1'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblContext
			WHERE
				tblContext.Context_id	= Context_id
			AND	tblContext.Context_tp	= Context_tp

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;

	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd = 'FK2'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblContext
			WHERE
				tblContext.Context_tp	= Context_tp

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;


	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblContext
			WHERE
				tblContext.Context_tp	= Context_tp
			AND	tblContext.Context_nm	= Context_nm

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;

	#######################################################################
	-- Attribute lookup
	#######################################################################
	IF	Key_cd	= 'AL'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblContext
			WHERE
				(
				Context_id	= Context_id
			OR	Context_id	=  -2147483647
				)
			AND	(
				Context_tp	= Context_tp
			OR	Context_tp	= '-2147483647'
				)
			AND	(
				Context_nm	= Context_nm
			OR	Context_nm	= '-2147483647'
				)
			AND	(
				Context_cd	= Context_cd
			OR	Context_cd	= '-2147483647'
				)

		)
		THEN
			SET RowExists_fg	= 1;
		ELSE
			SET RowExists_fg	= 0;
		END IF;
		LEAVE RSP;
	END IF;
	#######################################################################
END	RSP
;
###############################################################################
END
//
DELIMITER ;
;

