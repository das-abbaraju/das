--liquibase formatted SQL
--sql:
--changeset sshacter:35DROP splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS invoice_item_archive_before_delete
;
--sql:
--changeset sshacter:35CREATE splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	invoice_item_archive_before_delete
BEFORE DELETE ON	invoice_item
/*
**	Name:		invoice_item_archive_before_delete
**	Type:		before delete trigger
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
	SET	@dmlType	= "DELETE";
	SET	@ddlName	= "invoice_item";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(old.revenueStartDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.revenueFinishDate,CURRENT_DATE));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT IFNULL(IFNULL(NAME,"Unknown"),old.updatedBy) FROM users WHERE id = old.updatedBy);
	END IF
	;
	SET @username := IFNULL(@username, CURRENT_USER);

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', FROM_UNIXTIME(@logDate),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(old.invoiceID,"") ,'</oldValue><newValue>', IFNULL(old.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="feeID"><oldValue>', IFNULL(old.feeID,"") ,'</oldValue><newValue>', IFNULL(old.feeID,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(old.amount,"") ,'</oldValue><newValue>', IFNULL(old.amount,"") ,'</newValue></field>'
	,	'	<field column="originalAmount"><oldValue>', IFNULL(old.originalAmount,"") ,'</oldValue><newValue>', IFNULL(old.originalAmount,"") ,'</newValue></field>'
	,	'	<field column="description"><oldValue>', IFNULL(old.description,"") ,'</oldValue><newValue>', IFNULL(old.description,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'
	,	'	<field column="paymentExpires"><oldValue>', IFNULL(old.paymentExpires,"") ,'</oldValue><newValue>', IFNULL(old.paymentExpires,"") ,'</newValue></field>'
	,	'	<field column="qbRefundID"><oldValue>', IFNULL(old.qbRefundID,"") ,'</oldValue><newValue>', IFNULL(old.qbRefundID,"") ,'</newValue></field>'
	,	'	<field column="refunded"><oldValue>', IFNULL(old.refunded,"") ,'</oldValue><newValue>', IFNULL(old.refunded,"") ,'</newValue></field>'
	,	'	<field column="refundFor"><oldValue>', IFNULL(old.refundFor,"") ,'</oldValue><newValue>', IFNULL(old.refundFor,"") ,'</newValue></field>'
	,	'	<field column="transactionType"><oldValue>', IFNULL(old.transactionType,"") ,'</oldValue><newValue>', IFNULL(old.transactionType,"") ,'</newValue></field>'
	,	'	<field column="revenueStartDate"><oldValue>', IFNULL(old.revenueStartDate,"") ,'</oldValue><newValue>', IFNULL(old.revenueStartDate,"") ,'</newValue></field>'
	,	'	<field column="revenueFinishDate"><oldValue>', IFNULL(old.revenueFinishDate,"") ,'</oldValue><newValue>', IFNULL(old.revenueFinishDate,"") ,'</newValue></field>'
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
	--	-------------------------------------------------------------------------
	INSERT INTO	log_archive.log_invoice_item
	VALUES
	(
		@logDate
	,	old.id
	,	old.invoiceID
	,	old.feeID
	,	old.amount
	,	old.originalAmount
	,	old.description
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	,	old.paymentExpires
	,	old.qbRefundID
	,	old.refunded
	,	old.refundFor
	,	old.transactionType
	,	old.revenueStartDate
	,	old.revenueFinishDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END
;
