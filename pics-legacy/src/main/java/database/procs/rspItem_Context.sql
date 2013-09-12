DROP PROCEDURE IF EXISTS	`rspItem_Context`
;

DELIMITER //
CREATE PROCEDURE	rspItem_Context
(
	Item_id		int signed		
,	Item_tp		varchar(64)		
,	Context_id		int signed		
,	Context_tp		varchar(64)		
,	Order_id		int signed		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspItem_Context
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in tblItem_Context
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
	IF Context_id IS NULL OR Context_id = 0 THEN SET Context_id =  -2147483647;	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF Order_id IS NULL OR Order_id = 0 THEN SET Order_id =  -2147483647;	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	tblItem_Context
			WHERE
				tblItem_Context.Item_id	= Item_id
			AND	tblItem_Context.Item_tp	= Item_tp
			AND	tblItem_Context.Context_id	= Context_id
			AND	tblItem_Context.Context_tp	= Context_tp

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
			FROM	tblItem_Context
			WHERE
				tblItem_Context.Item_id	= Item_id
			AND	tblItem_Context.Item_tp	= Item_tp

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
			FROM	tblItem_Context
			WHERE
				tblItem_Context.Context_id	= Context_id
			AND	tblItem_Context.Context_tp	= Context_tp

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
			FROM	tblItem_Context
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
				Context_id	= Context_id
			OR	Context_id	=  -2147483647
				)
			AND	(
				Context_tp	= Context_tp
			OR	Context_tp	= '-2147483647'
				)
			AND	(
				Order_id	= Order_id
			OR	Order_id	=  -2147483647
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

