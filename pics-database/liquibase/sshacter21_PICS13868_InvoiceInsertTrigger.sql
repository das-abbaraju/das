--liquibase formatted SQL
--sql:
--changeset sshacter:21DROP splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS invoice_archive_after_insert
;
--sql:
--changeset sshacter:21CREATE splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	invoice_archive_after_insert
AFTER INSERT ON		invoice
/*
**	Name:		invoice_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert invoice change history into the logging tables
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
	SET	@ddlName	= "invoice";
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));

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
	,	'	<field column="accountID"><oldValue>', IFNULL(new.accountID,"") ,'</oldValue><newValue>', IFNULL(new.accountID,"") ,'</newValue></field>'
	,	'	<field column="tableType"><oldValue>', IFNULL(new.tableType,"") ,'</oldValue><newValue>', IFNULL(new.tableType,"") ,'</newValue></field>'
	,	'	<field column="invoiceType"><oldValue>', IFNULL(new.invoiceType,"") ,'</oldValue><newValue>', IFNULL(new.invoiceType,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="dueDate"><oldValue>', IFNULL(new.dueDate,"") ,'</oldValue><newValue>', IFNULL(new.dueDate,"") ,'</newValue></field>'
	,	'	<field column="status"><oldValue>', IFNULL(new.status,"") ,'</oldValue><newValue>', IFNULL(new.status,"") ,'</newValue></field>'
	,	'	<field column="totalAmount"><oldValue>', IFNULL(new.totalAmount,"") ,'</oldValue><newValue>', IFNULL(new.totalAmount,"") ,'</newValue></field>'
	,	'	<field column="amountApplied"><oldValue>', IFNULL(new.amountApplied,"") ,'</oldValue><newValue>', IFNULL(new.amountApplied,"") ,'</newValue></field>'
	,	'	<field column="commissionableAmount"><oldValue>', IFNULL(new.commissionableAmount,"") ,'</oldValue><newValue>', IFNULL(new.commissionableAmount,"") ,'</newValue></field>'
	,	'	<field column="paidDate"><oldValue>', IFNULL(new.paidDate,"") ,'</oldValue><newValue>', IFNULL(new.paidDate,"") ,'</newValue></field>'
	,	'	<field column="paymentMethod"><oldValue>', IFNULL(new.paymentMethod,"") ,'</oldValue><newValue>', IFNULL(new.paymentMethod,"") ,'</newValue></field>'
	,	'	<field column="checkNumber"><oldValue>', IFNULL(new.checkNumber,"") ,'</oldValue><newValue>', IFNULL(new.checkNumber,"") ,'</newValue></field>'
	,	'	<field column="transactionID"><oldValue>', IFNULL(new.transactionID,"") ,'</oldValue><newValue>', IFNULL(new.transactionID,"") ,'</newValue></field>'
	,	'	<field column="poNumber"><oldValue>', IFNULL(new.poNumber,"") ,'</oldValue><newValue>', IFNULL(new.poNumber,"") ,'</newValue></field>'
	,	'	<field column="ccNumber"><oldValue>', IFNULL(new.ccNumber,"") ,'</oldValue><newValue>', IFNULL(new.ccNumber,"") ,'</newValue></field>'
	,	'	<field column="qbSync"><oldValue>', IFNULL(new.qbSync,"") ,'</oldValue><newValue>', IFNULL(new.qbSync,"") ,'</newValue></field>'
	,	'	<field column="sapLastSync"><oldValue>', IFNULL(new.sapLastSync,"") ,'</oldValue><newValue>', IFNULL(new.sapLastSync,"") ,'</newValue></field>'
	,	'	<field column="sapSync"><oldValue>', IFNULL(new.sapSync,"") ,'</oldValue><newValue>', IFNULL(new.sapSync,"") ,'</newValue></field>'
	,	'	<field column="sapID"><oldValue>', IFNULL(new.sapID,"") ,'</oldValue><newValue>', IFNULL(new.sapID,"") ,'</newValue></field>'
	,	'	<field column="qbListID"><oldValue>', IFNULL(new.qbListID,"") ,'</oldValue><newValue>', IFNULL(new.qbListID,"") ,'</newValue></field>'
	,	'	<field column="qbSyncWithTax"><oldValue>', IFNULL(new.qbSyncWithTax,"") ,'</oldValue><newValue>', IFNULL(new.qbSyncWithTax,"") ,'</newValue></field>'
	,	'	<field column="payingFacilities"><oldValue>', IFNULL(new.payingFacilities,"") ,'</oldValue><newValue>', IFNULL(new.payingFacilities,"") ,'</newValue></field>'
	,	'	<field column="notes"><oldValue>', IFNULL(new.notes,"") ,'</oldValue><newValue>', IFNULL(new.notes,"") ,'</newValue></field>'
	,	'	<field column="currency"><oldValue>', IFNULL(new.currency,"") ,'</oldValue><newValue>', IFNULL(new.currency,"") ,'</newValue></field>'
	,	'	<field column="lateFeeInvoiceID"><oldValue>', IFNULL(new.lateFeeInvoiceID,"") ,'</oldValue><newValue>', IFNULL(new.lateFeeInvoiceID,"") ,'</newValue></field>'
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
	INSERT INTO	log_archive.log_invoice
	VALUES
	(
		@logDate
	,	new.id
	,	new.accountID
	,	new.tableType
	,	new.invoiceType
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	,	new.dueDate
	,	new.status
	,	new.totalAmount
	,	new.amountApplied
	,	new.commissionableAmount
	,	new.paidDate
	,	new.paymentMethod
	,	new.checkNumber
	,	new.transactionID
	,	new.poNumber
	,	new.ccNumber
	,	new.qbSync
	,	new.sapLastSync
	,	new.sapSync
	,	new.sapID
	,	new.qbListID
	,	new.qbSyncWithTax
	,	new.payingFacilities
	,	new.notes
	,	new.currency
	,	new.lateFeeInvoiceID
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END
;
