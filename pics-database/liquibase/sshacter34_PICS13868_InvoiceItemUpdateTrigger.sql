--liquibase formatted SQL
--sql:
--changeset sshacter:34DROP splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS invoice_item_archive_after_update
;
--sql:
--changeset sshacter:34CREATE splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	invoice_item_archive_after_update
AFTER UPDATE ON	invoice_item
/*
**	Name:		invoice_item_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert invoice_item DML history into the logging tables
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
	SET	@ddlName	= "invoice_item";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(new.revenueStartDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.revenueFinishDate,CURRENT_DATE));

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
	,	'	<field column="feeID"><oldValue>', IFNULL(old.feeID,"") ,'</oldValue><newValue>', IFNULL(new.feeID,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(old.amount,"") ,'</oldValue><newValue>', IFNULL(new.amount,"") ,'</newValue></field>'
	,	'	<field column="originalAmount"><oldValue>', IFNULL(old.originalAmount,"") ,'</oldValue><newValue>', IFNULL(new.originalAmount,"") ,'</newValue></field>'
	,	'	<field column="description"><oldValue>', IFNULL(old.description,"") ,'</oldValue><newValue>', IFNULL(new.description,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="paymentExpires"><oldValue>', IFNULL(old.paymentExpires,"") ,'</oldValue><newValue>', IFNULL(new.paymentExpires,"") ,'</newValue></field>'
	,	'	<field column="qbRefundID"><oldValue>', IFNULL(old.qbRefundID,"") ,'</oldValue><newValue>', IFNULL(new.qbRefundID,"") ,'</newValue></field>'
	,	'	<field column="refunded"><oldValue>', IFNULL(old.refunded,"") ,'</oldValue><newValue>', IFNULL(new.refunded,"") ,'</newValue></field>'
	,	'	<field column="refundFor"><oldValue>', IFNULL(old.refundFor,"") ,'</oldValue><newValue>', IFNULL(new.refundFor,"") ,'</newValue></field>'
	,	'	<field column="transactionType"><oldValue>', IFNULL(old.transactionType,"") ,'</oldValue><newValue>', IFNULL(new.transactionType,"") ,'</newValue></field>'
	,	'	<field column="revenueStartDate"><oldValue>', IFNULL(old.revenueStartDate,"") ,'</oldValue><newValue>', IFNULL(new.revenueStartDate,"") ,'</newValue></field>'
	,	'	<field column="revenueFinishDate"><oldValue>', IFNULL(old.revenueFinishDate,"") ,'</oldValue><newValue>', IFNULL(new.revenueFinishDate,"") ,'</newValue></field>'
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
	INSERT INTO	log_archive.log_invoice_item
	VALUES
	(
		@logDate
	,	new.id
	,	new.invoiceID
	,	new.feeID
	,	new.amount
	,	new.originalAmount
	,	new.description
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	,	new.paymentExpires
	,	new.qbRefundID
	,	new.refunded
	,	new.refundFor
	,	new.transactionType
	,	new.revenueStartDate
	,	new.revenueFinishDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END
;