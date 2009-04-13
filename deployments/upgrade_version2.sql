/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/

update contractor_audit_operator
set inherit = 1
where status in ('Awaiting','NotApplicable');

-- CAO Status updates
update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'Pending'
where cao.status not in('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Pending'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');

update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'Awaiting'
where cao.status not in ('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Submitted'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');

update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'Verified'
where cao.status not in ('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Active'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');

update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'Awaiting'
where cao.status not in ('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Resubmitted'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');

update contractor_audit_operator cao
  join contractor_audit ca
    on cao.auditID = ca.id
set cao.status = 'NotApplicable'
where cao.status not in ('Approved', 'Rejected', 'NotApplicable')
    and ca.auditStatus = 'Exempt'
    and ca.auditTypeID in(select
                            id
                          from audit_type
                          where classType = 'Policy');
                       
--
drop table temp_contractor_invoice;
create table temp_contractor_invoice (
  `accountID` int(10) unsigned NOT NULL,
  `creationDate` datetime default NULL,
  `paid` tinyint(4) default '0',
  `paidDate` datetime default NULL,
  `invoiceItemID` int(10) unsigned NOT NULL,
  `amount` decimal(9,2) default NULL,
  `feeClass` varchar(30) default NULL,
  KEY `accountID` (`accountID`,feeClass)
);

insert into temp_contractor_invoice
select i.accountID, i.creationDate, i.paid, i.paidDate, ii.id, ii.amount, f.feeClass
from invoice i
join invoice_item ii on i.id = ii.invoiceID
JOIN invoice_fee f on ii.feeID = f.id and feeClass = 'Activation';

insert into temp_contractor_invoice
select i.accountID, i.creationDate, i.paid, i.paidDate, ii.id, ii.amount, f.feeClass
from invoice i
join invoice_item ii on i.id = ii.invoiceID
JOIN invoice_fee f on ii.feeID = f.id and feeClass = 'Membership';

update invoice_item set paymentExpires = null;

update temp_contractor_invoice t, contractor_info c, invoice_item ii
set ii.paymentExpires = concat(year(t.creationDate) + 1, right(c.paymentExpires,6))
where t.accountID = c.id and t.invoiceItemID = ii.id and t.feeClass = 'Membership'
and t.amount in (99,225,399,499,599,699,799,999,1099,1197,1299,1399,1699,1999)
and datediff(concat(year(t.creationDate) + 1, right(c.paymentExpires,6)), t.creationDate) between 200 and 500;


update temp_contractor_invoice t, contractor_info c, invoice_item ii
set ii.paymentExpires = concat(year(t.creationDate) + 2, right(c.paymentExpires,6))
where t.accountID = c.id and t.invoiceItemID = ii.id and t.feeClass = 'Membership'
and t.amount in (99,225,399,499,599,699,799,999,1099,1197,1299,1399,1699,1999)
and datediff(concat(year(t.creationDate) + 2, right(c.paymentExpires,6)), t.creationDate) between 200 and 500;

update temp_contractor_invoice t, contractor_info c, invoice_item ii
set ii.paymentExpires = concat(year(t.creationDate) + 1, right(c.paymentExpires,6))
where t.accountID = c.id and t.invoiceItemID = ii.id and t.feeClass = 'Membership'
and ii.amount >= 0
and ii.paymentExpires is null;
