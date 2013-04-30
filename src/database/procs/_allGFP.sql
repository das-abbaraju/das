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
DROP PROCEDURE IF EXISTS	`gfpContext`
;

DELIMITER //
CREATE PROCEDURE	gfpContext
(
	Context_id		int signed		-- PK1 
,	Context_tp		varchar(64)		-- PK2 AK1
,	Context_nm		varchar(256)		--  AK2
,	Context_cd		varchar(128)	
,	Context_tx		mediumtext	
,	ContextADD_dm		datetime	
,	ContextADD_nm		varchar(256)	
,	ContextUPD_dm		datetime	
,	ContextUPD_nm		varchar(256)	
,	ContextDEL_dm		datetime	
,	ContextDEL_nm		varchar(256)	
,	ParentContext_tp		varchar(64)	
,	ContextType_tx		mediumtext	
,	ContextTypeLeft_id		int signed	
,	ContextTypeRight_id		int signed	
,	ContextTypeLevel_id		int signed	
,	ContextTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpContext
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwContext
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwContext';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpContext';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Context_id IS NULL OR Context_id = 0 THEN SET Context_id =  -2147483647;	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF Context_nm IS NULL OR Context_nm = '' THEN SET Context_nm = '-2147483647';	END IF;
	IF Context_cd IS NULL OR Context_cd = '' THEN SET Context_cd = '-2147483647';	END IF;
	IF Context_tx IS NULL OR Context_tx = '' THEN SET Context_tx = '-2147483647';	END IF;
	IF ContextADD_dm IS NULL OR ContextADD_dm = '' THEN SET ContextADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextADD_nm IS NULL OR ContextADD_nm = '' THEN SET ContextADD_nm = '-2147483647';	END IF;
	IF ContextUPD_dm IS NULL OR ContextUPD_dm = '' THEN SET ContextUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextUPD_nm IS NULL OR ContextUPD_nm = '' THEN SET ContextUPD_nm = '-2147483647';	END IF;
	IF ContextDEL_dm IS NULL OR ContextDEL_dm = '' THEN SET ContextDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextDEL_nm IS NULL OR ContextDEL_nm = '' THEN SET ContextDEL_nm = '-2147483647';	END IF;
	IF ParentContext_tp IS NULL OR ParentContext_tp = '' THEN SET ParentContext_tp = '-2147483647';	END IF;
	IF ContextType_tx IS NULL OR ContextType_tx = '' THEN SET ContextType_tx = '-2147483647';	END IF;
	IF ContextTypeLeft_id IS NULL OR ContextTypeLeft_id = 0 THEN SET ContextTypeLeft_id =  -2147483647;	END IF;
	IF ContextTypeRight_id IS NULL OR ContextTypeRight_id = 0 THEN SET ContextTypeRight_id =  -2147483647;	END IF;
	IF ContextTypeLevel_id IS NULL OR ContextTypeLevel_id = 0 THEN SET ContextTypeLevel_id =  -2147483647;	END IF;
	IF ContextTypeOrder_id IS NULL OR ContextTypeOrder_id = 0 THEN SET ContextTypeOrder_id =  -2147483647;	END IF;

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
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			vwContext.Context_id	= Context_id
		AND	vwContext.Context_tp	= Context_tp
		AND	vwContext.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			vwContext.Context_tp	= Context_tp
		AND	vwContext.Context_id	= Context_id
		AND	vwContext.Context_tp	= Context_tp
		AND	vwContext.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			vwContext.Context_tp	= Context_tp
		AND	vwContext.Context_tp	= Context_tp
		AND	vwContext.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			vwContext.Context_tp	= Context_tp
		AND	vwContext.Context_nm	= Context_nm
		AND	vwContext.ContextDEL_dm	IS NULL

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
			vwContext.Context_id
		,	vwContext.Context_tp
		,	vwContext.Context_nm
		,	vwContext.Context_cd
		,	vwContext.Context_tx
		,	vwContext.ContextADD_dm
		,	vwContext.ContextADD_nm
		,	vwContext.ContextUPD_dm
		,	vwContext.ContextUPD_nm
		,	vwContext.ContextDEL_dm
		,	vwContext.ContextDEL_nm
		,	vwContext.ParentContext_tp
		,	vwContext.ContextType_tx
		,	vwContext.ContextTypeLeft_id
		,	vwContext.ContextTypeRight_id
		,	vwContext.ContextTypeLevel_id
		,	vwContext.ContextTypeOrder_id
		FROM
			vwContext
		WHERE
			(
			vwContext.Context_id	= Context_id
		OR	Context_id	=  -2147483647
			)
		AND	(
			vwContext.Context_tp	= Context_tp
		OR	Context_tp	= '-2147483647'
			)
		AND	(
			vwContext.Context_nm	LIKE CONCAT('%', Context_nm, '%')
		OR	Context_nm	= '-2147483647'
			)
		AND	(
			vwContext.Context_cd	LIKE CONCAT('%', Context_cd, '%')
		OR	Context_cd	= '-2147483647'
			)
		AND	(
			vwContext.Context_tx	LIKE CONCAT('%', Context_tx, '%')
		OR	Context_tx	LIKE '-2147483647'
			)
		AND	(
			vwContext.ContextADD_dm	= ContextADD_dm
		OR	ContextADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwContext.ContextADD_nm	LIKE CONCAT('%', ContextADD_nm, '%')
		OR	ContextADD_nm	= '-2147483647'
			)
		AND	(
			vwContext.ContextUPD_dm	= ContextUPD_dm
		OR	ContextUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwContext.ContextUPD_nm	LIKE CONCAT('%', ContextUPD_nm, '%')
		OR	ContextUPD_nm	= '-2147483647'
			)
		AND	(
			vwContext.ContextDEL_dm	= ContextDEL_dm
		OR	ContextDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwContext.ContextDEL_nm	LIKE CONCAT('%', ContextDEL_nm, '%')
		OR	ContextDEL_nm	= '-2147483647'
			)
		AND	(
			vwContext.ParentContext_tp	= ParentContext_tp
		OR	ParentContext_tp	= '-2147483647'
			)
		AND	(
			vwContext.ContextType_tx	LIKE CONCAT('%', ContextType_tx, '%')
		OR	ContextType_tx	LIKE '-2147483647'
			)
		AND	(
			vwContext.ContextTypeLeft_id	= ContextTypeLeft_id
		OR	ContextTypeLeft_id	=  -2147483647
			)
		AND	(
			vwContext.ContextTypeRight_id	= ContextTypeRight_id
		OR	ContextTypeRight_id	=  -2147483647
			)
		AND	(
			vwContext.ContextTypeLevel_id	= ContextTypeLevel_id
		OR	ContextTypeLevel_id	=  -2147483647
			)
		AND	(
			vwContext.ContextTypeOrder_id	= ContextTypeOrder_id
		OR	ContextTypeOrder_id	=  -2147483647
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
DROP PROCEDURE IF EXISTS	`gfpContextType`
;

DELIMITER //
CREATE PROCEDURE	gfpContextType
(
	Context_tp		varchar(64)		-- PK1 
,	ParentContext_tp		varchar(64)	
,	ContextType_tx		mediumtext	
,	ContextTypeLeft_id		int signed	
,	ContextTypeRight_id		int signed	
,	ContextTypeLevel_id		int signed	
,	ContextTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpContextType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwContextType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwContextType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpContextType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF ParentContext_tp IS NULL OR ParentContext_tp = '' THEN SET ParentContext_tp = '-2147483647';	END IF;
	IF ContextType_tx IS NULL OR ContextType_tx = '' THEN SET ContextType_tx = '-2147483647';	END IF;
	IF ContextTypeLeft_id IS NULL OR ContextTypeLeft_id = 0 THEN SET ContextTypeLeft_id =  -2147483647;	END IF;
	IF ContextTypeRight_id IS NULL OR ContextTypeRight_id = 0 THEN SET ContextTypeRight_id =  -2147483647;	END IF;
	IF ContextTypeLevel_id IS NULL OR ContextTypeLevel_id = 0 THEN SET ContextTypeLevel_id =  -2147483647;	END IF;
	IF ContextTypeOrder_id IS NULL OR ContextTypeOrder_id = 0 THEN SET ContextTypeOrder_id =  -2147483647;	END IF;

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
			vwContextType.Context_tp
		,	vwContextType.ParentContext_tp
		,	vwContextType.ContextType_tx
		,	vwContextType.ContextTypeLeft_id
		,	vwContextType.ContextTypeRight_id
		,	vwContextType.ContextTypeLevel_id
		,	vwContextType.ContextTypeOrder_id
		FROM
			vwContextType
		WHERE
			vwContextType.Context_tp	= Context_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwContextType.Context_tp
		,	vwContextType.ParentContext_tp
		,	vwContextType.ContextType_tx
		,	vwContextType.ContextTypeLeft_id
		,	vwContextType.ContextTypeRight_id
		,	vwContextType.ContextTypeLevel_id
		,	vwContextType.ContextTypeOrder_id
		FROM
			vwContextType
		WHERE
			vwContextType.Context_tp	= Context_tp

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
			vwContextType.Context_tp
		,	vwContextType.ParentContext_tp
		,	vwContextType.ContextType_tx
		,	vwContextType.ContextTypeLeft_id
		,	vwContextType.ContextTypeRight_id
		,	vwContextType.ContextTypeLevel_id
		,	vwContextType.ContextTypeOrder_id
		FROM
			vwContextType
		WHERE
			(
			vwContextType.Context_tp	= Context_tp
		OR	Context_tp	= '-2147483647'
			)
		AND	(
			vwContextType.ParentContext_tp	= ParentContext_tp
		OR	ParentContext_tp	= '-2147483647'
			)
		AND	(
			vwContextType.ContextType_tx	LIKE CONCAT('%', ContextType_tx, '%')
		OR	ContextType_tx	LIKE '-2147483647'
			)
		AND	(
			vwContextType.ContextTypeLeft_id	= ContextTypeLeft_id
		OR	ContextTypeLeft_id	=  -2147483647
			)
		AND	(
			vwContextType.ContextTypeRight_id	= ContextTypeRight_id
		OR	ContextTypeRight_id	=  -2147483647
			)
		AND	(
			vwContextType.ContextTypeLevel_id	= ContextTypeLevel_id
		OR	ContextTypeLevel_id	=  -2147483647
			)
		AND	(
			vwContextType.ContextTypeOrder_id	= ContextTypeOrder_id
		OR	ContextTypeOrder_id	=  -2147483647
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
DROP PROCEDURE IF EXISTS	`gfpItem`
;

DELIMITER //
CREATE PROCEDURE	gfpItem
(
	Item_id		int signed		-- PK1 
,	Item_tp		varchar(64)		-- PK2 AK1
,	Item_nm		varchar(256)		--  AK2
,	Item_cd		varchar(128)	
,	Item_tx		mediumtext	
,	ItemADD_dm		datetime	
,	ItemADD_nm		varchar(256)	
,	ItemUPD_dm		datetime	
,	ItemUPD_nm		varchar(256)	
,	ItemDEL_dm		datetime	
,	ItemDEL_nm		varchar(256)	
,	ParentItem_tp		varchar(64)	
,	ItemType_tx		mediumtext	
,	ItemTypeLeft_id		int signed	
,	ItemTypeRight_id		int signed	
,	ItemTypeLevel_id		int signed	
,	ItemTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpItem
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItem
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItem';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItem';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Item_id IS NULL OR Item_id = 0 THEN SET Item_id =  -2147483647;	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;
	IF Item_tx IS NULL OR Item_tx = '' THEN SET Item_tx = '-2147483647';	END IF;
	IF ItemADD_dm IS NULL OR ItemADD_dm = '' THEN SET ItemADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemADD_nm IS NULL OR ItemADD_nm = '' THEN SET ItemADD_nm = '-2147483647';	END IF;
	IF ItemUPD_dm IS NULL OR ItemUPD_dm = '' THEN SET ItemUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemUPD_nm IS NULL OR ItemUPD_nm = '' THEN SET ItemUPD_nm = '-2147483647';	END IF;
	IF ItemDEL_dm IS NULL OR ItemDEL_dm = '' THEN SET ItemDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemDEL_nm IS NULL OR ItemDEL_nm = '' THEN SET ItemDEL_nm = '-2147483647';	END IF;
	IF ParentItem_tp IS NULL OR ParentItem_tp = '' THEN SET ParentItem_tp = '-2147483647';	END IF;
	IF ItemType_tx IS NULL OR ItemType_tx = '' THEN SET ItemType_tx = '-2147483647';	END IF;
	IF ItemTypeLeft_id IS NULL OR ItemTypeLeft_id = 0 THEN SET ItemTypeLeft_id =  -2147483647;	END IF;
	IF ItemTypeRight_id IS NULL OR ItemTypeRight_id = 0 THEN SET ItemTypeRight_id =  -2147483647;	END IF;
	IF ItemTypeLevel_id IS NULL OR ItemTypeLevel_id = 0 THEN SET ItemTypeLevel_id =  -2147483647;	END IF;
	IF ItemTypeOrder_id IS NULL OR ItemTypeOrder_id = 0 THEN SET ItemTypeOrder_id =  -2147483647;	END IF;

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
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			vwItem.Item_id	= Item_id
		AND	vwItem.Item_tp	= Item_tp
		AND	vwItem.ItemDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			vwItem.Item_tp	= Item_tp
		AND	vwItem.Item_id	= Item_id
		AND	vwItem.Item_tp	= Item_tp
		AND	vwItem.ItemDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			vwItem.Item_tp	= Item_tp
		AND	vwItem.Item_tp	= Item_tp
		AND	vwItem.ItemDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			vwItem.Item_tp	= Item_tp
		AND	vwItem.Item_nm	= Item_nm
		AND	vwItem.ItemDEL_dm	IS NULL

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
			vwItem.Item_id
		,	vwItem.Item_tp
		,	vwItem.Item_nm
		,	vwItem.Item_cd
		,	vwItem.Item_tx
		,	vwItem.ItemADD_dm
		,	vwItem.ItemADD_nm
		,	vwItem.ItemUPD_dm
		,	vwItem.ItemUPD_nm
		,	vwItem.ItemDEL_dm
		,	vwItem.ItemDEL_nm
		,	vwItem.ParentItem_tp
		,	vwItem.ItemType_tx
		,	vwItem.ItemTypeLeft_id
		,	vwItem.ItemTypeRight_id
		,	vwItem.ItemTypeLevel_id
		,	vwItem.ItemTypeOrder_id
		FROM
			vwItem
		WHERE
			(
			vwItem.Item_id	= Item_id
		OR	Item_id	=  -2147483647
			)
		AND	(
			vwItem.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItem.Item_nm	LIKE CONCAT('%', Item_nm, '%')
		OR	Item_nm	= '-2147483647'
			)
		AND	(
			vwItem.Item_cd	LIKE CONCAT('%', Item_cd, '%')
		OR	Item_cd	= '-2147483647'
			)
		AND	(
			vwItem.Item_tx	LIKE CONCAT('%', Item_tx, '%')
		OR	Item_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem.ItemADD_dm	= ItemADD_dm
		OR	ItemADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem.ItemADD_nm	LIKE CONCAT('%', ItemADD_nm, '%')
		OR	ItemADD_nm	= '-2147483647'
			)
		AND	(
			vwItem.ItemUPD_dm	= ItemUPD_dm
		OR	ItemUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem.ItemUPD_nm	LIKE CONCAT('%', ItemUPD_nm, '%')
		OR	ItemUPD_nm	= '-2147483647'
			)
		AND	(
			vwItem.ItemDEL_dm	= ItemDEL_dm
		OR	ItemDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem.ItemDEL_nm	LIKE CONCAT('%', ItemDEL_nm, '%')
		OR	ItemDEL_nm	= '-2147483647'
			)
		AND	(
			vwItem.ParentItem_tp	= ParentItem_tp
		OR	ParentItem_tp	= '-2147483647'
			)
		AND	(
			vwItem.ItemType_tx	LIKE CONCAT('%', ItemType_tx, '%')
		OR	ItemType_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem.ItemTypeLeft_id	= ItemTypeLeft_id
		OR	ItemTypeLeft_id	=  -2147483647
			)
		AND	(
			vwItem.ItemTypeRight_id	= ItemTypeRight_id
		OR	ItemTypeRight_id	=  -2147483647
			)
		AND	(
			vwItem.ItemTypeLevel_id	= ItemTypeLevel_id
		OR	ItemTypeLevel_id	=  -2147483647
			)
		AND	(
			vwItem.ItemTypeOrder_id	= ItemTypeOrder_id
		OR	ItemTypeOrder_id	=  -2147483647
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
DROP PROCEDURE IF EXISTS	`gfpItem_Context`
;

DELIMITER //
CREATE PROCEDURE	gfpItem_Context
(
	Item_id		int signed		-- PK1 
,	Item_tp		varchar(64)		-- PK2 AK1
,	Context_id		int signed		-- PK3 
,	Context_tp		varchar(64)		-- PK4 AK3
,	Order_id		int signed	
,	Item_nm		varchar(256)		--  AK2
,	Item_cd		varchar(128)	
,	Context_nm		varchar(256)		--  AK4
,	Context_cd		varchar(128)	
,	Item_tx		mediumtext	
,	ItemADD_dm		datetime	
,	ItemADD_nm		varchar(256)	
,	ItemUPD_dm		datetime	
,	ItemUPD_nm		varchar(256)	
,	ItemDEL_dm		datetime	
,	ItemDEL_nm		varchar(256)	
,	Context_tx		mediumtext	
,	ContextADD_dm		datetime	
,	ContextADD_nm		varchar(256)	
,	ContextUPD_dm		datetime	
,	ContextUPD_nm		varchar(256)	
,	ContextDEL_dm		datetime	
,	ContextDEL_nm		varchar(256)	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpItem_Context
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItem_Context
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItem_Context';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItem_Context';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Item_id IS NULL OR Item_id = 0 THEN SET Item_id =  -2147483647;	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF Context_id IS NULL OR Context_id = 0 THEN SET Context_id =  -2147483647;	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF Order_id IS NULL OR Order_id = 0 THEN SET Order_id =  -2147483647;	END IF;
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;
	IF Context_nm IS NULL OR Context_nm = '' THEN SET Context_nm = '-2147483647';	END IF;
	IF Context_cd IS NULL OR Context_cd = '' THEN SET Context_cd = '-2147483647';	END IF;
	IF Item_tx IS NULL OR Item_tx = '' THEN SET Item_tx = '-2147483647';	END IF;
	IF ItemADD_dm IS NULL OR ItemADD_dm = '' THEN SET ItemADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemADD_nm IS NULL OR ItemADD_nm = '' THEN SET ItemADD_nm = '-2147483647';	END IF;
	IF ItemUPD_dm IS NULL OR ItemUPD_dm = '' THEN SET ItemUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemUPD_nm IS NULL OR ItemUPD_nm = '' THEN SET ItemUPD_nm = '-2147483647';	END IF;
	IF ItemDEL_dm IS NULL OR ItemDEL_dm = '' THEN SET ItemDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemDEL_nm IS NULL OR ItemDEL_nm = '' THEN SET ItemDEL_nm = '-2147483647';	END IF;
	IF Context_tx IS NULL OR Context_tx = '' THEN SET Context_tx = '-2147483647';	END IF;
	IF ContextADD_dm IS NULL OR ContextADD_dm = '' THEN SET ContextADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextADD_nm IS NULL OR ContextADD_nm = '' THEN SET ContextADD_nm = '-2147483647';	END IF;
	IF ContextUPD_dm IS NULL OR ContextUPD_dm = '' THEN SET ContextUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextUPD_nm IS NULL OR ContextUPD_nm = '' THEN SET ContextUPD_nm = '-2147483647';	END IF;
	IF ContextDEL_dm IS NULL OR ContextDEL_dm = '' THEN SET ContextDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ContextDEL_nm IS NULL OR ContextDEL_nm = '' THEN SET ContextDEL_nm = '-2147483647';	END IF;

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
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			vwItem_Context.Item_id	= Item_id
		AND	vwItem_Context.Item_tp	= Item_tp
		AND	vwItem_Context.Context_id	= Context_id
		AND	vwItem_Context.Context_tp	= Context_tp
		AND	vwItem_Context.ItemDEL_dm	IS NULL
		AND	vwItem_Context.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			vwItem_Context.Item_id	= Item_id
		AND	vwItem_Context.Item_tp	= Item_tp
		AND	vwItem_Context.ItemDEL_dm	IS NULL
		AND	vwItem_Context.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			vwItem_Context.Context_id	= Context_id
		AND	vwItem_Context.Context_tp	= Context_tp
		AND	vwItem_Context.ItemDEL_dm	IS NULL
		AND	vwItem_Context.ContextDEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			vwItem_Context.Item_tp	= Item_tp
		AND	vwItem_Context.Context_tp	= Context_tp
		AND	vwItem_Context.Item_nm	= Item_nm
		AND	vwItem_Context.Context_nm	= Context_nm
		AND	vwItem_Context.ItemDEL_dm	IS NULL
		AND	vwItem_Context.ContextDEL_dm	IS NULL

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
			vwItem_Context.Item_id
		,	vwItem_Context.Item_tp
		,	vwItem_Context.Context_id
		,	vwItem_Context.Context_tp
		,	vwItem_Context.Order_id
		,	vwItem_Context.Item_nm
		,	vwItem_Context.Item_cd
		,	vwItem_Context.Context_nm
		,	vwItem_Context.Context_cd
		,	vwItem_Context.Item_tx
		,	vwItem_Context.ItemADD_dm
		,	vwItem_Context.ItemADD_nm
		,	vwItem_Context.ItemUPD_dm
		,	vwItem_Context.ItemUPD_nm
		,	vwItem_Context.ItemDEL_dm
		,	vwItem_Context.ItemDEL_nm
		,	vwItem_Context.Context_tx
		,	vwItem_Context.ContextADD_dm
		,	vwItem_Context.ContextADD_nm
		,	vwItem_Context.ContextUPD_dm
		,	vwItem_Context.ContextUPD_nm
		,	vwItem_Context.ContextDEL_dm
		,	vwItem_Context.ContextDEL_nm
		FROM
			vwItem_Context
		WHERE
			(
			vwItem_Context.Item_id	= Item_id
		OR	Item_id	=  -2147483647
			)
		AND	(
			vwItem_Context.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context.Context_id	= Context_id
		OR	Context_id	=  -2147483647
			)
		AND	(
			vwItem_Context.Context_tp	= Context_tp
		OR	Context_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context.Order_id	= Order_id
		OR	Order_id	=  -2147483647
			)
		AND	(
			vwItem_Context.Item_nm	LIKE CONCAT('%', Item_nm, '%')
		OR	Item_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.Item_cd	LIKE CONCAT('%', Item_cd, '%')
		OR	Item_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context.Context_nm	LIKE CONCAT('%', Context_nm, '%')
		OR	Context_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.Context_cd	LIKE CONCAT('%', Context_cd, '%')
		OR	Context_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context.Item_tx	LIKE CONCAT('%', Item_tx, '%')
		OR	Item_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Context.ItemADD_dm	= ItemADD_dm
		OR	ItemADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ItemADD_nm	LIKE CONCAT('%', ItemADD_nm, '%')
		OR	ItemADD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.ItemUPD_dm	= ItemUPD_dm
		OR	ItemUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ItemUPD_nm	LIKE CONCAT('%', ItemUPD_nm, '%')
		OR	ItemUPD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.ItemDEL_dm	= ItemDEL_dm
		OR	ItemDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ItemDEL_nm	LIKE CONCAT('%', ItemDEL_nm, '%')
		OR	ItemDEL_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.Context_tx	LIKE CONCAT('%', Context_tx, '%')
		OR	Context_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Context.ContextADD_dm	= ContextADD_dm
		OR	ContextADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ContextADD_nm	LIKE CONCAT('%', ContextADD_nm, '%')
		OR	ContextADD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.ContextUPD_dm	= ContextUPD_dm
		OR	ContextUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ContextUPD_nm	LIKE CONCAT('%', ContextUPD_nm, '%')
		OR	ContextUPD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context.ContextDEL_dm	= ContextDEL_dm
		OR	ContextDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context.ContextDEL_nm	LIKE CONCAT('%', ContextDEL_nm, '%')
		OR	ContextDEL_nm	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`gfpItem_Context_Locale`
;

DELIMITER //
CREATE PROCEDURE	gfpItem_Context_Locale
(
	Item_id		int signed		-- PK1 
,	Item_tp		varchar(64)		-- PK2 AK1
,	Context_id		int signed		-- PK3 
,	Context_tp		varchar(64)		-- PK4 AK3
,	Locale_cd		varchar(128)		-- PK5 AK5
,	ItemEntry_tp		varchar(64)	
,	ItemEntry_tx		text	
,	Item_nm		varchar(256)		--  AK2
,	Item_cd		varchar(128)	
,	Context_nm		varchar(256)		--  AK4
,	Context_cd		varchar(128)	
,	Language_cd		varchar(128)	
,	Country_cd		varchar(128)	
,	Status_nm		varchar(128)	
,	Item_tx		mediumtext	
,	EFF_dm		datetime	
,	USE_dm		datetime	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpItem_Context_Locale
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItem_Context_Locale
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItem_Context_Locale';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItem_Context_Locale';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Item_id IS NULL OR Item_id = 0 THEN SET Item_id =  -2147483647;	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF Context_id IS NULL OR Context_id = 0 THEN SET Context_id =  -2147483647;	END IF;
	IF Context_tp IS NULL OR Context_tp = '' THEN SET Context_tp = '-2147483647';	END IF;
	IF Locale_cd IS NULL OR Locale_cd = '' THEN SET Locale_cd = '-2147483647';	END IF;
	IF ItemEntry_tp IS NULL OR ItemEntry_tp = '' THEN SET ItemEntry_tp = '-2147483647';	END IF;
	IF ItemEntry_tx IS NULL OR ItemEntry_tx = '' THEN SET ItemEntry_tx = '-2147483647';	END IF;
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;
	IF Context_nm IS NULL OR Context_nm = '' THEN SET Context_nm = '-2147483647';	END IF;
	IF Context_cd IS NULL OR Context_cd = '' THEN SET Context_cd = '-2147483647';	END IF;
	IF Language_cd IS NULL OR Language_cd = '' THEN SET Language_cd = '-2147483647';	END IF;
	IF Country_cd IS NULL OR Country_cd = '' THEN SET Country_cd = '-2147483647';	END IF;
	IF Status_nm IS NULL OR Status_nm = '' THEN SET Status_nm = '-2147483647';	END IF;
	IF Item_tx IS NULL OR Item_tx = '' THEN SET Item_tx = '-2147483647';	END IF;
	IF EFF_dm IS NULL OR EFF_dm = '' THEN SET EFF_dm = '0000-00-00 00:00:00';	END IF;
	IF USE_dm IS NULL OR USE_dm = '' THEN SET USE_dm = '0000-00-00 00:00:00';	END IF;

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
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			vwItem_Context_Locale.Item_id	= Item_id
		AND	vwItem_Context_Locale.Item_tp	= Item_tp
		AND	vwItem_Context_Locale.Context_id	= Context_id
		AND	vwItem_Context_Locale.Context_tp	= Context_tp
		AND	vwItem_Context_Locale.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			vwItem_Context_Locale.Item_id	= Item_id
		AND	vwItem_Context_Locale.Item_tp	= Item_tp
		AND	vwItem_Context_Locale.Context_id	= Context_id
		AND	vwItem_Context_Locale.Context_tp	= Context_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			vwItem_Context_Locale.Item_id	= Item_id
		AND	vwItem_Context_Locale.Item_tp	= Item_tp
		AND	vwItem_Context_Locale.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			vwItem_Context_Locale.Item_tp	= Item_tp
		AND	vwItem_Context_Locale.Context_tp	= Context_tp
		AND	vwItem_Context_Locale.Locale_cd	= Locale_cd
		AND	vwItem_Context_Locale.Item_nm	= Item_nm
		AND	vwItem_Context_Locale.Context_nm	= Context_nm

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
			vwItem_Context_Locale.Item_id
		,	vwItem_Context_Locale.Item_tp
		,	vwItem_Context_Locale.Context_id
		,	vwItem_Context_Locale.Context_tp
		,	vwItem_Context_Locale.Locale_cd
		,	vwItem_Context_Locale.ItemEntry_tp
		,	vwItem_Context_Locale.ItemEntry_tx
		,	vwItem_Context_Locale.Item_nm
		,	vwItem_Context_Locale.Item_cd
		,	vwItem_Context_Locale.Context_nm
		,	vwItem_Context_Locale.Context_cd
		,	vwItem_Context_Locale.Language_cd
		,	vwItem_Context_Locale.Country_cd
		,	vwItem_Context_Locale.Status_nm
		,	vwItem_Context_Locale.Item_tx
		,	vwItem_Context_Locale.EFF_dm
		,	vwItem_Context_Locale.USE_dm
		FROM
			vwItem_Context_Locale
		WHERE
			(
			vwItem_Context_Locale.Item_id	= Item_id
		OR	Item_id	=  -2147483647
			)
		AND	(
			vwItem_Context_Locale.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Context_id	= Context_id
		OR	Context_id	=  -2147483647
			)
		AND	(
			vwItem_Context_Locale.Context_tp	= Context_tp
		OR	Context_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Locale_cd	LIKE CONCAT('%', Locale_cd, '%')
		OR	Locale_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.ItemEntry_tp	= ItemEntry_tp
		OR	ItemEntry_tp	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.ItemEntry_tx	LIKE CONCAT('%', ItemEntry_tx, '%')
		OR	ItemEntry_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Item_nm	LIKE CONCAT('%', Item_nm, '%')
		OR	Item_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Item_cd	LIKE CONCAT('%', Item_cd, '%')
		OR	Item_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Context_nm	LIKE CONCAT('%', Context_nm, '%')
		OR	Context_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Context_cd	LIKE CONCAT('%', Context_cd, '%')
		OR	Context_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Language_cd	LIKE CONCAT('%', Language_cd, '%')
		OR	Language_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Country_cd	LIKE CONCAT('%', Country_cd, '%')
		OR	Country_cd	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Status_nm	LIKE CONCAT('%', Status_nm, '%')
		OR	Status_nm	= '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.Item_tx	LIKE CONCAT('%', Item_tx, '%')
		OR	Item_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Context_Locale.EFF_dm	= EFF_dm
		OR	EFF_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Context_Locale.USE_dm	= USE_dm
		OR	USE_dm	= '0000-00-00 00:00:00'
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
DROP PROCEDURE IF EXISTS	`gfpItem_Locale`
;

DELIMITER //
CREATE PROCEDURE	gfpItem_Locale
(
	Item_id		int signed		-- PK1 
,	Item_tp		varchar(64)		-- PK2 AK1
,	Locale_cd		varchar(128)		-- PK3 AK3
,	Entry_tp		varchar(64)	
,	Entry_tx		text	
,	EFF_dm		datetime	
,	USE_dm		datetime	
,	Item_nm		varchar(256)		--  AK2
,	Item_cd		varchar(128)	
,	Language_cd		varchar(128)	
,	Country_cd		varchar(128)	
,	Status_nm		varchar(128)	
,	Item_tx		mediumtext	
,	ParentItem_tp		varchar(64)	
,	ItemType_tx		mediumtext	
,	ItemADD_dm		datetime	
,	ItemADD_nm		varchar(256)	
,	ItemUPD_dm		datetime	
,	ItemUPD_nm		varchar(256)	
,	ItemDEL_dm		datetime	
,	ItemDEL_nm		varchar(256)	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpItem_Locale
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItem_Locale
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItem_Locale';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItem_Locale';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Item_id IS NULL OR Item_id = 0 THEN SET Item_id =  -2147483647;	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF Locale_cd IS NULL OR Locale_cd = '' THEN SET Locale_cd = '-2147483647';	END IF;
	IF Entry_tp IS NULL OR Entry_tp = '' THEN SET Entry_tp = '-2147483647';	END IF;
	IF Entry_tx IS NULL OR Entry_tx = '' THEN SET Entry_tx = '-2147483647';	END IF;
	IF EFF_dm IS NULL OR EFF_dm = '' THEN SET EFF_dm = '0000-00-00 00:00:00';	END IF;
	IF USE_dm IS NULL OR USE_dm = '' THEN SET USE_dm = '0000-00-00 00:00:00';	END IF;
	IF Item_nm IS NULL OR Item_nm = '' THEN SET Item_nm = '-2147483647';	END IF;
	IF Item_cd IS NULL OR Item_cd = '' THEN SET Item_cd = '-2147483647';	END IF;
	IF Language_cd IS NULL OR Language_cd = '' THEN SET Language_cd = '-2147483647';	END IF;
	IF Country_cd IS NULL OR Country_cd = '' THEN SET Country_cd = '-2147483647';	END IF;
	IF Status_nm IS NULL OR Status_nm = '' THEN SET Status_nm = '-2147483647';	END IF;
	IF Item_tx IS NULL OR Item_tx = '' THEN SET Item_tx = '-2147483647';	END IF;
	IF ParentItem_tp IS NULL OR ParentItem_tp = '' THEN SET ParentItem_tp = '-2147483647';	END IF;
	IF ItemType_tx IS NULL OR ItemType_tx = '' THEN SET ItemType_tx = '-2147483647';	END IF;
	IF ItemADD_dm IS NULL OR ItemADD_dm = '' THEN SET ItemADD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemADD_nm IS NULL OR ItemADD_nm = '' THEN SET ItemADD_nm = '-2147483647';	END IF;
	IF ItemUPD_dm IS NULL OR ItemUPD_dm = '' THEN SET ItemUPD_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemUPD_nm IS NULL OR ItemUPD_nm = '' THEN SET ItemUPD_nm = '-2147483647';	END IF;
	IF ItemDEL_dm IS NULL OR ItemDEL_dm = '' THEN SET ItemDEL_dm = '0000-00-00 00:00:00';	END IF;
	IF ItemDEL_nm IS NULL OR ItemDEL_nm = '' THEN SET ItemDEL_nm = '-2147483647';	END IF;

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
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			vwItem_Locale.Item_id	= Item_id
		AND	vwItem_Locale.Item_tp	= Item_tp
		AND	vwItem_Locale.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			vwItem_Locale.Item_id	= Item_id
		AND	vwItem_Locale.Item_tp	= Item_tp

		;
		LEAVE GFP;
	END IF;
	IF	Key_cd	= 'FK2'
	THEN
		SELECT
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			vwItem_Locale.Locale_cd	= Locale_cd

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			vwItem_Locale.Item_tp	= Item_tp
		AND	vwItem_Locale.Locale_cd	= Locale_cd
		AND	vwItem_Locale.Item_nm	= Item_nm

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
			vwItem_Locale.Item_id
		,	vwItem_Locale.Item_tp
		,	vwItem_Locale.Locale_cd
		,	vwItem_Locale.Entry_tp
		,	vwItem_Locale.Entry_tx
		,	vwItem_Locale.EFF_dm
		,	vwItem_Locale.USE_dm
		,	vwItem_Locale.Item_nm
		,	vwItem_Locale.Item_cd
		,	vwItem_Locale.Language_cd
		,	vwItem_Locale.Country_cd
		,	vwItem_Locale.Status_nm
		,	vwItem_Locale.Item_tx
		,	vwItem_Locale.ParentItem_tp
		,	vwItem_Locale.ItemType_tx
		,	vwItem_Locale.ItemADD_dm
		,	vwItem_Locale.ItemADD_nm
		,	vwItem_Locale.ItemUPD_dm
		,	vwItem_Locale.ItemUPD_nm
		,	vwItem_Locale.ItemDEL_dm
		,	vwItem_Locale.ItemDEL_nm
		FROM
			vwItem_Locale
		WHERE
			(
			vwItem_Locale.Item_id	= Item_id
		OR	Item_id	=  -2147483647
			)
		AND	(
			vwItem_Locale.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Locale_cd	LIKE CONCAT('%', Locale_cd, '%')
		OR	Locale_cd	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Entry_tp	= Entry_tp
		OR	Entry_tp	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Entry_tx	LIKE CONCAT('%', Entry_tx, '%')
		OR	Entry_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Locale.EFF_dm	= EFF_dm
		OR	EFF_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.USE_dm	= USE_dm
		OR	USE_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.Item_nm	LIKE CONCAT('%', Item_nm, '%')
		OR	Item_nm	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Item_cd	LIKE CONCAT('%', Item_cd, '%')
		OR	Item_cd	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Language_cd	LIKE CONCAT('%', Language_cd, '%')
		OR	Language_cd	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Country_cd	LIKE CONCAT('%', Country_cd, '%')
		OR	Country_cd	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Status_nm	LIKE CONCAT('%', Status_nm, '%')
		OR	Status_nm	= '-2147483647'
			)
		AND	(
			vwItem_Locale.Item_tx	LIKE CONCAT('%', Item_tx, '%')
		OR	Item_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Locale.ParentItem_tp	= ParentItem_tp
		OR	ParentItem_tp	= '-2147483647'
			)
		AND	(
			vwItem_Locale.ItemType_tx	LIKE CONCAT('%', ItemType_tx, '%')
		OR	ItemType_tx	LIKE '-2147483647'
			)
		AND	(
			vwItem_Locale.ItemADD_dm	= ItemADD_dm
		OR	ItemADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.ItemADD_nm	LIKE CONCAT('%', ItemADD_nm, '%')
		OR	ItemADD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Locale.ItemUPD_dm	= ItemUPD_dm
		OR	ItemUPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.ItemUPD_nm	LIKE CONCAT('%', ItemUPD_nm, '%')
		OR	ItemUPD_nm	= '-2147483647'
			)
		AND	(
			vwItem_Locale.ItemDEL_dm	= ItemDEL_dm
		OR	ItemDEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwItem_Locale.ItemDEL_nm	LIKE CONCAT('%', ItemDEL_nm, '%')
		OR	ItemDEL_nm	= '-2147483647'
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
DROP PROCEDURE IF EXISTS	`gfpItemType`
;

DELIMITER //
CREATE PROCEDURE	gfpItemType
(
	Item_tp		varchar(64)		-- PK1 
,	ParentItem_tp		varchar(64)	
,	ItemType_tx		mediumtext	
,	ItemTypeLeft_id		int signed	
,	ItemTypeRight_id		int signed	
,	ItemTypeLevel_id		int signed	
,	ItemTypeOrder_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpItemType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwItemType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwItemType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpItemType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Item_tp IS NULL OR Item_tp = '' THEN SET Item_tp = '-2147483647';	END IF;
	IF ParentItem_tp IS NULL OR ParentItem_tp = '' THEN SET ParentItem_tp = '-2147483647';	END IF;
	IF ItemType_tx IS NULL OR ItemType_tx = '' THEN SET ItemType_tx = '-2147483647';	END IF;
	IF ItemTypeLeft_id IS NULL OR ItemTypeLeft_id = 0 THEN SET ItemTypeLeft_id =  -2147483647;	END IF;
	IF ItemTypeRight_id IS NULL OR ItemTypeRight_id = 0 THEN SET ItemTypeRight_id =  -2147483647;	END IF;
	IF ItemTypeLevel_id IS NULL OR ItemTypeLevel_id = 0 THEN SET ItemTypeLevel_id =  -2147483647;	END IF;
	IF ItemTypeOrder_id IS NULL OR ItemTypeOrder_id = 0 THEN SET ItemTypeOrder_id =  -2147483647;	END IF;

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
			vwItemType.Item_tp
		,	vwItemType.ParentItem_tp
		,	vwItemType.ItemType_tx
		,	vwItemType.ItemTypeLeft_id
		,	vwItemType.ItemTypeRight_id
		,	vwItemType.ItemTypeLevel_id
		,	vwItemType.ItemTypeOrder_id
		FROM
			vwItemType
		WHERE
			vwItemType.Item_tp	= Item_tp

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwItemType.Item_tp
		,	vwItemType.ParentItem_tp
		,	vwItemType.ItemType_tx
		,	vwItemType.ItemTypeLeft_id
		,	vwItemType.ItemTypeRight_id
		,	vwItemType.ItemTypeLevel_id
		,	vwItemType.ItemTypeOrder_id
		FROM
			vwItemType
		WHERE
			vwItemType.Item_tp	= Item_tp

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
			vwItemType.Item_tp
		,	vwItemType.ParentItem_tp
		,	vwItemType.ItemType_tx
		,	vwItemType.ItemTypeLeft_id
		,	vwItemType.ItemTypeRight_id
		,	vwItemType.ItemTypeLevel_id
		,	vwItemType.ItemTypeOrder_id
		FROM
			vwItemType
		WHERE
			(
			vwItemType.Item_tp	= Item_tp
		OR	Item_tp	= '-2147483647'
			)
		AND	(
			vwItemType.ParentItem_tp	= ParentItem_tp
		OR	ParentItem_tp	= '-2147483647'
			)
		AND	(
			vwItemType.ItemType_tx	LIKE CONCAT('%', ItemType_tx, '%')
		OR	ItemType_tx	LIKE '-2147483647'
			)
		AND	(
			vwItemType.ItemTypeLeft_id	= ItemTypeLeft_id
		OR	ItemTypeLeft_id	=  -2147483647
			)
		AND	(
			vwItemType.ItemTypeRight_id	= ItemTypeRight_id
		OR	ItemTypeRight_id	=  -2147483647
			)
		AND	(
			vwItemType.ItemTypeLevel_id	= ItemTypeLevel_id
		OR	ItemTypeLevel_id	=  -2147483647
			)
		AND	(
			vwItemType.ItemTypeOrder_id	= ItemTypeOrder_id
		OR	ItemTypeOrder_id	=  -2147483647
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
DROP PROCEDURE IF EXISTS	`gfpResource`
;

DELIMITER //
CREATE PROCEDURE	gfpResource
(
	Resrc_id		int signed		-- PK1 
,	Resrc_tp		varchar(64)		-- PK2 AK1
,	Resrc_nm		varchar(256)		--  AK2
,	Resrc_tx		mediumtext	
,	ADD_dm		datetime	
,	ADD_nm		varchar(256)	
,	UPD_dm		datetime	
,	UPD_nm		varchar(256)	
,	DEL_dm		datetime	
,	DEL_nm		varchar(256)	
,	ParentResrc_tp		varchar(64)	
,	ResrcType_tx		mediumtext	
,	Left_id		int signed	
,	Right_id		int signed	
,	Level_id		int signed	
,	Order_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpResource
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwResource
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwResource';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpResource';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
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
	IF ParentResrc_tp IS NULL OR ParentResrc_tp = '' THEN SET ParentResrc_tp = '-2147483647';	END IF;
	IF ResrcType_tx IS NULL OR ResrcType_tx = '' THEN SET ResrcType_tx = '-2147483647';	END IF;
	IF Left_id IS NULL OR Left_id = 0 THEN SET Left_id =  -2147483647;	END IF;
	IF Right_id IS NULL OR Right_id = 0 THEN SET Right_id =  -2147483647;	END IF;
	IF Level_id IS NULL OR Level_id = 0 THEN SET Level_id =  -2147483647;	END IF;
	IF Order_id IS NULL OR Order_id = 0 THEN SET Order_id =  -2147483647;	END IF;

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
			vwResource.Resrc_id
		,	vwResource.Resrc_tp
		,	vwResource.Resrc_nm
		,	vwResource.Resrc_tx
		,	vwResource.ADD_dm
		,	vwResource.ADD_nm
		,	vwResource.UPD_dm
		,	vwResource.UPD_nm
		,	vwResource.DEL_dm
		,	vwResource.DEL_nm
		,	vwResource.ParentResrc_tp
		,	vwResource.ResrcType_tx
		,	vwResource.Left_id
		,	vwResource.Right_id
		,	vwResource.Level_id
		,	vwResource.Order_id
		FROM
			vwResource
		WHERE
			vwResource.Resrc_id	= Resrc_id
		AND	vwResource.Resrc_tp	= Resrc_tp
		AND	vwResource.DEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;
	#######################################################################
	-- Foreign Key lookup
	#######################################################################
	IF	Key_cd	= 'FK1'
	THEN
		SELECT
			vwResource.Resrc_id
		,	vwResource.Resrc_tp
		,	vwResource.Resrc_nm
		,	vwResource.Resrc_tx
		,	vwResource.ADD_dm
		,	vwResource.ADD_nm
		,	vwResource.UPD_dm
		,	vwResource.UPD_nm
		,	vwResource.DEL_dm
		,	vwResource.DEL_nm
		,	vwResource.ParentResrc_tp
		,	vwResource.ResrcType_tx
		,	vwResource.Left_id
		,	vwResource.Right_id
		,	vwResource.Level_id
		,	vwResource.Order_id
		FROM
			vwResource
		WHERE
			vwResource.Resrc_tp	= Resrc_tp
		AND	vwResource.DEL_dm	IS NULL

		;
		LEAVE GFP;
	END IF;

	#######################################################################
	-- Alternate Key lookup
	#######################################################################
	IF	Key_cd = 'AK'
	THEN
		SELECT
			vwResource.Resrc_id
		,	vwResource.Resrc_tp
		,	vwResource.Resrc_nm
		,	vwResource.Resrc_tx
		,	vwResource.ADD_dm
		,	vwResource.ADD_nm
		,	vwResource.UPD_dm
		,	vwResource.UPD_nm
		,	vwResource.DEL_dm
		,	vwResource.DEL_nm
		,	vwResource.ParentResrc_tp
		,	vwResource.ResrcType_tx
		,	vwResource.Left_id
		,	vwResource.Right_id
		,	vwResource.Level_id
		,	vwResource.Order_id
		FROM
			vwResource
		WHERE
			vwResource.Resrc_tp	= Resrc_tp
		AND	vwResource.Resrc_nm	= Resrc_nm
		AND	vwResource.DEL_dm	IS NULL

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
			vwResource.Resrc_id
		,	vwResource.Resrc_tp
		,	vwResource.Resrc_nm
		,	vwResource.Resrc_tx
		,	vwResource.ADD_dm
		,	vwResource.ADD_nm
		,	vwResource.UPD_dm
		,	vwResource.UPD_nm
		,	vwResource.DEL_dm
		,	vwResource.DEL_nm
		,	vwResource.ParentResrc_tp
		,	vwResource.ResrcType_tx
		,	vwResource.Left_id
		,	vwResource.Right_id
		,	vwResource.Level_id
		,	vwResource.Order_id
		FROM
			vwResource
		WHERE
			(
			vwResource.Resrc_id	= Resrc_id
		OR	Resrc_id	=  -2147483647
			)
		AND	(
			vwResource.Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
			)
		AND	(
			vwResource.Resrc_nm	LIKE CONCAT('%', Resrc_nm, '%')
		OR	Resrc_nm	= '-2147483647'
			)
		AND	(
			vwResource.Resrc_tx	LIKE CONCAT('%', Resrc_tx, '%')
		OR	Resrc_tx	LIKE '-2147483647'
			)
		AND	(
			vwResource.ADD_dm	= ADD_dm
		OR	ADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwResource.ADD_nm	LIKE CONCAT('%', ADD_nm, '%')
		OR	ADD_nm	= '-2147483647'
			)
		AND	(
			vwResource.UPD_dm	= UPD_dm
		OR	UPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwResource.UPD_nm	LIKE CONCAT('%', UPD_nm, '%')
		OR	UPD_nm	= '-2147483647'
			)
		AND	(
			vwResource.DEL_dm	= DEL_dm
		OR	DEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			vwResource.DEL_nm	LIKE CONCAT('%', DEL_nm, '%')
		OR	DEL_nm	= '-2147483647'
			)
		AND	(
			vwResource.ParentResrc_tp	= ParentResrc_tp
		OR	ParentResrc_tp	= '-2147483647'
			)
		AND	(
			vwResource.ResrcType_tx	LIKE CONCAT('%', ResrcType_tx, '%')
		OR	ResrcType_tx	LIKE '-2147483647'
			)
		AND	(
			vwResource.Left_id	= Left_id
		OR	Left_id	=  -2147483647
			)
		AND	(
			vwResource.Right_id	= Right_id
		OR	Right_id	=  -2147483647
			)
		AND	(
			vwResource.Level_id	= Level_id
		OR	Level_id	=  -2147483647
			)
		AND	(
			vwResource.Order_id	= Order_id
		OR	Order_id	=  -2147483647
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
DROP PROCEDURE IF EXISTS	`gfpResourceType`
;

DELIMITER //
CREATE PROCEDURE	gfpResourceType
(
	Resrc_tp		varchar(64)		-- PK1 AK1
,	ParentResrc_tp		varchar(64)	
,	ResrcType_tx		mediumtext	
,	Left_id		int signed	
,	Right_id		int signed	
,	Level_id		int signed	
,	Order_id		int signed	

,		Key_cd		VARCHAR(16)		-- Search key code
)
BEGIN
/*
**	Name:		gfpResourceType
**	Type:		DB API Stored Procedure: Get Filtered
**	Purpose:	To Get an active record or set of active records
**			from vwResourceType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/29/2013
**	Modnumber:	00
**	Modification:	Original
**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'vwResourceType';
DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'VIEW';
DECLARE	Proc_nm	VARCHAR(255) DEFAULT 'gfpResourceType';
###############################################################################
GFP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF Key_cd IS NULL OR Key_cd = '' THEN SET Key_cd = 'AL';	END IF;
	IF Resrc_tp IS NULL OR Resrc_tp = '' THEN SET Resrc_tp = '-2147483647';	END IF;
	IF ParentResrc_tp IS NULL OR ParentResrc_tp = '' THEN SET ParentResrc_tp = '-2147483647';	END IF;
	IF ResrcType_tx IS NULL OR ResrcType_tx = '' THEN SET ResrcType_tx = '-2147483647';	END IF;
	IF Left_id IS NULL OR Left_id = 0 THEN SET Left_id =  -2147483647;	END IF;
	IF Right_id IS NULL OR Right_id = 0 THEN SET Right_id =  -2147483647;	END IF;
	IF Level_id IS NULL OR Level_id = 0 THEN SET Level_id =  -2147483647;	END IF;
	IF Order_id IS NULL OR Order_id = 0 THEN SET Order_id =  -2147483647;	END IF;

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
			vwResourceType.Resrc_tp
		,	vwResourceType.ParentResrc_tp
		,	vwResourceType.ResrcType_tx
		,	vwResourceType.Left_id
		,	vwResourceType.Right_id
		,	vwResourceType.Level_id
		,	vwResourceType.Order_id
		FROM
			vwResourceType
		WHERE
			vwResourceType.Resrc_tp	= Resrc_tp

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
			vwResourceType.Resrc_tp
		,	vwResourceType.ParentResrc_tp
		,	vwResourceType.ResrcType_tx
		,	vwResourceType.Left_id
		,	vwResourceType.Right_id
		,	vwResourceType.Level_id
		,	vwResourceType.Order_id
		FROM
			vwResourceType
		WHERE
			vwResourceType.Resrc_tp	= Resrc_tp

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
			vwResourceType.Resrc_tp
		,	vwResourceType.ParentResrc_tp
		,	vwResourceType.ResrcType_tx
		,	vwResourceType.Left_id
		,	vwResourceType.Right_id
		,	vwResourceType.Level_id
		,	vwResourceType.Order_id
		FROM
			vwResourceType
		WHERE
			(
			vwResourceType.Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
			)
		AND	(
			vwResourceType.ParentResrc_tp	= ParentResrc_tp
		OR	ParentResrc_tp	= '-2147483647'
			)
		AND	(
			vwResourceType.ResrcType_tx	LIKE CONCAT('%', ResrcType_tx, '%')
		OR	ResrcType_tx	LIKE '-2147483647'
			)
		AND	(
			vwResourceType.Left_id	= Left_id
		OR	Left_id	=  -2147483647
			)
		AND	(
			vwResourceType.Right_id	= Right_id
		OR	Right_id	=  -2147483647
			)
		AND	(
			vwResourceType.Level_id	= Level_id
		OR	Level_id	=  -2147483647
			)
		AND	(
			vwResourceType.Order_id	= Order_id
		OR	Order_id	=  -2147483647
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

