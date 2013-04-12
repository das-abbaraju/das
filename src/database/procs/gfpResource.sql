DROP PROCEDURE IF EXISTS	`gfpResource`
;

DELIMITER //
CREATE PROCEDURE	gfpResource
(
	Resrc_id		int signed		-- PK1 
,	Resrc_tp		varchar(80)		-- PK2 AK1
,	Resrc_nm		varchar(128)		--  AK2
,	Resrc_tx		mediumtext	
,	ADD_dm		datetime	
,	ADD_nm		varchar(128)	
,	UPD_dm		datetime	
,	UPD_nm		varchar(128)	
,	DEL_dm		datetime	
,	DEL_nm		varchar(128)	
,	ParentResrc_tp		varchar(80)	
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
**	Modified:	4/12/2013
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
			Resrc_id	= Resrc_id
		OR	Resrc_id	=  -2147483647
			)
		AND	(
			Resrc_tp	= Resrc_tp
		OR	Resrc_tp	= '-2147483647'
			)
		AND	(
			Resrc_nm	LIKE CONCAT('%', Resrc_nm, '%')
		OR	Resrc_nm	= '-2147483647'
			)
		AND	(
			Resrc_tx	LIKE CONCAT('%', Resrc_tx, '%')
		OR	Resrc_tx	LIKE '-2147483647'
			)
		AND	(
			ADD_dm	= ADD_dm
		OR	ADD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			ADD_nm	LIKE CONCAT('%', ADD_nm, '%')
		OR	ADD_nm	= '-2147483647'
			)
		AND	(
			UPD_dm	= UPD_dm
		OR	UPD_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			UPD_nm	LIKE CONCAT('%', UPD_nm, '%')
		OR	UPD_nm	= '-2147483647'
			)
		AND	(
			DEL_dm	= DEL_dm
		OR	DEL_dm	= '0000-00-00 00:00:00'
			)
		AND	(
			DEL_nm	LIKE CONCAT('%', DEL_nm, '%')
		OR	DEL_nm	= '-2147483647'
			)
		AND	(
			ParentResrc_tp	= ParentResrc_tp
		OR	ParentResrc_tp	= '-2147483647'
			)
		AND	(
			ResrcType_tx	LIKE CONCAT('%', ResrcType_tx, '%')
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

