DROP PROCEDURE IF EXISTS	`rspapp_language`
;

DELIMITER //
CREATE PROCEDURE	rspapp_language
(
	locale		varchar(128)		
,	language		varchar(128)		
,	country		varchar(128)		
,	status		varchar(128)		

,		Key_cd		VARCHAR(16)		-- = 'PK'	-- Search key code
,	OUT 	RowExists_fg	TINYINT	
)
BEGIN
/*
**	Name:		rspapp_language
**	Type:		DB API Procedure: Referential 
**	Purpose:	Check existence of a record in app_language
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
	IF locale IS NULL OR locale = '' THEN SET locale = '-2147483647';	END IF;
	IF language IS NULL OR language = '' THEN SET language = '-2147483647';	END IF;
	IF country IS NULL OR country = '' THEN SET country = '-2147483647';	END IF;
	IF status IS NULL OR status = '' THEN SET status = '-2147483647';	END IF;

	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF 	Key_cd	= 'PK'
	THEN
		IF
		EXISTS
		(
			SELECT	1
			FROM	app_language
			WHERE
				app_language.locale	= locale

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
			FROM	app_language
			WHERE
				(
				locale	= locale
			OR	locale	= '-2147483647'
				)
			AND	(
				language	= language
			OR	language	= '-2147483647'
				)
			AND	(
				country	= country
			OR	country	= '-2147483647'
				)
			AND	(
				status	= status
			OR	status	= '-2147483647'
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

