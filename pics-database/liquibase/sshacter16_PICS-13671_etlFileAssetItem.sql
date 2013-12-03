--liquibase formatted sql

--sql:
--changeset sshacter:16a splitStatements:true endDelimiter:|
DROP PROCEDURE IF EXISTS etlFileAssetItem;
--sql:
--changeset sshacter:16b splitStatements:false endDelimiter:|
CREATE DEFINER=`pics_admin`@`%` PROCEDURE etlFileAssetItem
(
	_fileAssetID	INT(11)
,	_etlAction	VARCHAR(16)
)
BEGIN
/*
**	Name:		etlFileAssetItem
**	Type:		DB API procedure: Insert
**	Purpose:
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	2013-NOV-18
**	Modnumber:	00
**	Modification:	Original

CALL etlFileAssetItem
(
	@fileAssetID	:= 187
,	@etlAction	:= "STAGE"
)

**
*/
###############################################################################
DECLARE	SYSTABLE	VARCHAR(255)	DEFAULT 'file_asset';
DECLARE	SYSRIGHT	VARCHAR(40)	DEFAULT 'ETL-INSERT';
DECLARE	Proc_nm		VARCHAR(255)	DEFAULT 'etlFileAssetItem';
DECLARE	Key_cd		VARCHAR(16)	DEFAULT 'PK';
DECLARE RowExists_fg	BOOLEAN 	DEFAULT FALSE;
DECLARE ProcFailed_fg	BOOLEAN 	DEFAULT FALSE;

DECLARE ERRTABLE	VARCHAR(64) 	DEFAULT SYSTABLE;
DECLARE ERR99000	CONDITION FOR SQLSTATE '99000';
DECLARE MSG99000	VARCHAR(1024) DEFAULT
	CONCAT
	(
		"Error: Invalid action sent. Action must be STAGE or LOAD."
	)
;
DECLARE ERR99001	CONDITION FOR SQLSTATE '99001';
DECLARE MSG99001	VARCHAR(1024) DEFAULT
	CONCAT
	(
		"Error: No matching row in "
	,	ERRTABLE
	,	" for fileAssetID "
	,	_fileAssetID
	,	" . Nothing to process!"
	)
;
###############################################################################
ETL:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	IF _etlAction IS NULL OR _etlAction = "" THEN SET _etlAction = "STAGE";	END IF;
	#######################################################################
	--	-------------------------------------------------------------------------
	--	Verify correct action to take
	--	-------------------------------------------------------------------------
	IF
		_etlAction	= "STAGE"
	OR 	_etlAction	= "LOAD"
	THEN
		SET 	ProcFailed_fg	= FALSE;
	ELSE
		SET ERRTABLE	= "file_asset_item";
		SHOW ERRORS;
		SIGNAL ERR99000
		SET MESSAGE_TEXT	= MSG99000
		,	MYSQL_ERRNO	= 9000
		,	TABLE_NAME	= ERRTABLE
		;
		LEAVE 	ETL;
	END IF
	;
	--	-------------------------------------------------------------------------
	--	Verify valid file asset ID
	--	-------------------------------------------------------------------------
	IF
	EXISTS
	(
		SELECT 	1
		FROM
			file_asset
		WHERE	1=1
		AND	file_asset.id	= _fileAssetID
	)
	THEN
		SET RowExists_fg	= TRUE;
	ELSE
		SET ERRTABLE	= SYSTABLE;
		SHOW ERRORS;
		SIGNAL ERR99001
		SET MESSAGE_TEXT	= MSG99001
		,	MYSQL_ERRNO	= 9001
		,	TABLE_NAME	= ERRTABLE
		;
		LEAVE ETL;
	END IF
	;
	--	Dependent child entity FK check.
	IF
	EXISTS
	(
		SELECT 	1
		FROM
			file_asset_item
		WHERE	1=1
		AND	file_asset_item.fileAssetID	= _fileAssetID
	)
	THEN
		SET RowExists_fg	= TRUE;
	ELSE
		SET ERRTABLE	= "file_asset_item";
		SHOW ERRORS;
		SIGNAL ERR99001
		SET MESSAGE_TEXT	= MSG99001
		,	MYSQL_ERRNO	= 9001
		,	TABLE_NAME	= ERRTABLE
		;
		LEAVE ETL;
	END IF
	;
	--	-------------------------------------------------------------------------
	--	Determine which rows to insert, update, ignore, or reject
	--	-------------------------------------------------------------------------
	--	Store and use constants
	SET 	@etlStatus	= "IMPORTED";
	SET 	@processDate	= NOW();
	SET 	@enLocale	= "en";
	SET 	@insAction	= "INSERT";
	SET 	@updAction	= "UPDATE";
	SET 	@ignAction	= "IGNORE";
	SET 	@rejAction	= "REJECT";
	--	-------------------------------------------------------------------------
	--	Begin by initially setting the action on all rows to INSERT
	UPDATE
		file_asset_item
	SET
		file_asset_item.etlAction	= @insAction
	,	file_asset_item.etlActionResult	= "This row has no matching key and locale in the database for this msgValue. This row will be inserted into the database."
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	;
	--	-------------------------------------------------------------------------
	--	Find any duplicate rows that appear in the file
	--	then set the action to IGNORE for any duplicate rows found.
	--	-------------------------------------------------------------------------
	DROP TEMPORARY TABLE IF EXISTS fileDups;
	CREATE TEMPORARY TABLE IF NOT EXISTS	fileDups
	AS
	SELECT
		file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	COUNT(1) 	dupRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	GROUP BY
		file_asset_item.msgKey
	,	file_asset_item.locale
	HAVING 	dupRows 	> 1
	ORDER BY
		dupRows 	DESC
	;
	--	-------------------------------------------------------------------------
	--	Find any rows that appear in the file that would cause a duplicate row in the tables
	--	then set the action to IGNORE for any duplicate rows found.
	--	-------------------------------------------------------------------------
	DROP TEMPORARY TABLE IF EXISTS tableDups;
	CREATE TEMPORARY TABLE IF NOT EXISTS	tableDups
	SELECT
		file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	COUNT(1) 	dupRows
	FROM
		file_asset_item
	,	vwmsg_key_locale
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	vwmsg_key_locale.msgKey	= file_asset_item.msgKey
	AND	vwmsg_key_locale.locale	= file_asset_item.locale
	AND	vwmsg_key_locale.msgValue	= file_asset_item.msgValue
	GROUP BY
		file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	HAVING 	dupRows 	>= 1
	ORDER BY
		dupRows 	DESC
	;
	--	-------------------------------------------------------------------------
	UPDATE
		file_asset_item
	,	fileDups
	SET
		file_asset_item.etlAction	= @ignAction
	,	file_asset_item.etlActionResult	= "This row is an exact duplicate of another row in this file. This row will be ignored."
	WHERE 	1=1
	AND	file_asset_item.msgKey	= fileDups.msgKey
	AND	file_asset_item.locale	= fileDups.locale
	AND	file_asset_item.msgValue	= fileDups.msgValue
	;
	UPDATE
		file_asset_item
	,	tableDups
	SET
		file_asset_item.etlAction	= @ignAction
	,	file_asset_item.etlActionResult	= "This row is an exact duplicate of another row in the database table. This row will be ignored."
	WHERE 	1=1
	AND	file_asset_item.msgKey	= tableDups.msgKey
	AND	file_asset_item.locale	= tableDups.locale
	AND	file_asset_item.msgValue	= tableDups.msgValue
	;
	--	-------------------------------------------------------------------------
	--	Row-level error checking. Reject rows that:
	--	Have a NULL or empty ("") msgKey, locale or msgValue
	--	Have a msgKey and a dialect locale with no matching key and locale of "en" (English)
	--	Set the action to REJECT for any error rows found.
	--	-------------------------------------------------------------------------
	DROP TEMPORARY TABLE IF EXISTS	errRows;
	CREATE TEMPORARY TABLE IF NOT EXISTS	errRows
	SELECT
		file_asset_item.id
	,	file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	"This row has a missing or empty value for msgKey, locale or msgValue. This row will be rejected."	etlActionResult
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	NULLIF(file_asset_item.msgKey, "")	IS NULL
	OR	NULLIF(file_asset_item.locale, "")	IS NULL
	OR	NULLIF(file_asset_item.msgValue, "")	IS NULL
	;
	--	Row has msgKey and a dialect locale with no matching key and locale of "en" (English)
	INSERT INTO	errRows
	SELECT
		file_asset_item.id
	,	file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	"This row has no matching key with locale of 'en' (English) in the database for the msgKey and dialect locale provided. This row will be rejected."
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.locale	<> @enLocale
	AND	NULLIF(file_asset_item.msgKey, "")	IS NOT NULL
	AND	NULLIF(file_asset_item.locale, "")	IS NOT NULL
	AND	NULLIF(file_asset_item.msgValue, "")	IS NOT NULL
	AND NOT EXISTS
	(
		SELECT	1
		FROM
			vwmsg_key_locale
		WHERE	1
		AND	vwmsg_key_locale.msgKey = file_asset_item.msgKey
		AND	vwmsg_key_locale.locale	= @enLocale
	)
	;
	--	-------------------------------------------------------------------------
	UPDATE
		file_asset_item
	,	errRows
	SET
		file_asset_item.etlAction	= @rejAction
	,	file_asset_item.etlActionResult	= errRows.etlActionResult
	WHERE 	1=1
	AND	file_asset_item.id	= errRows.id
	;
	--	-------------------------------------------------------------------------
	--	Find any remaining rows in the file that are updates to msgValue in the
	--	msg_locale table (based on msgKey and locale)
	--	then set the action to UPDATE for any msgValue update rows found.
	--	-------------------------------------------------------------------------
	DROP TEMPORARY TABLE IF EXISTS	updRows;
	CREATE TEMPORARY TABLE IF NOT EXISTS	updRows
	SELECT
		file_asset_item.id
	,	file_asset_item.msgKey
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	FROM
		file_asset_item
	JOIN
		vwmsg_key_locale
	ON	vwmsg_key_locale.msgKey = file_asset_item.msgKey
	AND	vwmsg_key_locale.locale	= file_asset_item.locale
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @insAction
	AND	NULLIF(file_asset_item.msgKey, "")	IS NOT NULL
	AND	NULLIF(file_asset_item.locale, "")	IS NOT NULL
	AND	NULLIF(file_asset_item.msgValue, "")	IS NOT NULL
	;
	--	-------------------------------------------------------------------------
	UPDATE
		file_asset_item
	,	updRows
	SET
		file_asset_item.etlAction	= @updAction
	,	file_asset_item.etlActionResult	= "This row has a matching key and locale in the database for this msgValue. This row will update the database."
	WHERE 	1=1
	AND	file_asset_item.id	= updRows.id
	;
	--	-------------------------------------------------------------------------
	--	Store all the row counts from the pre-processing (STAGE).
	--	-------------------------------------------------------------------------
	SELECT
		COUNT(1)
	INTO
		@allRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	;

	SELECT
		COUNT(1)
	INTO
		@insRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @insAction
	;

	SELECT
		COUNT(1)
	INTO
		@updRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @updAction
	;

	SELECT
		COUNT(1)
	INTO
		@errRows
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @rejAction
	;

	SET	@delRows	= 0;
	SET 	@fileDups	= 0;
	SET 	@tableDups	= 0;
	SELECT
		COUNT(1)
	INTO
		@fileDups
	FROM
		fileDups
	;
	SELECT
		COUNT(1)
	INTO
		@tableDups
	FROM
		tableDups
	;
	--	-------------------------------------------------------------------------
	--	Update the import table with the row counts from staging the file_item rows
	--	-------------------------------------------------------------------------
	UPDATE
		file_asset
	SET
		file_asset.etlStatus	= @etlStatus
	,	file_asset.processDate	= @processDate
	,	file_asset.allRows	= @allRows
	,	file_asset.insRows	= @insRows
	,	file_asset.updRows	= @updRows
	,	file_asset.delRows	= @delRows
	,	file_asset.errRows	= @errRows
	,	file_asset.dupRows	= @fileDups + @tableDups
	WHERE	1=1
	AND	file_asset.id	= _fileAssetId
	;
	--	-------------------------------------------------------------------------
	--	Return file staging counts to calling program
	--	then exit if action specified was STAGE only.
	--	Add new keys and values (INSERT) if action specified was LOAD.
	--	-------------------------------------------------------------------------
	SELECT
		file_asset.*
	FROM
		file_asset
	WHERE	1=1
	AND	file_asset.id	= _fileAssetID
	;
	--	-------------------------------------------------------------------------
	IF
		_etlAction	= "STAGE"
	THEN
		LEAVE 	ETL;
	END IF
	;
	--	-------------------------------------------------------------------------
	--	Process the INSERTS
	--	-------------------------------------------------------------------------
	INSERT INTO	msg_key
	(
		msgKey
	,	js
	,	creationDate
	)
	SELECT
		msgKey
	,	js
	,	creationDate
	FROM
		file_asset_item
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @insAction
	;

	INSERT INTO	msg_locale
	(
		keyID
	,	locale
	,	msgValue
	,	qualityRating
	,	creationDate
	)
	SELECT
		msg_key.id
	,	file_asset_item.locale
	,	file_asset_item.msgValue
	,	2	-- 2=Good, 1=Questionable, 0=Bad
	,	NOW()
	FROM
		file_asset_item
	JOIN
		msg_key
	ON	msg_key.msgKey	= file_asset_item.msgKey
	WHERE	1=1
	AND	file_asset_item.fileAssetID	= _fileAssetID
	AND	file_asset_item.etlAction	= @insAction
	;
	--	-------------------------------------------------------------------------

END ETL;
###############################################################################
END
;
