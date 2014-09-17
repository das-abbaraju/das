--liquibase formatted SQL
--sql:
--changeset sshacter:23DROP splitStatements:true endDelimiter:|
DROP TRIGGER IF EXISTS invoice_archive_before_delete
;
--sql:
--changeset sshacter:23CREATE splitStatements:false endDelimiter:|
CREATE DEFINER = 'pics_admin'@'%' TRIGGER	invoice_archive_before_delete
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
	SET	@logDate	= UNIX_TIMESTAMP();
	SET	@validStart	= DATE(IFNULL(old.creationDate,CURRENT_DATE));
	SET	@validFinish	= DATE(IFNULL(old.updateDate,CURRENT_DATE));

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
	,	'	<field column="payingFacilities"><oldValue>', IFNULL(old.payingFacilities,"") ,'</oldValue><newValue>', IFNULL(old.payingFacilities,"") ,'</newValue></field>'
	,	'	<field column="notes"><oldValue>', IFNULL(old.notes,"") ,'</oldValue><newValue>', IFNULL(old.notes,"") ,'</newValue></field>'
	,	'	<field column="currency"><oldValue>', IFNULL(old.currency,"") ,'</oldValue><newValue>', IFNULL(old.currency,"") ,'</newValue></field>'
	,	'	<field column="lateFeeInvoiceID"><oldValue>', IFNULL(old.lateFeeInvoiceID,"") ,'</oldValue><newValue>', IFNULL(old.lateFeeInvoiceID,"") ,'</newValue></field>'
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
	INSERT INTO	log_archive.log_invoice
	VALUES
	(
		@logDate
	,	old.id
	,	old.accountID
	,	old.tableType
	,	old.invoiceType
	,	old.createdBy
	,	old.updatedBy
	,	old.creationDate
	,	old.updateDate
	,	old.dueDate
	,	old.status
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
	,	old.payingFacilities
	,	old.notes
	,	old.currency
	,	old.lateFeeInvoiceID
	)
	;
	--	-------------------------------------------------------------------------
	--	-------------------------------------------------------------------------
END
;