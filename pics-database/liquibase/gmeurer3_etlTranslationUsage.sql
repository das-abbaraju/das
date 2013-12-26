DROP PROCEDURE IF EXISTS	etlTranslationUsage
;

DELIMITER //
CREATE DEFINER=`pics_admin`@`%` PROCEDURE	etlTranslationUsage
  (
     IN 	_msgKey             VARCHAR(100)
    ,IN	_msgLocale          VARCHAR(8)
    ,IN	_pageName           VARCHAR(100)
    ,IN	_pageOrder           VARCHAR(100)
    ,IN	_environment        VARCHAR(20)
  )
  BEGIN
/*
**	Name:		etlTranslationUsage
**	Type:		DB API procedure: Insert or Update
**	Purpose:	To insert or update Translation Usage information into translate_usage
**	Author:		Solomon S. Shacter (modified by Galen Meurer)
**	Company:	Innovella, Inc.
**
**	Modified:	12/122013
**	Modnumber:	00
**	Modification:	Original

IF a row in translation_usage exists
for a given (`msgKey`, `msgLocale`, `pageName`,`environment`) (same as the unique key)
where the lastUsed date is < now (this is a daily aggregation),
then update lastUsed date to now AND set synchronizedBatch to null AND set synchronizedDate to null

IF the row does not exist,
INSERT msgKey, msgLocale, pageName, environment to passed values
AND firstUsed = now and lastUsed = now

CALL etlTranslationUsage
(
	@msgKey		:= "Header.Welcome"
,	@msgLocale	:= "en"
,	@pageName	:= "ClearCache"
,	@pageOrder	:= "001.123"
,	@environment	:= "alpha"
)

**
*/
###############################################################################
    DECLARE	SYSTABLE	VARCHAR(255) DEFAULT 'translation_usage';
    DECLARE	SYSRIGHT	VARCHAR(40) DEFAULT 'ETL-UPSERT';
    DECLARE	Proc_nm		VARCHAR(255) DEFAULT 'etlTranslationUsage';
    DECLARE	Key_cd		VARCHAR(16) DEFAULT 'PK';
    DECLARE RowExists_fg	TINYINT	DEFAULT 0;
    DECLARE ProcFailed_fg	BOOLEAN DEFAULT FALSE;

    DECLARE	p_id	INT(11) DEFAULT 0;
    DECLARE p_lastUsed	DATE DEFAULT "0000-00-00";
###############################################################################
      ETL:
    BEGIN
#######################################################################
-- Initialize
#######################################################################
#######################################################################
-- Return if Alternate Key VIEW record exists
#######################################################################
      SELECT
        translation_usage.id
        ,	translation_usage.lastUsed
      INTO
        p_id
        ,	p_lastUsed
      FROM
        translation_usage
      WHERE	1=1
             AND	translation_usage.msgKey	= _msgKey
             AND	translation_usage.msgLocale	= _msgLocale
             AND	translation_usage.pageName	= _pageName
             AND	translation_usage.environment	= _environment
      ;

      IF
      FOUND_ROWS()	> 0
      THEN
        IF
        p_lastUsed	< DATE(NOW())
        THEN
          UPDATE
            translation_usage
          SET
            lastUsed 	= DATE(NOW())
            ,	synchronizedBatch 	= NULL
            ,	synchronizedDate 	= NULL
          WHERE	1=1
                 AND	id 	= p_id
          ;
        END IF;
      ELSE
        INSERT INTO 	translation_usage
        (
          msgKey
          ,	msgLocale
          ,	pageName
          ,	pageOrder
          ,	environment
          ,	firstUsed
          ,	lastUsed
        )
        VALUES
          (
            _msgKey
            ,	_msgLocale
            ,	_pageName
            ,	_pageOrder
            ,	_environment
            ,	DATE(NOW())
            ,	DATE(NOW())
          )
        ;
      END IF;
#######################################################################
    END ETL;
###############################################################################
  END
//
DELIMITER ;
;
