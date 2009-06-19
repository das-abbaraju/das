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
	DROP KEY `accountID`, add KEY `accountID`(`accountID`);

ALTER TABLE `invoice_payment` 
	ADD COLUMN `paymentType` char(1)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'I' after `updateDate`, COMMENT='';


/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/

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

/*
Added a new widget for operators to show contractors with Pending Approval
*/
insert into widget 
values (null,"Contractors Pending Approvals", "Html", 0, 
	"ContractorPendingApprovalAjax.action", 
	"ContractorApproval",null);

insert into widget_user 
values (null, newWidgetID, 616, 1, 2,10, null);>>>>>>> .r5827

/*
 * Added a system message to the app_properties.
 */
 insert into app_properties values ('SYSTEM.MESSAGE', null);

 delete from invoice where tableType = 'P';

delete from invoice_payment;

insert into invoice (qbListID, accountID, tableType, createdBy, updatedBy, creationDate, updateDate, status, totalAmount, amountApplied, paymentMethod, checkNumber, transactionID, poNumber, ccNumber)
select id, accountID, 'P', createdBy, updatedBy, paidDate, paidDate, 'Paid', totalAmount, totalAmount, paymentMethod, checkNumber, transactionID, poNumber, ccNumber 
from invoice where tableType = 'I' and status = 'Paid' and totalAmount > 0;

insert into invoice_payment (paymentID, invoiceID, paymentType, amount, createdBy, updatedBy, creationDate, updateDate)
select p.id, i.id, 'I', i.totalAmount, p.createdBy, p.updatedBy, p.creationDate, p.creationDate
from invoice i, invoice p
where i.tableType = 'I' and p.tableType = 'P'
and i.id = p.qbListID;

update invoice set qbListID = null where tableType = 'P';

update invoice set amountApplied = totalAmount where tableType = 'I' and status = 'Paid';
