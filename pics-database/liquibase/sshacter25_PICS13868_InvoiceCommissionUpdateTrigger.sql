--liquibase formatted SQL
--sql:
--changeset sshacter:25DROP splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS invoice_commission_archive_after_update
;
--sql:
--changeset sshacter:25CREATE splitStatements:false endDelimiter:|
 CREATE DEFINER = 'pics_admin'@'%' TRIGGER	invoice_commission_archive_after_update
AFTER UPDATE ON	invoice_commission
/*
**	Name:		invoice_commission_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert invoice_commission DML history into the logging tables
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
	SET	@dmlType	= "UPDATE";
	SET	@ddlName	= "invoice_commission";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,CURRENT_DATE));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT IFNULL(IFNULL(NAME,"Unknown"),new.updatedBy) FROM users WHERE id = new.updatedBy);
	END IF
	;
	SET @username := IFNULL(@username, CURRENT_USER);

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', FROM_UNIXTIME(@logDate),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(old.invoiceID,"") ,'</oldValue><newValue>', IFNULL(new.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="userID"><oldValue>', IFNULL(old.userID,"") ,'</oldValue><newValue>', IFNULL(new.userID,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="activationPoints"><oldValue>', IFNULL(old.activationPoints,"") ,'</oldValue><newValue>', IFNULL(new.activationPoints,"") ,'</newValue></field>'
	,	'	<field column="revenue"><oldValue>', IFNULL(old.revenue,"") ,'</oldValue><newValue>', IFNULL(new.revenue,"") ,'</newValue></field>'
	,	'	<field column="accountUserID"><oldValue>', IFNULL(old.accountUserID,"") ,'</oldValue><newValue>', IFNULL(new.accountUserID,"") ,'</newValue></field>'
	,	'</diff>\n'
	,'</logdata>'
	)
	;
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_event
	(
		logDate
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
		@logDate
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
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_invoice_commission
	VALUES
	(
		@logDate
	,	new.id
	,	new.invoiceID
	,	new.userID
	,	new.createdBy
	,	new.creationDate
	,	new.updatedBy
	,	new.updateDate
	,	new.activationPoints
	,	new.revenue
	,	new.accountUserID
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END
;
