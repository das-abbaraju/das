--liquibase formatted sql

--sql:
--changeset sshacter:8a splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS invoice_archive_after_insert;
--sql:
--changeset sshacter:8b splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER invoice_archive_after_insert
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
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.createdBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
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
	,	'	<field column="notes"><oldValue>', IFNULL(new.notes,"") ,'</oldValue><newValue>', IFNULL(new.notes,"") ,'</newValue></field>'
	,	'	<field column="currency"><oldValue>', IFNULL(new.currency,"") ,'</oldValue><newValue>', IFNULL(new.currency,"") ,'</newValue></field>'
	,	'	<field column="lateFeeInvoiceID"><oldValue>', IFNULL(new.lateFeeInvoiceID,"") ,'</oldValue><newValue>', IFNULL(new.lateFeeInvoiceID,"") ,'</newValue></field>'
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
	INSERT INTO	log_invoice
	VALUES
	(
		@logEventID
	,	new.id
	,	new.accountID
	,	new.tableType
	,	new.invoiceType
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	,	new.dueDate
	,	new.STATUS
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
	,	new.notes
	,	new.currency
	,	new.lateFeeInvoiceID
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;
--sql:
--changeset sshacter:8c splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_archive_after_update;
--sql:
--changeset sshacter:8d splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */	TRIGGER	invoice_archive_after_update
AFTER UPDATE ON 	invoice
/*
**	Name:		invoice_archive_after_update
**	Type:		after update trigger
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
	SET	@dmlType	= "UPDATE";
	SET	@ddlName	= "invoice";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="accountID"><oldValue>', IFNULL(old.accountID,"") ,'</oldValue><newValue>', IFNULL(new.accountID,"") ,'</newValue></field>'
	,	'	<field column="tableType"><oldValue>', IFNULL(old.tableType,"") ,'</oldValue><newValue>', IFNULL(new.tableType,"") ,'</newValue></field>'
	,	'	<field column="invoiceType"><oldValue>', IFNULL(old.invoiceType,"") ,'</oldValue><newValue>', IFNULL(new.invoiceType,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="dueDate"><oldValue>', IFNULL(old.dueDate,"") ,'</oldValue><newValue>', IFNULL(new.dueDate,"") ,'</newValue></field>'
	,	'	<field column="status"><oldValue>', IFNULL(old.status,"") ,'</oldValue><newValue>', IFNULL(new.status,"") ,'</newValue></field>'
	,	'	<field column="totalAmount"><oldValue>', IFNULL(old.totalAmount,"") ,'</oldValue><newValue>', IFNULL(new.totalAmount,"") ,'</newValue></field>'
	,	'	<field column="amountApplied"><oldValue>', IFNULL(old.amountApplied,"") ,'</oldValue><newValue>', IFNULL(new.amountApplied,"") ,'</newValue></field>'
	,	'	<field column="commissionableAmount"><oldValue>', IFNULL(old.commissionableAmount,"") ,'</oldValue><newValue>', IFNULL(new.commissionableAmount,"") ,'</newValue></field>'
	,	'	<field column="paidDate"><oldValue>', IFNULL(old.paidDate,"") ,'</oldValue><newValue>', IFNULL(new.paidDate,"") ,'</newValue></field>'
	,	'	<field column="paymentMethod"><oldValue>', IFNULL(old.paymentMethod,"") ,'</oldValue><newValue>', IFNULL(new.paymentMethod,"") ,'</newValue></field>'
	,	'	<field column="checkNumber"><oldValue>', IFNULL(old.checkNumber,"") ,'</oldValue><newValue>', IFNULL(new.checkNumber,"") ,'</newValue></field>'
	,	'	<field column="transactionID"><oldValue>', IFNULL(old.transactionID,"") ,'</oldValue><newValue>', IFNULL(new.transactionID,"") ,'</newValue></field>'
	,	'	<field column="poNumber"><oldValue>', IFNULL(old.poNumber,"") ,'</oldValue><newValue>', IFNULL(new.poNumber,"") ,'</newValue></field>'
	,	'	<field column="ccNumber"><oldValue>', IFNULL(old.ccNumber,"") ,'</oldValue><newValue>', IFNULL(new.ccNumber,"") ,'</newValue></field>'
	,	'	<field column="qbSync"><oldValue>', IFNULL(old.qbSync,"") ,'</oldValue><newValue>', IFNULL(new.qbSync,"") ,'</newValue></field>'
	,	'	<field column="sapLastSync"><oldValue>', IFNULL(old.sapLastSync,"") ,'</oldValue><newValue>', IFNULL(new.sapLastSync,"") ,'</newValue></field>'
	,	'	<field column="sapSync"><oldValue>', IFNULL(old.sapSync,"") ,'</oldValue><newValue>', IFNULL(new.sapSync,"") ,'</newValue></field>'
	,	'	<field column="sapID"><oldValue>', IFNULL(old.sapID,"") ,'</oldValue><newValue>', IFNULL(new.sapID,"") ,'</newValue></field>'
	,	'	<field column="qbListID"><oldValue>', IFNULL(old.qbListID,"") ,'</oldValue><newValue>', IFNULL(new.qbListID,"") ,'</newValue></field>'
	,	'	<field column="qbSyncWithTax"><oldValue>', IFNULL(old.qbSyncWithTax,"") ,'</oldValue><newValue>', IFNULL(new.qbSyncWithTax,"") ,'</newValue></field>'
	,	'	<field column="notes"><oldValue>', IFNULL(old.notes,"") ,'</oldValue><newValue>', IFNULL(new.notes,"") ,'</newValue></field>'
	,	'	<field column="currency"><oldValue>', IFNULL(old.currency,"") ,'</oldValue><newValue>', IFNULL(new.currency,"") ,'</newValue></field>'
	,	'	<field column="lateFeeInvoiceID"><oldValue>', IFNULL(old.lateFeeInvoiceID,"") ,'</oldValue><newValue>', IFNULL(new.lateFeeInvoiceID,"") ,'</newValue></field>'
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
	INSERT INTO	log_invoice
	VALUES
	(
		@logEventID
	,	new.id
	,	new.accountID
	,	new.tableType
	,	new.invoiceType
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	,	new.dueDate
	,	new.STATUS
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
	,	new.notes
	,	new.currency
	,	new.lateFeeInvoiceID
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;
--sql:
--changeset sshacter:8e splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_archive_before_delete;
--sql:
--changeset sshacter:8f splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_archive_before_delete
BEFORE DELETE ON	invoice
/*
**	Name:		invoice_archive_before_delete
**	Type:		before delete trigger
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
	SET	@dmlType	= "DELETE";
	SET	@ddlName	= "invoice";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.updateDate,CURRENT_DATE));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = old.createdBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="accountID"><oldValue>', IFNULL(old.accountID,"") ,'</oldValue><newValue>', IFNULL(old.accountID,"") ,'</newValue></field>'
	,	'	<field column="tableType"><oldValue>', IFNULL(old.tableType,"") ,'</oldValue><newValue>', IFNULL(old.tableType,"") ,'</newValue></field>'
	,	'	<field column="invoiceType"><oldValue>', IFNULL(old.invoiceType,"") ,'</oldValue><newValue>', IFNULL(old.invoiceType,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'
	,	'	<field column="dueDate"><oldValue>', IFNULL(old.dueDate,"") ,'</oldValue><newValue>', IFNULL(old.dueDate,"") ,'</newValue></field>'
	,	'	<field column="status"><oldValue>', IFNULL(old.status,"") ,'</oldValue><newValue>', IFNULL(old.status,"") ,'</newValue></field>'
	,	'	<field column="totalAmount"><oldValue>', IFNULL(old.totalAmount,"") ,'</oldValue><newValue>', IFNULL(old.totalAmount,"") ,'</newValue></field>'
	,	'	<field column="amountApplied"><oldValue>', IFNULL(old.amountApplied,"") ,'</oldValue><newValue>', IFNULL(old.amountApplied,"") ,'</newValue></field>'
	,	'	<field column="commissionableAmount"><oldValue>', IFNULL(old.commissionableAmount,"") ,'</oldValue><newValue>', IFNULL(old.commissionableAmount,"") ,'</newValue></field>'
	,	'	<field column="paidDate"><oldValue>', IFNULL(old.paidDate,"") ,'</oldValue><newValue>', IFNULL(old.paidDate,"") ,'</newValue></field>'
	,	'	<field column="paymentMethod"><oldValue>', IFNULL(old.paymentMethod,"") ,'</oldValue><newValue>', IFNULL(old.paymentMethod,"") ,'</newValue></field>'
	,	'	<field column="checkNumber"><oldValue>', IFNULL(old.checkNumber,"") ,'</oldValue><newValue>', IFNULL(old.checkNumber,"") ,'</newValue></field>'
	,	'	<field column="transactionID"><oldValue>', IFNULL(old.transactionID,"") ,'</oldValue><newValue>', IFNULL(old.transactionID,"") ,'</newValue></field>'
	,	'	<field column="poNumber"><oldValue>', IFNULL(old.poNumber,"") ,'</oldValue><newValue>', IFNULL(old.poNumber,"") ,'</newValue></field>'
	,	'	<field column="ccNumber"><oldValue>', IFNULL(old.ccNumber,"") ,'</oldValue><newValue>', IFNULL(old.ccNumber,"") ,'</newValue></field>'
	,	'	<field column="qbSync"><oldValue>', IFNULL(old.qbSync,"") ,'</oldValue><newValue>', IFNULL(old.qbSync,"") ,'</newValue></field>'
	,	'	<field column="sapLastSync"><oldValue>', IFNULL(old.sapLastSync,"") ,'</oldValue><newValue>', IFNULL(old.sapLastSync,"") ,'</newValue></field>'
	,	'	<field column="sapSync"><oldValue>', IFNULL(old.sapSync,"") ,'</oldValue><newValue>', IFNULL(old.sapSync,"") ,'</newValue></field>'
	,	'	<field column="sapID"><oldValue>', IFNULL(old.sapID,"") ,'</oldValue><newValue>', IFNULL(old.sapID,"") ,'</newValue></field>'
	,	'	<field column="qbListID"><oldValue>', IFNULL(old.qbListID,"") ,'</oldValue><newValue>', IFNULL(old.qbListID,"") ,'</newValue></field>'
	,	'	<field column="qbSyncWithTax"><oldValue>', IFNULL(old.qbSyncWithTax,"") ,'</oldValue><newValue>', IFNULL(old.qbSyncWithTax,"") ,'</newValue></field>'
	,	'	<field column="notes"><oldValue>', IFNULL(old.notes,"") ,'</oldValue><newValue>', IFNULL(old.notes,"") ,'</newValue></field>'
	,	'	<field column="currency"><oldValue>', IFNULL(old.currency,"") ,'</oldValue><newValue>', IFNULL(old.currency,"") ,'</newValue></field>'
	,	'	<field column="lateFeeInvoiceID"><oldValue>', IFNULL(old.lateFeeInvoiceID,"") ,'</oldValue><newValue>', IFNULL(old.lateFeeInvoiceID,"") ,'</newValue></field>'
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
	INSERT INTO	log_invoice
	VALUES
	(
		@logEventID
	,	old.id
	,	old.accountID
	,	old.tableType
	,	old.invoiceType
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	,	old.dueDate
	,	old.STATUS
	,	old.totalAmount
	,	old.amountApplied
	,	old.commissionableAmount
	,	old.paidDate
	,	old.paymentMethod
	,	old.checkNumber
	,	old.transactionID
	,	old.poNumber
	,	old.ccNumber
	,	old.qbSync
	,	old.sapLastSync
	,	old.sapSync
	,	old.sapID
	,	old.qbListID
	,	old.qbSyncWithTax
	,	old.notes
	,	old.currency
	,	old.lateFeeInvoiceID
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;
--sql:
--changeset sshacter:8g splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_item_archive_after_insert
--sql:
--changeset sshacter:8h splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_item_archive_after_insert
AFTER INSERT ON		invoice_item
/*
**	Name:		invoice_item_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert invoice_item change history into the logging tables
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
	SET	@ddlName	= "invoice_item";
	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.createdBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(new.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(new.invoiceID,"") ,'</oldValue><newValue>', IFNULL(new.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="feeID"><oldValue>', IFNULL(new.feeID,"") ,'</oldValue><newValue>', IFNULL(new.feeID,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(new.amount,"") ,'</oldValue><newValue>', IFNULL(new.amount,"") ,'</newValue></field>'
	,	'	<field column="description"><oldValue>', IFNULL(new.description,"") ,'</oldValue><newValue>', IFNULL(new.description,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="paymentExpires"><oldValue>', IFNULL(new.paymentExpires,"") ,'</oldValue><newValue>', IFNULL(new.paymentExpires,"") ,'</newValue></field>'
	,	'	<field column="qbRefundID"><oldValue>', IFNULL(new.qbRefundID,"") ,'</oldValue><newValue>', IFNULL(new.qbRefundID,"") ,'</newValue></field>'
	,	'	<field column="refunded"><oldValue>', IFNULL(new.refunded,"") ,'</oldValue><newValue>', IFNULL(new.refunded,"") ,'</newValue></field>'
	,	'	<field column="refundFor"><oldValue>', IFNULL(new.refundFor,"") ,'</oldValue><newValue>', IFNULL(new.refundFor,"") ,'</newValue></field>'
	,	'	<field column="transactionType"><oldValue>', IFNULL(new.transactionType,"") ,'</oldValue><newValue>', IFNULL(new.transactionType,"") ,'</newValue></field>'
	,	'	<field column="revenueStartDate"><oldValue>', IFNULL(new.revenueStartDate,"") ,'</oldValue><newValue>', IFNULL(new.revenueStartDate,"") ,'</newValue></field>'
	,	'	<field column="revenueFinishDate"><oldValue>', IFNULL(new.revenueFinishDate,"") ,'</oldValue><newValue>', IFNULL(new.revenueFinishDate,"") ,'</newValue></field>'
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
	,	DATE(IFNULL(new.revenueStartDate, new.creationDate))
	,	DATE(IFNULL(new.revenueFinishDate, "9999-12-31"))
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
	INSERT INTO	log_invoice_item
	VALUES
	(
		@logEventID
	,	new.id
	,	new.invoiceID
	,	new.feeID
	,	new.amount
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
END ;
--sql:
--changeset sshacter:8i splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_item_archive_after_update
--sql:
--changeset sshacter:8j splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_item_archive_after_update
AFTER UPDATE ON		invoice_item
/*
**	Name:		invoice_item_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert invoice_item change history into the logging tables
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
	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(old.invoiceID,"") ,'</oldValue><newValue>', IFNULL(new.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="feeID"><oldValue>', IFNULL(old.feeID,"") ,'</oldValue><newValue>', IFNULL(new.feeID,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(old.amount,"") ,'</oldValue><newValue>', IFNULL(new.amount,"") ,'</newValue></field>'
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
	,	DATE(IFNULL(new.revenueStartDate, new.creationDate))
	,	DATE(IFNULL(new.revenueFinishDate, "9999-12-31"))
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
	INSERT INTO	log_invoice_item
	VALUES
	(
		@logEventID
	,	new.id
	,	new.invoiceID
	,	new.feeID
	,	new.amount
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
END ;
--sql:
--changeset sshacter:8k splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_item_archive_before_delete
--sql:
--changeset sshacter:8l splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_item_archive_before_delete
BEFORE DELETE ON		invoice_item
/*
**	Name:		invoice_item_archive_before_delete
**	Type:		before delete trigger
**	Purpose:	To insert invoice_item change history into the logging tables
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
	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = old.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(old.invoiceID,"") ,'</oldValue><newValue>', IFNULL(old.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="feeID"><oldValue>', IFNULL(old.feeID,"") ,'</oldValue><newValue>', IFNULL(old.feeID,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(old.amount,"") ,'</oldValue><newValue>', IFNULL(old.amount,"") ,'</newValue></field>'
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
	,	DATE(IFNULL(old.revenueStartDate, old.creationDate))
	,	DATE(IFNULL(old.revenueFinishDate, "9999-12-31"))
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
	INSERT INTO	log_invoice_item
	VALUES
	(
		@logEventID
	,	old.id
	,	old.invoiceID
	,	old.feeID
	,	old.amount
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
END ;
--sql:
--changeset sshacter:8m splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_commission_archive_after_insert
--sql:
--changeset sshacter:8n splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_commission_archive_after_insert
AFTER INSERT ON		invoice_commission
/*
**	Name:		invoice_commission_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert invoice_commission change history into the logging tables
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
	SET	@ddlName	= "invoice_commission";
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.createdBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(new.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(new.invoiceID,"") ,'</oldValue><newValue>', IFNULL(new.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="userID"><oldValue>', IFNULL(new.userID,"") ,'</oldValue><newValue>', IFNULL(new.userID,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="activationPoints"><oldValue>', IFNULL(new.activationPoints,"") ,'</oldValue><newValue>', IFNULL(new.activationPoints,"") ,'</newValue></field>'
	,	'	<field column="revenue"><oldValue>', IFNULL(new.revenue,"") ,'</oldValue><newValue>', IFNULL(new.revenue,"") ,'</newValue></field>'
	,	'	<field column="accountUserID"><oldValue>', IFNULL(new.accountUserID,"") ,'</oldValue><newValue>', IFNULL(new.accountUserID,"") ,'</newValue></field>'
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
	INSERT INTO	log_invoice_commission
	VALUES
	(
		@logEventID
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
END ;
--sql:
--changeset sshacter:8o splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_commission_archive_after_update
--sql:
--changeset sshacter:8p splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_commission_archive_after_update
AFTER UPDATE ON		invoice_commission
/*
**	Name:		invoice_commission_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert invoice_commission change history into the logging tables
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
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
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
	INSERT INTO	log_invoice_commission
	VALUES
	(
		@logEventID
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
END ;
--sql:
--changeset sshacter:8q splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_commission_archive_before_delete
--sql:
--changeset sshacter:8r splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_commission_archive_before_delete
BEFORE DELETE ON		invoice_commission
/*
**	Name:		invoice_commission_archive_before_delete
**	Type:		before delete trigger
**	Purpose:	To insert invoice_commission change history into the logging tables
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
	SET	@ddlName	= "invoice_commission";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.updateDate,CURRENT_DATE));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = old.accountUserID);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(old.invoiceID,"") ,'</oldValue><newValue>', IFNULL(old.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="userID"><oldValue>', IFNULL(old.userID,"") ,'</oldValue><newValue>', IFNULL(old.userID,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'
	,	'	<field column="activationPoints"><oldValue>', IFNULL(old.activationPoints,"") ,'</oldValue><newValue>', IFNULL(old.activationPoints,"") ,'</newValue></field>'
	,	'	<field column="revenue"><oldValue>', IFNULL(old.revenue,"") ,'</oldValue><newValue>', IFNULL(old.revenue,"") ,'</newValue></field>'
	,	'	<field column="accountUserID"><oldValue>', IFNULL(old.accountUserID,"") ,'</oldValue><newValue>', IFNULL(old.accountUserID,"") ,'</newValue></field>'
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
	INSERT INTO	log_invoice_commission
	VALUES
	(
		@logEventID
	,	old.id
	,	old.invoiceID
	,	old.userID
	,	old.createdBy
	,	old.creationDate
	,	old.updatedBy
	,	old.updateDate
	,	old.activationPoints
	,	old.revenue
	,	old.accountUserID
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;
--sql:
--changeset sshacter:8s splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_fee_archive_after_insert
--sql:
--changeset sshacter:8t splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_fee_archive_after_insert
AFTER INSERT ON		invoice_fee
/*
**	Name:		invoice_fee_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert invoice_fee change history into the logging tables
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
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.createdBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
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
	INSERT INTO	log_invoice_fee
	VALUES
	(
		@logEventID
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
END ;
--sql:
--changeset sshacter:8u splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_fee_archive_after_update
--sql:
--changeset sshacter:8v splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_fee_archive_after_update
AFTER UPDATE ON		invoice_fee
/*
**	Name:		invoice_fee_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert invoice_fee change history into the logging tables
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
	SET	@ddlName	= "invoice_fee";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="fee"><oldValue>', IFNULL(old.fee,"") ,'</oldValue><newValue>', IFNULL(new.fee,"") ,'</newValue></field>'
	,	'	<field column="defaultAmount"><oldValue>', IFNULL(old.defaultAmount,"") ,'</oldValue><newValue>', IFNULL(new.defaultAmount,"") ,'</newValue></field>'
	,	'	<field column="ratePercent"><oldValue>', IFNULL(old.ratePercent,"") ,'</oldValue><newValue>', IFNULL(new.ratePercent,"") ,'</newValue></field>'
	,	'	<field column="visible"><oldValue>', IFNULL(old.visible,"") ,'</oldValue><newValue>', IFNULL(new.visible,"") ,'</newValue></field>'
	,	'	<field column="feeClass"><oldValue>', IFNULL(old.feeClass,"") ,'</oldValue><newValue>', IFNULL(new.feeClass,"") ,'</newValue></field>'
	,	'	<field column="minFacilities"><oldValue>', IFNULL(old.minFacilities,"") ,'</oldValue><newValue>', IFNULL(new.minFacilities,"") ,'</newValue></field>'
	,	'	<field column="maxFacilities"><oldValue>', IFNULL(old.maxFacilities,"") ,'</oldValue><newValue>', IFNULL(new.maxFacilities,"") ,'</newValue></field>'
	,	'	<field column="qbFullName"><oldValue>', IFNULL(old.qbFullName,"") ,'</oldValue><newValue>', IFNULL(new.qbFullName,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="effectiveDate"><oldValue>', IFNULL(old.effectiveDate,"") ,'</oldValue><newValue>', IFNULL(new.effectiveDate,"") ,'</newValue></field>'
	,	'	<field column="displayOrder"><oldValue>', IFNULL(old.displayOrder,"") ,'</oldValue><newValue>', IFNULL(new.displayOrder,"") ,'</newValue></field>'
	,	'	<field column="commissionEligible"><oldValue>', IFNULL(old.commissionEligible,"") ,'</oldValue><newValue>', IFNULL(new.commissionEligible,"") ,'</newValue></field>'
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
	INSERT INTO	log_invoice_fee
	VALUES
	(
		@logEventID
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
END ;
--sql:
--changeset sshacter:8w splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_fee_archive_before_delete
--sql:
--changeset sshacter:8x splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_fee_archive_before_delete
BEFORE DELETE ON		invoice_fee
/*
**	Name:		invoice_fee_archive_before_delete
**	Type:		before delete trigger
**	Purpose:	To insert invoice_fee change history into the logging tables
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
	SET	@ddlName	= "invoice_fee";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.effectiveDate,CURRENT_DATE));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = old.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'
	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="fee"><oldValue>', IFNULL(old.fee,"") ,'</oldValue><newValue>', IFNULL(old.fee,"") ,'</newValue></field>'
	,	'	<field column="defaultAmount"><oldValue>', IFNULL(old.defaultAmount,"") ,'</oldValue><newValue>', IFNULL(old.defaultAmount,"") ,'</newValue></field>'
	,	'	<field column="ratePercent"><oldValue>', IFNULL(old.ratePercent,"") ,'</oldValue><newValue>', IFNULL(old.ratePercent,"") ,'</newValue></field>'
	,	'	<field column="visible"><oldValue>', IFNULL(old.visible,"") ,'</oldValue><newValue>', IFNULL(old.visible,"") ,'</newValue></field>'
	,	'	<field column="feeClass"><oldValue>', IFNULL(old.feeClass,"") ,'</oldValue><newValue>', IFNULL(old.feeClass,"") ,'</newValue></field>'
	,	'	<field column="minFacilities"><oldValue>', IFNULL(old.minFacilities,"") ,'</oldValue><newValue>', IFNULL(old.minFacilities,"") ,'</newValue></field>'
	,	'	<field column="maxFacilities"><oldValue>', IFNULL(old.maxFacilities,"") ,'</oldValue><newValue>', IFNULL(old.maxFacilities,"") ,'</newValue></field>'
	,	'	<field column="qbFullName"><oldValue>', IFNULL(old.qbFullName,"") ,'</oldValue><newValue>', IFNULL(old.qbFullName,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'
	,	'	<field column="effectiveDate"><oldValue>', IFNULL(old.effectiveDate,"") ,'</oldValue><newValue>', IFNULL(old.effectiveDate,"") ,'</newValue></field>'
	,	'	<field column="displayOrder"><oldValue>', IFNULL(old.displayOrder,"") ,'</oldValue><newValue>', IFNULL(old.displayOrder,"") ,'</newValue></field>'
	,	'	<field column="commissionEligible"><oldValue>', IFNULL(old.commissionEligible,"") ,'</oldValue><newValue>', IFNULL(old.commissionEligible,"") ,'</newValue></field>'
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
	INSERT INTO	log_invoice_fee
	VALUES
	(
		@logEventID
	,	old.id
	,	old.fee
	,	old.defaultAmount
	,	old.ratePercent
	,	old.visible
	,	old.feeClass
	,	old.minFacilities
	,	old.maxFacilities
	,	old.qbFullName
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	,	old.effectiveDate
	,	old.displayOrder
	,	old.commissionEligible
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;
--sql:
--changeset sshacter:8y splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS	invoice_fee_country_archive_after_insert;
--sql:
--changeset sshacter:8z splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_fee_country_archive_after_insert
AFTER INSERT ON		invoice_fee_country
/*
**	Name:		invoice_fee_country_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert invoice_fee_country change history into the logging tables
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
	SET	@validStart	= DATE(IFNULL(new.effectiveDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.expirationDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.createdBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
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
	INSERT INTO	log_invoice_fee_country
	VALUES
	(
		@logEventID
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
END ;
--sql:
--changeset sshacter:8aa splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS	invoice_fee_country_archive_after_update;
--sql:
--changeset sshacter:8bb splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_fee_country_archive_after_update
AFTER UPDATE ON		invoice_fee_country
/*
**	Name:		invoice_fee_country_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert invoice_fee_country change history into the logging tables
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
	SET	@ddlName	= "invoice_fee_country";
	SET	@validStart	= DATE(IFNULL(new.effectiveDate,old.effectiveDate));
	SET	@validFinish	= DATE(IFNULL(new.expirationDate,old.expirationDate));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="feeID"><oldValue>', IFNULL(old.feeID,"") ,'</oldValue><newValue>', IFNULL(new.feeID,"") ,'</newValue></field>'
	,	'	<field column="country"><oldValue>', IFNULL(old.country,"") ,'</oldValue><newValue>', IFNULL(new.country,"") ,'</newValue></field>'
	,	'	<field column="subdivision"><oldValue>', IFNULL(old.subdivision,"") ,'</oldValue><newValue>', IFNULL(new.subdivision,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(old.amount,"") ,'</oldValue><newValue>', IFNULL(new.amount,"") ,'</newValue></field>'
	,	'	<field column="ratePercent"><oldValue>', IFNULL(old.ratePercent,"") ,'</oldValue><newValue>', IFNULL(new.ratePercent,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="effectiveDate"><oldValue>', IFNULL(old.effectiveDate,"") ,'</oldValue><newValue>', IFNULL(new.effectiveDate,"") ,'</newValue></field>'
	,	'	<field column="expirationDate"><oldValue>', IFNULL(old.expirationDate,"") ,'</oldValue><newValue>', IFNULL(new.expirationDate,"") ,'</newValue></field>'

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
	INSERT INTO	log_invoice_fee_country
	VALUES
	(
		@logEventID
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
END ;
--sql:
--changeset sshacter:8cc splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS invoice_fee_country_archive_before_delete
--sql:
--changeset sshacter:8dd splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_fee_country_archive_before_delete
BEFORE DELETE ON		invoice_fee_country
/*
**	Name:		invoice_fee_country_archive_before_delete
**	Type:		before delete trigger
**	Purpose:	To insert invoice_fee_country change history into the logging tables
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
	SET	@ddlName	= "invoice_fee_country";
	SET	@validStart	= DATE(IFNULL(old.effectiveDate,old.creationDate));
	SET	@validFinish	= DATE(IFNULL(old.expirationDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = old.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="feeID"><oldValue>', IFNULL(old.feeID,"") ,'</oldValue><newValue>', IFNULL(old.feeID,"") ,'</newValue></field>'
	,	'	<field column="country"><oldValue>', IFNULL(old.country,"") ,'</oldValue><newValue>', IFNULL(old.country,"") ,'</newValue></field>'
	,	'	<field column="subdivision"><oldValue>', IFNULL(old.subdivision,"") ,'</oldValue><newValue>', IFNULL(old.subdivision,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(old.amount,"") ,'</oldValue><newValue>', IFNULL(old.amount,"") ,'</newValue></field>'
	,	'	<field column="ratePercent"><oldValue>', IFNULL(old.ratePercent,"") ,'</oldValue><newValue>', IFNULL(old.ratePercent,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'
	,	'	<field column="effectiveDate"><oldValue>', IFNULL(old.effectiveDate,"") ,'</oldValue><newValue>', IFNULL(old.effectiveDate,"") ,'</newValue></field>'
	,	'	<field column="expirationDate"><oldValue>', IFNULL(old.expirationDate,"") ,'</oldValue><newValue>', IFNULL(old.expirationDate,"") ,'</newValue></field>'

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
	INSERT INTO	log_invoice_fee_country
	VALUES
	(
		@logEventID
	,	old.id
	,	old.feeID
	,	old.country
	,	old.subdivision
	,	old.amount
	,	old.ratePercent
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	,	old.effectiveDate
	,	old.expirationDate
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;
--sql:
--changeset sshacter:8ee splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS	invoice_payment_archive_after_insert
--sql:
--changeset sshacter:8ff splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_payment_archive_after_insert
AFTER INSERT ON		invoice_payment
/*
**	Name:		invoice_payment_archive_after_insert
**	Type:		after insert trigger
**	Purpose:	To insert invoice_payment change history into the logging tables
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
	SET	@ddlName	= "invoice_payment";
	SET	@validStart	= DATE(IFNULL(new.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.createdBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(new.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="paymentID"><oldValue>', IFNULL(new.paymentID,"") ,'</oldValue><newValue>', IFNULL(new.paymentID,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(new.invoiceID,"") ,'</oldValue><newValue>', IFNULL(new.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="refundID"><oldValue>', IFNULL(new.refundID,"") ,'</oldValue><newValue>', IFNULL(new.refundID,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(new.amount,"") ,'</oldValue><newValue>', IFNULL(new.amount,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(new.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(new.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(new.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(new.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="paymentType"><oldValue>', IFNULL(new.paymentType,"") ,'</oldValue><newValue>', IFNULL(new.paymentType,"") ,'</newValue></field>'

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
	INSERT INTO	log_invoice_payment
	VALUES
	(
		@logEventID
	,	new.id
	,	new.paymentID
	,	new.invoiceID
	,	new.refundID
	,	new.amount
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	,	new.paymentType
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;
--sql:
--changeset sshacter:8gg splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS	invoice_payment_archive_after_update;
--sql:
--changeset sshacter:8hh splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_payment_archive_after_update
AFTER UPDATE ON		invoice_payment
/*
**	Name:		invoice_payment_archive_after_update
**	Type:		after update trigger
**	Purpose:	To insert invoice_payment change history into the logging tables
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
	SET	@ddlName	= "invoice_payment";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(new.updateDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = new.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(new.id,"") ,'</newValue></field>'
	,	'	<field column="paymentID"><oldValue>', IFNULL(old.paymentID,"") ,'</oldValue><newValue>', IFNULL(new.paymentID,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(old.invoiceID,"") ,'</oldValue><newValue>', IFNULL(new.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="refundID"><oldValue>', IFNULL(old.refundID,"") ,'</oldValue><newValue>', IFNULL(new.refundID,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(old.amount,"") ,'</oldValue><newValue>', IFNULL(new.amount,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(new.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(new.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(new.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(new.updateDate,"") ,'</newValue></field>'
	,	'	<field column="paymentType"><oldValue>', IFNULL(old.paymentType,"") ,'</oldValue><newValue>', IFNULL(new.paymentType,"") ,'</newValue></field>'

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
	INSERT INTO	log_invoice_payment
	VALUES
	(
		@logEventID
	,	new.id
	,	new.paymentID
	,	new.invoiceID
	,	new.refundID
	,	new.amount
	,	new.createdBy
	,	new.updatedBy
	,	new.creationDate
	,	new.updateDate
	,	new.paymentType
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;
--sql:
--changeset sshacter:8ii splitStatements:false endDelimiter:|
DROP TRIGGER IF EXISTS	invoice_payment_archive_before_delete;
--sql:
--changeset sshacter:8jj splitStatements:false endDelimiter:|
CREATE /* DEFINER = 'pics_admin'@'%' */ TRIGGER	invoice_payment_archive_before_delete
BEFORE DELETE ON		invoice_payment
/*
**	Name:		invoice_payment_archive_before_delete
**	Type:		before delete trigger
**	Purpose:	To insert invoice_payment change history into the logging tables
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
	SET	@ddlName	= "invoice_payment";
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.updateDate,"9999-12-31"));

	IF
		LOCATE("pics", CURRENT_USER())	> 0
	THEN
		SET	@username	= (SELECT username FROM users WHERE id = old.updatedBy);
	ELSE
		SET	@username	= CURRENT_USER();
	END IF
	;

	SET	@logEntry	=
	CONCAT
	(
	'<?xml version="1.0"?>\n<logdata logdate="', TIMESTAMP (NOW()),'" operation="',@dmlType,'" user="',@username,'" table="',@ddlName,'" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
	,	'<diff>\n'

	,	'	<field column="id"><oldValue>', IFNULL(old.id,"") ,'</oldValue><newValue>', IFNULL(old.id,"") ,'</newValue></field>'
	,	'	<field column="paymentID"><oldValue>', IFNULL(old.paymentID,"") ,'</oldValue><newValue>', IFNULL(old.paymentID,"") ,'</newValue></field>'
	,	'	<field column="invoiceID"><oldValue>', IFNULL(old.invoiceID,"") ,'</oldValue><newValue>', IFNULL(old.invoiceID,"") ,'</newValue></field>'
	,	'	<field column="refundID"><oldValue>', IFNULL(old.refundID,"") ,'</oldValue><newValue>', IFNULL(old.refundID,"") ,'</newValue></field>'
	,	'	<field column="amount"><oldValue>', IFNULL(old.amount,"") ,'</oldValue><newValue>', IFNULL(old.amount,"") ,'</newValue></field>'
	,	'	<field column="createdBy"><oldValue>', IFNULL(old.createdBy,"") ,'</oldValue><newValue>', IFNULL(old.createdBy,"") ,'</newValue></field>'
	,	'	<field column="updatedBy"><oldValue>', IFNULL(old.updatedBy,"") ,'</oldValue><newValue>', IFNULL(old.updatedBy,"") ,'</newValue></field>'
	,	'	<field column="creationDate"><oldValue>', IFNULL(old.creationDate,"") ,'</oldValue><newValue>', IFNULL(old.creationDate,"") ,'</newValue></field>'
	,	'	<field column="updateDate"><oldValue>', IFNULL(old.updateDate,"") ,'</oldValue><newValue>', IFNULL(old.updateDate,"") ,'</newValue></field>'
	,	'	<field column="paymentType"><oldValue>', IFNULL(old.paymentType,"") ,'</oldValue><newValue>', IFNULL(old.paymentType,"") ,'</newValue></field>'

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
	INSERT INTO	log_invoice_payment
	VALUES
	(
		@logEventID
	,	old.id
	,	old.paymentID
	,	old.invoiceID
	,	old.refundID
	,	old.amount
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	,	old.paymentType
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END ;
