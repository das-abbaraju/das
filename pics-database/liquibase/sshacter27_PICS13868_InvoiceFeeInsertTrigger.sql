--liquibase formatted SQL
--sql:
--changeset sshacter:27DROP splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS invoice_fee_archive_after_insert
;
--sql:
--changeset sshacter:27CREATE splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	invoice_fee_archive_after_insert
AFTER INSERT ON	invoice_fee
/*
**	Name:		invoice_fee_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert invoice_fee DML history into the logging tables
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
	SET	@ddlName	= "invoice_fee";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,CURRENT_DATE));

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
	,	'	<field column="fee"><oldValue>', IFNULL(new.fee,"") ,'</oldValue><newValue>', IFNULL(new.fee,"") ,'</newValue></field>'
	,	'	<field column="defaultAmount"><oldValue>', IFNULL(new.defaultAmount,"") ,'</oldValue><newValue>', IFNULL(new.defaultAmount,"") ,'</newValue></field>'
	,	'	<field column="ratePercent"><oldValue>', IFNULL(new.ratePercent,"") ,'</oldValue><newValue>', IFNULL(new.ratePercent,"") ,'</newValue></field>'
	,	'	<field column="visible"><oldValue>', IFNULL(new.visible,"") ,'</oldValue><newValue>', IFNULL(new.visible,"") ,'</newValue></field>'
	,	'	<field column="feeClass"><oldValue>', IFNULL(new.feeClass,"") ,'</oldValue><newValue>', IFNULL(new.feeClass,"") ,'</newValue></field>'
	,	'	<field column="minFacilities"><oldValue>', IFNULL(new.minFacilities,"") ,'</oldValue><newValue>', IFNULL(new.minFacilities,"") ,'</newValue></field>'
	,	'	<field column="maxFacilities"><oldValue>', IFNULL(new.maxFacilities,"") ,'</oldValue><newValue>', IFNULL(new.maxFacilities,"") ,'</newValue></field>'
	,	'	<field column="qbFullName"><oldValue>', IFNULL(new.qbFullName,"") ,'</oldValue><newValue>', IFNULL(new.qbFullName,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="effectiveDate"><oldValue>', IFNULL(new.effectiveDate,"") ,'</oldValue><newValue>', IFNULL(new.effectiveDate,"") ,'</newValue></field>'
	,	'	<field column="displayOrder"><oldValue>', IFNULL(new.displayOrder,"") ,'</oldValue><newValue>', IFNULL(new.displayOrder,"") ,'</newValue></field>'
	,	'	<field column="commissionEligible"><oldValue>', IFNULL(new.commissionEligible,"") ,'</oldValue><newValue>', IFNULL(new.commissionEligible,"") ,'</newValue></field>'
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
	INSERT INTO	log_archive.log_invoice_fee
	VALUES
	(
		@logDate
	,	new.id
	,	new.fee
	,	new.defaultAmount
	,	new.ratePercent
	,	new.visible
	,	new.feeClass
	,	new.minFacilities
	,	new.maxFacilities
	,	new.qbFullName
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	,	new.effectiveDate
	,	new.displayOrder
	,	new.commissionEligible
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END
;
