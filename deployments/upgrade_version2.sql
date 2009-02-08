insert into users 
      (username, password, isGroup, email, name, isActive, lastLogin, accountID, phone, fax, passwordChanged, createdBy, updatedBy, creationDate, updateDate)
select username, password, 'No', email, contact, 'Yes', lastLogin, id, phone, fax, passwordChange, createdBy, updatedBy, creationDate, updateDate
from accounts where type = 'Contractor';

update audit_type set classType = 'IM' where id = 17;

update note set noteCategory = 'OperatorChanges'
where noteCategory = 'ContractorAddition';


update contractor_info set needsRecalculation = 0;

update contractor_info set membershipLevelID = 9 where billingAmount = 99;
update contractor_info set membershipLevelID = 3 where billingAmount = 399;
update contractor_info set membershipLevelID = 4 where billingAmount = 699;
update contractor_info set membershipLevelID = 5 where billingAmount = 999;
update contractor_info set membershipLevelID = 6 where billingAmount = 1299;
update contractor_info set membershipLevelID = 7 where billingAmount = 1699;
update contractor_info set membershipLevelID = 8 where billingAmount = 1999;

update contractor_info set newMembershipLevelID = 9 where newBillingAmount = 99;
update contractor_info set newMembershipLevelID = 3 where newBillingAmount = 399;
update contractor_info set newMembershipLevelID = 4 where newBillingAmount = 699;
update contractor_info set newMembershipLevelID = 5 where newBillingAmount = 999;
update contractor_info set newMembershipLevelID = 6 where newBillingAmount = 1299;
update contractor_info set newMembershipLevelID = 7 where newBillingAmount = 1699;
update contractor_info set newMembershipLevelID = 8 where newBillingAmount = 1999;

update contractor_info set renew = 0 
	where id in (select id from accounts where active = 'N');
update contractor_info set membershipLevelID = newMembershipLevelID 
	where membershipLevelID is null and mustPay = 'Yes' and billingAmount > 0;
update contractor_info set renew = 1
	where accountDate is null OR accountDate = '0000-00-00';


/******* INVOICES *************/
-- Add all paid invoices
insert into invoice (accountID, creationDate, createdBy, totalAmount, paid, paidDate)
select c.id, lastPayment, 1, lastPaymentAmount, 1, lastPayment
from contractor_info c
where lastPaymentAmount > 0 and lastPayment > '0000-00-00';

-- Add all overdue invoices
insert into invoice (accountID, creationDate, createdBy, totalAmount, paid)
select c.id, lastInvoiceDate, 1, billingAmount, 0
from contractor_info c
where billingAmount > 0 and lastInvoiceDate > '0000-00-00'
and lastInvoiceDate > lastPayment;

insert into invoice_item (invoiceID, creationDate, createdBy, amount)
select id, creationDate, createdBy, totalAmount from invoice;

update invoice_item set feeID = 9 where amount = 99;
update invoice_item set feeID = 3 where amount = 399;
update invoice_item set feeID = 4 where amount = 699;
update invoice_item set feeID = 5 where amount = 999;
update invoice_item set feeID = 6 where amount = 1299;
update invoice_item set feeID = 7 where amount = 1699;
update invoice_item set feeID = 8 where amount = 1999;

update contractor_info
set paymentExpires = adddate(lastPayment, interval 1 year)
where (paymentExpires = '0000-00-00' or paymentExpires is null) and lastPayment > '0000-00-00';

update contractor_info, accounts
set paymentExpires = creationDate
where (paymentExpires = '0000-00-00' or paymentExpires is null)
and contractor_info.id = accounts.id;

select * from users
where id = 959

/**** Notes Conversion *****/
insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select 
	id as accountID, 
	accountDate as creationDate,
	959 as createdBy,
	'Contractor Notes Pre-2009' as summary,
	'General' as noteCategory,
	2 as priorityMed,
	1 as viewableByEveryone,
	notes
from contractor_info
where notes > '';

insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select 
	id, 
	accountDate, 
	959, 
	'PICS-only Notes Pre-2009',
	'Billing', 
	2 as priorityMed,
	1 as viewableByPics,
	adminNotes
from contractor_info
where adminNotes > '';

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy)
select 
	conID as accountID,
	timeStamp as creationDate, 
	case ISNULL(userID) when 1 then 959 else userID end,
	deletedDate,
	deletedUserID,
	note as summary,
	'General' as noteCategory,
	case isDeleted when 1 then 0 else 2 end as status,
	2 as priorityMed,
	opID as viewableBy
from notes
where length(note) <= 100;

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy, body)
select conID, timeStamp, case ISNULL(userID) when 1 then 959 else userID end, deletedDate, deletedUserID, substring(note, 1, 100), 'General', case isDeleted when 1 then 0 else 2 end, 2, opID, substring(note, 100)
from notes
where length(note) > 100;

/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;


update notes set userID from whois and opID
update notes set deletedUserID from whoDeleted and opID

*/

