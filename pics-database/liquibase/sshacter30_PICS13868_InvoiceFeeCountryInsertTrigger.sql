--liquibase formatted SQL
--sql:
--changeset sshacter:30DROP splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS invoice_fee_country_archive_after_insert
;
--sql:
--changeset sshacter:30CREATE splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	invoice_fee_country_archive_after_insert
AFTER INSERT ON	invoice_fee_country
/*
**	Name:		invoice_fee_country_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert invoice_fee_country DML history into the logging tables
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
	SET	@dmlType	= "INSERT";
	SET	@ddlName	= "invoice_fee_country";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(new.effectiveDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.expirationDate,CURRENT_DATE));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT IFNULL(IFNULL(NAME,"Unknown"),new.createdBy) FROM users WHERE id = new.createdBy);
	END IF
	;
	SET @username := IFNULL(@username, CURRENT_USER);

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', FROM_UNIXTIME(@logDate),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(new.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="feeID"><oldValue>', IFNULL(new.feeID,"") ,'</oldValue><newValue>', IFNULL(new.feeID,"") ,'</newValue></field>'
	,	'	<field column="country"><oldValue>', IFNULL(new.country,"") ,'</oldValue><newValue>', IFNULL(new.country,"") ,'</newValue></field>'
	,	'	<field column="subdivision"><oldValue>', IFNULL(new.subdivision,"") ,'</oldValue><newValue>', IFNULL(new.subdivision,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(new.amount,"") ,'</oldValue><newValue>', IFNULL(new.amount,"") ,'</newValue></field>'
	,	'	<field column="ratePercent"><oldValue>', IFNULL(new.ratePercent,"") ,'</oldValue><newValue>', IFNULL(new.ratePercent,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="effectiveDate"><oldValue>', IFNULL(new.effectiveDate,"") ,'</oldValue><newValue>', IFNULL(new.effectiveDate,"") ,'</newValue></field>'
	,	'	<field column="expirationDate"><oldValue>', IFNULL(new.expirationDate,"") ,'</oldValue><newValue>', IFNULL(new.expirationDate,"") ,'</newValue></field>'
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
	INSERT INTO	log_archive.log_invoice_fee_country
	VALUES
	(
		@logDate
	,	new.id
	,	new.feeID
	,	new.country
	,	new.subdivision
	,	new.amount
	,	new.ratePercent
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	,	new.effectiveDate
	,	new.expirationDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END
;
