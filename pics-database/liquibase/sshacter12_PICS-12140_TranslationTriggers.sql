--liquibase formatted sql

--sql:
--changeset sshacter:12a splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS	msg_key_archive_after_insert;
--sql:
--changeset sshacter:12b splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	msg_key_archive_after_insert
AFTER INSERT ON		msg_key
/*
**	Name:		msg_key_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert msg_key change history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-18
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "INSERT";
	SET	@ddlName	= "msg_key";
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));
	SET	@username	= CURRENT_USER();

	IF
		LOCATE("pics", @username)	> 0
	THEN
		SET	@username	= IFNULL(new.createdBy,CURRENT_USER());
	ELSE
		SET	@username	= IFNULL(new.createdBy,"Not found or NULL");
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(new.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="msgKey"><oldValue>', IFNULL(new.msgKey,"") ,'</oldValue><newValue>', IFNULL(new.msgKey,"") ,'</newValue></field>'
	,	'	<field column="description"><oldValue>', IFNULL(new.description,"") ,'</oldValue><newValue>', IFNULL(new.description,"") ,'</newValue></field>'
	,	'	<field column="js"><oldValue>', IFNULL(new.js,"") ,'</oldValue><newValue>', IFNULL(new.js,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(new.firstUsed,"") ,'</oldValue><newValue>', IFNULL(new.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(new.lastUsed,"") ,'</oldValue><newValue>', IFNULL(new.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'

	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_event
	(
		logStart
	,	logFinish
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		TIMESTAMP(NOW())
	,	TIMESTAMP(NOW())
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	new.id
	,	new.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	SET	@logEventID	= LAST_INSERT_ID();
	--	-------------------------------------------------------------------------
	INSERT INTO	log_msg_key
	VALUES
	(
		@logEventID
	,	new.id
	,	new.msgKey
	,	new.description
	,	new.js
	,	new.firstUsed
	,	new.lastUsed
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;

--sql:
--changeset sshacter:12c splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS	msg_key_archive_after_update;
--sql:
--changeset sshacter:12d splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	msg_key_archive_after_update
AFTER UPDATE ON		msg_key
/*
**	Name:		msg_key_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert msg_key change history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-18
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "UPDATE";
	SET	@ddlName	= "msg_key";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));
	SET	@username	= CURRENT_USER();

	IF
		LOCATE("pics", @username)	> 0
	THEN
		SET	@username	= IFNULL(new.updatedBy,CURRENT_USER());
	ELSE
		SET	@username	= IFNULL(new.updatedBy,"Not found or NULL");
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="msgKey"><oldValue>', IFNULL(old.msgKey,"") ,'</oldValue><newValue>', IFNULL(new.msgKey,"") ,'</newValue></field>'
	,	'	<field column="description"><oldValue>', IFNULL(old.description,"") ,'</oldValue><newValue>', IFNULL(new.description,"") ,'</newValue></field>'
	,	'	<field column="js"><oldValue>', IFNULL(old.js,"") ,'</oldValue><newValue>', IFNULL(new.js,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(old.firstUsed,"") ,'</oldValue><newValue>', IFNULL(new.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(old.lastUsed,"") ,'</oldValue><newValue>', IFNULL(new.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'

	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_event
	(
		logStart
	,	logFinish
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		TIMESTAMP(NOW())
	,	TIMESTAMP(NOW())
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	new.id
	,	new.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	SET	@logEventID	= LAST_INSERT_ID();
	--	-------------------------------------------------------------------------
	INSERT INTO	log_msg_key
	VALUES
	(
		@logEventID
	,	new.id
	,	new.msgKey
	,	new.description
	,	new.js
	,	new.firstUsed
	,	new.lastUsed
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;

--sql:
--changeset sshacter:12e splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS	msg_key_archive_before_delete;
--sql:
--changeset sshacter:12f splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	msg_key_archive_before_delete
BEFORE DELETE ON	msg_key
/*
**	Name:		msg_key_archive_before_delete
**	Type:		before delete trigger
**	Purpose:	To insert msg_key change history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-01
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "DELETE";
	SET	@ddlName	= "msg_key";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.updateDate,"9999-12-31"));
	SET	@username	= CURRENT_USER();

	IF
		LOCATE("pics", @username)	> 0
	THEN
		SET	@username	= IFNULL(old.updatedBy,CURRENT_USER());
	ELSE
		SET	@username	= IFNULL(old.updatedBy,"Not found or NULL");
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="msgKey"><oldValue>', IFNULL(old.msgKey,"") ,'</oldValue><newValue>', IFNULL(old.msgKey,"") ,'</newValue></field>'
	,	'	<field column="description"><oldValue>', IFNULL(old.description,"") ,'</oldValue><newValue>', IFNULL(old.description,"") ,'</newValue></field>'
	,	'	<field column="js"><oldValue>', IFNULL(old.js,"") ,'</oldValue><newValue>', IFNULL(old.js,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(old.firstUsed,"") ,'</oldValue><newValue>', IFNULL(old.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(old.lastUsed,"") ,'</oldValue><newValue>', IFNULL(old.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'

	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_event
	(
		logStart
	,	logFinish
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		TIMESTAMP(NOW())
	,	TIMESTAMP(NOW())
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	old.id
	,	old.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	SET	@logEventID	= LAST_INSERT_ID();
	--	-------------------------------------------------------------------------
	INSERT INTO	log_msg_key
	VALUES
	(
		@logEventID
	,	old.id
	,	old.msgKey
	,	old.description
	,	old.js
	,	old.firstUsed
	,	old.lastUsed
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END;

--sql:
--changeset sshacter:12g splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS	msg_locale_archive_after_insert;
--sql:
--changeset sshacter:12h splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	msg_locale_archive_after_insert
AFTER INSERT ON		msg_locale
/*
**	Name:		msg_locale_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert msg_locale change history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-18
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "INSERT";
	SET	@ddlName	= "msg_locale";
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));
	SET	@username	= CURRENT_USER();

	IF
		LOCATE("pics", @username)	> 0
	THEN
		SET	@username	= IFNULL(new.createdBy,CURRENT_USER());
	ELSE
		SET	@username	= IFNULL(new.createdBy,"Not found or NULL");
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(new.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="keyID"><oldValue>', IFNULL(new.keyID,"") ,'</oldValue><newValue>', IFNULL(new.keyID,"") ,'</newValue></field>'
	,	'	<field column="locale"><oldValue>', IFNULL(new.locale,"") ,'</oldValue><newValue>', IFNULL(new.locale,"") ,'</newValue></field>'
	,	'	<field column="msgValue"><oldValue>', IFNULL(new.msgValue,"") ,'</oldValue><newValue>', IFNULL(new.msgValue,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(new.firstUsed,"") ,'</oldValue><newValue>', IFNULL(new.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(new.lastUsed,"") ,'</oldValue><newValue>', IFNULL(new.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="qualityRating"><oldValue>', IFNULL(new.qualityRating,"") ,'</oldValue><newValue>', IFNULL(new.qualityRating,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'

	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_event
	(
		logStart
	,	logFinish
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		TIMESTAMP(NOW())
	,	TIMESTAMP(NOW())
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	new.id
	,	new.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	SET	@logEventID	= LAST_INSERT_ID();
	--	-------------------------------------------------------------------------
	INSERT INTO	log_msg_locale
	VALUES
	(
		@logEventID
	,	new.id
	,	new.keyID
	,	new.locale
	,	new.msgValue
	,	new.firstUsed
	,	new.lastUsed
	,	new.qualityRating
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END;

--sql:
--changeset sshacter:12i splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS	msg_locale_archive_after_update;
--sql:
--changeset sshacter:12j splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	msg_locale_archive_after_update
AFTER UPDATE ON		msg_locale
/*
**	Name:		msg_locale_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert msg_locale change history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-18
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "UPDATE";
	SET	@ddlName	= "msg_locale";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));
	SET	@username	= CURRENT_USER();

	IF
		LOCATE("pics", @username)	> 0
	THEN
		SET	@username	= IFNULL(new.updatedBy,CURRENT_USER());
	ELSE
		SET	@username	= IFNULL(new.updatedBy,"Not found or NULL");
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="keyID"><oldValue>', IFNULL(old.keyID,"") ,'</oldValue><newValue>', IFNULL(new.keyID,"") ,'</newValue></field>'
	,	'	<field column="locale"><oldValue>', IFNULL(old.locale,"") ,'</oldValue><newValue>', IFNULL(new.locale,"") ,'</newValue></field>'
	,	'	<field column="msgValue"><oldValue>', IFNULL(old.msgValue,"") ,'</oldValue><newValue>', IFNULL(new.msgValue,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(old.firstUsed,"") ,'</oldValue><newValue>', IFNULL(new.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(old.lastUsed,"") ,'</oldValue><newValue>', IFNULL(new.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="qualityRating"><oldValue>', IFNULL(old.qualityRating,"") ,'</oldValue><newValue>', IFNULL(new.qualityRating,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'

	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_event
	(
		logStart
	,	logFinish
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		TIMESTAMP(NOW())
	,	TIMESTAMP(NOW())
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	new.id
	,	new.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	SET	@logEventID	= LAST_INSERT_ID();
	--	-------------------------------------------------------------------------
	INSERT INTO	log_msg_locale
	VALUES
	(
		@logEventID
	,	new.id
	,	new.keyID
	,	new.locale
	,	new.msgValue
	,	new.firstUsed
	,	new.lastUsed
	,	new.qualityRating
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END;

--sql:
--changeset sshacter:12k splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS	msg_locale_archive_before_delete;
--sql:
--changeset sshacter:12l splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	msg_locale_archive_before_delete
BEFORE DELETE ON	msg_locale
/*
**	Name:		msg_locale_archive_before_delete
**	Type:		before delete trigger
**	Purpose:	To insert msg_locale change history into the logging tables
**	Author:		Solomon S. Shacter
**
**	Modified:	2013-NOV-01
**	Modnumber:	00
**	Modification:	Original
**
*/
FOR EACH ROW
BEGIN
	--	-------------------------------------------------------------------------
	SET	@dmlType	= "DELETE";
	SET	@ddlName	= "msg_locale";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.updateDate,"9999-12-31"));
	SET	@username	= CURRENT_USER();

	IF
		LOCATE("pics", @username)	> 0
	THEN
		SET	@username	= IFNULL(old.updatedBy,CURRENT_USER());
	ELSE
		SET	@username	= IFNULL(old.updatedBy,"Not found or NULL");
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="keyID"><oldValue>', IFNULL(old.keyID,"") ,'</oldValue><newValue>', IFNULL(old.keyID,"") ,'</newValue></field>'
	,	'	<field column="locale"><oldValue>', IFNULL(old.locale,"") ,'</oldValue><newValue>', IFNULL(old.locale,"") ,'</newValue></field>'
	,	'	<field column="msgValue"><oldValue>', IFNULL(old.msgValue,"") ,'</oldValue><newValue>', IFNULL(old.msgValue,"") ,'</newValue></field>'
	,	'	<field column="firstUsed"><oldValue>', IFNULL(old.firstUsed,"") ,'</oldValue><newValue>', IFNULL(old.firstUsed,"") ,'</newValue></field>'
	,	'	<field column="lastUsed"><oldValue>', IFNULL(old.lastUsed,"") ,'</oldValue><newValue>', IFNULL(old.lastUsed,"") ,'</newValue></field>'
	,	'	<field column="qualityRating"><oldValue>', IFNULL(old.qualityRating,"") ,'</oldValue><newValue>', IFNULL(old.qualityRating,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'

	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_event
	(
		logStart
	,	logFinish
	,	validStart
	,	validFinish
	,	dmlType
	,	ddlName
	,	ddlKey
	,	logSeq
	,	userName
	,	logYear
	,	logMonth
	,	logWeek
	,	logDay
	,	logQtr
	,	logEntry
	)
	VALUES
	(
		TIMESTAMP(NOW())
	,	TIMESTAMP(NOW())
	,	@validStart
	,	@validFinish
	,	@dmlType
	,	@ddlName
	,	old.id
	,	old.id
	,	@username
	,	YEAR(CURRENT_DATE)
	,	MONTH(CURRENT_DATE)
	,	WEEK(CURRENT_DATE)
	,	DAY(CURRENT_DATE)
	,	QUARTER(CURRENT_DATE)
	,	@logEntry
	)
	;
	--	-------------------------------------------------------------------------
	SET	@logEventID	= LAST_INSERT_ID();
	--	-------------------------------------------------------------------------
	INSERT INTO	log_msg_locale
	VALUES
	(
		@logEventID
	,	old.id
	,	old.keyID
	,	old.locale
	,	old.msgValue
	,	old.firstUsed
	,	old.lastUsed
	,	old.qualityRating
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END;

