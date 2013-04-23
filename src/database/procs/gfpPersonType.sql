DROP PROCEDURE IF EXISTS	`gfpPersonType`
;

DELIMITER //
CREATE PROCEDURE	gfpPersonType
(
	Person_tp		varchar(80)		-- PK1 
,	ParentPerson_tp		varchar(80)	
,	PersonType_tx		mediumtext	
,	PersonTypeLeft_id		int signed	
,	PersonTypeRight_id		int signed	
,	PersonTypeLevel_id		int signed	
,	PersonTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpPersonType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwPersonType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwPersonType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpPersonType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Person_tp IS NULL OR Person_tp = '' THEN SET Person_tp = '-2147483647';	END IF;
	IF ParentPerson_tp IS NULL OR ParentPerson_tp = '' THEN SET ParentPerson_tp = '-2147483647';	END IF;
	IF PersonType_tx IS NULL OR PersonType_tx = '' THEN SET PersonType_tx = '-2147483647';	END IF;
	IF PersonTypeLeft_id IS NULL OR PersonTypeLeft_id = 0 THEN SET PersonTypeLeft_id =  -2147483647;	END IF;
	IF PersonTypeRight_id IS NULL OR PersonTypeRight_id = 0 THEN SET PersonTypeRight_id =  -2147483647;	END IF;
	IF PersonTypeLevel_id IS NULL OR PersonTypeLevel_id = 0 THEN SET PersonTypeLevel_id =  -2147483647;	END IF;
	IF PersonTypeOrder_id IS NULL OR PersonTypeOrder_id = 0 THEN SET PersonTypeOrder_id =  -2147483647;	END IF;

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
			vwPersonType.Person_tp
		,	vwPersonType.ParentPerson_tp
		,	vwPersonType.PersonType_tx
		,	vwPersonType.PersonTypeLeft_id
		,	vwPersonType.PersonTypeRight_id
		,	vwPersonType.PersonTypeLevel_id
		,	vwPersonType.PersonTypeOrder_id
		FROM
			vwPersonType
		WHERE
			vwPersonType.Person_tp	= Person_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwPersonType.Person_tp
		,	vwPersonType.ParentPerson_tp
		,	vwPersonType.PersonType_tx
		,	vwPersonType.PersonTypeLeft_id
		,	vwPersonType.PersonTypeRight_id
		,	vwPersonType.PersonTypeLevel_id
		,	vwPersonType.PersonTypeOrder_id
		FROM
			vwPersonType
		WHERE
			vwPersonType.Person_tp	= Person_tp

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	-- NO ALTERNATE KEY DEFINED FOR THIS OBJECT
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
			vwPersonType.Person_tp
		,	vwPersonType.ParentPerson_tp
		,	vwPersonType.PersonType_tx
		,	vwPersonType.PersonTypeLeft_id
		,	vwPersonType.PersonTypeRight_id
		,	vwPersonType.PersonTypeLevel_id
		,	vwPersonType.PersonTypeOrder_id
		FROM
			vwPersonType
		WHERE
			(
			vwPersonType.Person_tp	= Person_tp
		OR	Person_tp	= '-2147483647'
			)
		AND	(
			vwPersonType.ParentPerson_tp	= ParentPerson_tp
		OR	ParentPerson_tp	= '-2147483647'
			)
		AND	(
			vwPersonType.PersonType_tx	LIKE CONCAT('%', PersonType_tx, '%')
		OR	PersonType_tx	LIKE '-2147483647'
			)
		AND	(
			vwPersonType.PersonTypeLeft_id	= PersonTypeLeft_id
		OR	PersonTypeLeft_id	=  -2147483647
			)
		AND	(
			vwPersonType.PersonTypeRight_id	= PersonTypeRight_id
		OR	PersonTypeRight_id	=  -2147483647
			)
		AND	(
			vwPersonType.PersonTypeLevel_id	= PersonTypeLevel_id
		OR	PersonTypeLevel_id	=  -2147483647
			)
		AND	(
			vwPersonType.PersonTypeOrder_id	= PersonTypeOrder_id
		OR	PersonTypeOrder_id	=  -2147483647
			)

		;
		LEAVE GFP;
	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

