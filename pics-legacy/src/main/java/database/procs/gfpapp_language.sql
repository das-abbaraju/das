DROP PROCEDURE IF EXISTS	`gfpapp_language`
;

DELIMITER //
CREATE PROCEDURE	gfpapp_language
(
	Locale_cd		varchar(128)		-- PK1 AK1
,	Language_cd		varchar(128)	
,	Country_cd		varchar(128)	
,	Status_nm		varchar(128)	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpapp_language
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwapp_language
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwapp_language';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpapp_language';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Locale_cd IS NULL OR Locale_cd = '' THEN SET Locale_cd = '-2147483647';	END IF;
	IF Language_cd IS NULL OR Language_cd = '' THEN SET Language_cd = '-2147483647';	END IF;
	IF Country_cd IS NULL OR Country_cd = '' THEN SET Country_cd = '-2147483647';	END IF;
	IF Status_nm IS NULL OR Status_nm = '' THEN SET Status_nm = '-2147483647';	END IF;

	#######################################################################
	-- Check Security
	#######################################################################
/*	EXECUTE	RETURN		= spSecurityCheck
		SYSTABLE	= SYSTABLE
	,	SYSRIGHT	= SYSRIGHT

	IF
	(
		RETURN	<> 0
	)
	BEGIN
		EXECUTE	STATUS		= errFailedSecurity
			Proc_nm	= Proc_nm
		,	Table_nm	= SYSTABLE
		,	Action_nm	= SYSRIGHT
		RETURN	STATUS
	END
*/
	#######################################################################
	-- Primary Key lookup
	#######################################################################
	IF	Key_cd = 'PK'
	THEN
		SELECT
			vwapp_language.Locale_cd
		,	vwapp_language.Language_cd
		,	vwapp_language.Country_cd
		,	vwapp_language.Status_nm
		FROM
			vwapp_language
		WHERE
			vwapp_language.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	--   NO SUPER-SET OR PARENT TABLE FOR THIS OBJECT TO REFERENCE
	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwapp_language.Locale_cd
		,	vwapp_language.Language_cd
		,	vwapp_language.Country_cd
		,	vwapp_language.Status_nm
		FROM
			vwapp_language
		WHERE
			vwapp_language.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Search Key lookup
	#######################################################################
	--   NO UI SEARCH KEY(S) DEFINED FOR THIS OBJECT
	#######################################################################
	-- Attribute lookup
	#######################################################################
	IF	Key_cd = 'AL'
	THEN
		SELECT
			vwapp_language.Locale_cd
		,	vwapp_language.Language_cd
		,	vwapp_language.Country_cd
		,	vwapp_language.Status_nm
		FROM
			vwapp_language
		WHERE
			(
			vwapp_language.Locale_cd	LIKE CONCAT('%', Locale_cd, '%')
		OR	Locale_cd	= '-2147483647'
			)
		AND	(
			vwapp_language.Language_cd	LIKE CONCAT('%', Language_cd, '%')
		OR	Language_cd	= '-2147483647'
			)
		AND	(
			vwapp_language.Country_cd	LIKE CONCAT('%', Country_cd, '%')
		OR	Country_cd	= '-2147483647'
			)
		AND	(
			vwapp_language.Status_nm	LIKE CONCAT('%', Status_nm, '%')
		OR	Status_nm	= '-2147483647'
			)

		;
		LEAVE GFP;
	END IF;
	#######################################################################
END	GFP
;
###############################################################################
END
//
DELIMITER ;
;

