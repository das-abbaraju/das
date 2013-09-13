DROP PROCEDURE IF EXISTS	`rspItem`
;

DELIMITER //
CREATE PROCEDURE	rspItem
(
	Item_id		int signed		
,	Item_tp		varchar(64)		
,	Item_nm		varchar(256)		
,	Item_cd		varchar(128)		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspItem
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblItem
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
	IF Item_id IS NULL OR Item_id = 0 THEN SET Item_id =  -2147483647;	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem
			WHERE
				tblItem.Item_id	= Item_id
			AND	tblItem.Item_tp	= Item_tp

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
			FROM	tblItem
			WHERE
				tblItem.Item_id	= Item_id
			AND	tblItem.Item_tp	= Item_tp

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
			FROM	tblItem
			WHERE
				tblItem.Item_tp	= Item_tp

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
			FROM	tblItem
			WHERE
				tblItem.Item_tp	= Item_tp
			AND	tblItem.Item_nm	= Item_nm

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
			FROM	tblItem
			WHERE
				(
				Item_id	= Item_id
			OR	Item_id	=  -2147483647
				)
			AND	(
				Item_tp	= Item_tp
			OR	Item_tp	= '-2147483647'
				)
			AND	(
				Item_nm	= Item_nm
			OR	Item_nm	= '-2147483647'
				)
			AND	(
				Item_cd	= Item_cd
			OR	Item_cd	= '-2147483647'
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

