/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
where os.applicable = 1
and pcd.applies = 'No'
and pcd.catID = 151; 

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
join contractor_audit ca on ca.id = os.auditid 
where os.applicable = 0
and pcd.applies = 'Yes'
and pcd.catID = 151;


**/

ALTER TABLE `invoice` 
	CHANGE `dueDate` `dueDate` date   NULL after `updateDate`, 
	CHANGE `status` `status` varchar(10)  COLLATE latin1_swedish_ci NULL after `dueDate`, 
	CHANGE `totalAmount` `totalAmount` decimal(9,2)   NULL after `status`, 
	CHANGE `amountApplied` `amountApplied` decimal(9,2)   NULL after `totalAmount`, 
	CHANGE `paidDate` `paidDate` datetime   NULL after `amountApplied`, 
	CHANGE `paymentMethod` `paymentMethod` varchar(30)  COLLATE latin1_swedish_ci NULL after `paidDate`, 
	CHANGE `checkNumber` `checkNumber` varchar(50)  COLLATE latin1_swedish_ci NULL after `paymentMethod`, 
	CHANGE `transactionID` `transactionID` varchar(50)  COLLATE latin1_swedish_ci NULL after `checkNumber`, 
	CHANGE `poNumber` `poNumber` varchar(20)  COLLATE latin1_swedish_ci NULL after `transactionID`, 
	CHANGE `ccNumber` `ccNumber` varchar(20)  COLLATE latin1_swedish_ci NULL after `poNumber`, 
	CHANGE `qbSync` `qbSync` tinyint(4)   NOT NULL DEFAULT '0' after `ccNumber`, 
	CHANGE `qbListID` `qbListID` varchar(25)  COLLATE latin1_swedish_ci NULL after `qbSync`, 
	CHANGE `notes` `notes` varchar(1000)  COLLATE latin1_swedish_ci NULL after `qbListID`, 
	DROP COLUMN `txnType`, 
	DROP COLUMN `paid`, 
	DROP COLUMN `qbPaymentListId`, 
	DROP KEY `accountID`, add KEY `accountID`(`accountID`), COMMENT='';

ALTER TABLE `invoice_payment` 
	CHANGE `invoiceID` `invoiceID` int(10) unsigned   NULL after `paymentID`, 
	ADD COLUMN `refundID` int(10) unsigned   NULL after `invoiceID`, 
	CHANGE `amount` `amount` decimal(6,2)   NOT NULL DEFAULT '0.00' after `refundID`, 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `amount`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `createdBy`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `updatedBy`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `creationDate`, 
	ADD COLUMN `paymentType` char(1)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'I' after `updateDate`, 
	ADD UNIQUE KEY `paymentInvoice`(`invoiceID`,`paymentID`,`refundID`), 
	ADD UNIQUE KEY `paymentRefund`(`refundID`,`paymentID`,`invoiceID`), COMMENT='';


-- DROP table pqfdata_duplicates;

create table pqfdata_duplicates as
select auditID, questionID, max(updateDate) updateDate, MAX(id) id
from pqfdata
group by auditID, questionID
having count(*) > 1;

CREATE UNIQUE INDEX questionaudit ON pqfdata_duplicates (questionID, auditID);

delete from pqfdata
where exists (
	select * from pqfdata_duplicates d2
	where pqfdata.auditID = d2.auditID and pqfdata.questionID = d2.questionID and pqfdata.updateDate < d2.updateDate
)

DROP table pqfdata_duplicates;

create table pqfdata_duplicates as
select auditID, questionID, max(updateDate) updateDate, MAX(id) id
from pqfdata
group by auditID, questionID
having count(*) > 1;

CREATE UNIQUE INDEX questionaudit ON pqfdata_duplicates (questionID, auditID);

delete from pqfdata
where exists (
	select * from pqfdata_duplicates d2
	where pqfdata.auditID = d2.auditID and pqfdata.questionID = d2.questionID and pqfdata.id < d2.id
)

DROP INDEX questionContractor ON pqfdata;
CREATE UNIQUE INDEX questionaudit ON pqfdata (questionID, auditID);

DROP table pqfdata_duplicates;

ALTER TABLE `pqfdata` 
	CHANGE `answer` `answer` text  COLLATE latin1_swedish_ci NULL after `questionID`, 
	CHANGE `comment` `comment` varchar(255)  COLLATE latin1_swedish_ci NULL after `answer`, 
	CHANGE `dateVerified` `dateVerified` date   NULL DEFAULT '0000-00-00' after `comment`, 
	CHANGE `auditorID` `auditorID` smallint(5) unsigned   NULL after `dateVerified`, 
	CHANGE `wasChanged` `wasChanged` enum('No','Yes')  COLLATE latin1_swedish_ci NULL DEFAULT 'No' after `auditorID`, 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `wasChanged`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `createdBy`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `creationDate`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `updatedBy`, 
	DROP COLUMN `parentID`, 
	DROP KEY `parentID`, 
	ADD UNIQUE KEY `questionaudit`(`questionID`,`auditID`), 
	DROP KEY `questionContractor`, COMMENT='';

/* Alter table in target */
ALTER TABLE `pqfquestions` 
	CHANGE `isVisible` `isVisible` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `isRequired`, 
	CHANGE `dependsOnQID` `dependsOnQID` smallint(6)   NULL after `isVisible`, 
	CHANGE `dependsOnAnswer` `dependsOnAnswer` varchar(100)  COLLATE latin1_swedish_ci NULL after `dependsOnQID`, 
	CHANGE `questionType` `questionType` varchar(50)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'Text' after `dependsOnAnswer`, 
	CHANGE `title` `title` varchar(250)  COLLATE latin1_swedish_ci NULL after `questionType`, 
	CHANGE `columnHeader` `columnHeader` varchar(30)  COLLATE latin1_swedish_ci NULL after `title`, 
	CHANGE `isGroupedWithPrevious` `isGroupedWithPrevious` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `columnHeader`, 
	CHANGE `isRedFlagQuestion` `isRedFlagQuestion` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `isGroupedWithPrevious`, 
	CHANGE `link` `link` varchar(250)  COLLATE latin1_swedish_ci NULL after `isRedFlagQuestion`, 
	CHANGE `linkText` `linkText` varchar(250)  COLLATE latin1_swedish_ci NULL after `link`, 
	CHANGE `linkURL1` `linkURL1` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText`, 
	CHANGE `linkText1` `linkText1` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL1`, 
	CHANGE `linkURL2` `linkURL2` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText1`, 
	CHANGE `linkText2` `linkText2` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL2`, 
	CHANGE `linkURL3` `linkURL3` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText2`, 
	CHANGE `linkText3` `linkText3` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL3`, 
	CHANGE `linkURL4` `linkURL4` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText3`, 
	CHANGE `linkText4` `linkText4` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL4`, 
	CHANGE `linkURL5` `linkURL5` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText4`, 
	CHANGE `linkText5` `linkText5` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL5`, 
	CHANGE `linkURL6` `linkURL6` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText5`, 
	CHANGE `linkText6` `linkText6` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL6`, 
	CHANGE `uniqueCode` `uniqueCode` varchar(50)  COLLATE latin1_swedish_ci NULL after `linkText6`, 
	CHANGE `showComment` `showComment` tinyint(4)   NOT NULL DEFAULT '0' after `uniqueCode`, 
	CHANGE `riskLevel` `riskLevel` tinyint(4)   NULL after `showComment`, 
	DROP COLUMN `allowMultipleAnswers`, 
	DROP COLUMN `parentID`, 
	DROP COLUMN `minimumTuples`, COMMENT='';
	
/*
Added a new widget for operators to show contractors with Pending Approval
*/
insert into widget 
values (null,"Contractors Pending Approvals", "Html", 0, 
	"ContractorPendingApprovalAjax.action", 
	"ContractorApproval",null);

insert into widget_user 
values (null, newWidgetID, 616, 1, 2,10, null);

/*
 * Added a system message to the app_properties.
 */
insert into app_properties values ('SYSTEM.MESSAGE', '');

delete from invoice where tableType = 'P';

delete from invoice_payment;

delete from load_payment;

delete from load_payment_invoice;

insert into invoice (accountID, tableType, qbListID, createdBy, updatedBy, creationDate, updateDate, status, 
	totalAmount, amountApplied, paidDate, paymentMethod, checkNumber, transactionID, 
	qbSync)
select customerName, 'P', p.txnID, 1, 1, p.creationDate, p.updateDate, case (unusedPayment + unusedCredits) when 0 THEN 'Paid' ELSE 'Unpaid' END, 
	amount, amount - (unusedPayment + unusedCredits), txnDate, paymentMethod, case paymentMethod when 'Check' THEN refNumber ELSE NULL END, case paymentMethod when 'Check' THEN NULL ELSE refNumber END,
	0
from load_payment p
join accounts a on p.customerID = a.qbListID

insert into invoice_payment (paymentID, invoiceID, amount, createdBy, updatedBy, creationDate, updateDate, paymentType)
select p.id, ip.refNumber, amount, 1,1,now(),NOW(),'I'
from load_payment_invoice ip
join invoice p on p.qbListID = ip.paymentID
where txnType = 'Invoice'

update invoice set notes = paymentMethod, paymentMethod = 'CreditCard' where paymentMethod LIKE 'Master%';
update invoice set notes = paymentMethod, paymentMethod = 'CreditCard' where paymentMethod = 'Discover';
update invoice set notes = paymentMethod, paymentMethod = 'CreditCard' where paymentMethod = 'VISA';
update invoice set notes = paymentMethod, paymentMethod = 'CreditCard' where paymentMethod = 'American Express';
update invoice set notes = paymentMethod, paymentMethod = 'CreditCard' where paymentMethod like 'Braintree%';
update invoice set notes = paymentMethod, paymentMethod = 'Check' where paymentMethod = 'EFT';
update invoice set notes = paymentMethod, paymentMethod = 'Check' where paymentMethod = 'Cash';
update invoice set notes = paymentMethod, paymentMethod = 'Check' where paymentMethod = 'Paypal';

select * from app_properties
where property like 'PICSQB%' or property like 'DumpPayment%'
order by property;
