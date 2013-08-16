start transaction;
-- alter table invoice_payment add column `paymentType` char(1);
-- update invoice_payment set paymentType = ""
alter table invoice_item add column `refundFor` int(11);
alter table invoice_item add column `transactionType` char(1);
update invoice_item set transactionType = "I";
commit;