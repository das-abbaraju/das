--liquibase formatted sql

--changeset pschlesinger:13
update invoice
join accounts on invoice.accountID = accounts.id
set invoice.sapSync = 1, invoice.sapLastSync = null
where 1
and invoice.tableType = 'I'
and accounts.status not in ('Demo','Declined','Pending')
and invoice.status != 'Void'
and invoice.currency in ('USD','CAD')
and invoice.creationDate >= '2012-07-01'
and invoice.creationDate < '2012-08-01'
;

UPDATE invoice JOIN invoice_payment ON	invoice_payment.paymentID	= invoice.id JOIN invoice 	i
ON	invoice_payment.invoiceID	= i.id SET invoice.sapSync	= 1 WHERE	1=1 AND	i.sapLastSync	IS NOT NULL AND	i.tableType	= "I" AND	invoice.tableType	= "P" AND	invoice.status	<> "Void";
