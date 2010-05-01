/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
**/

/** Update the requiresOQ for all contractors
 * we don't want to run this yet 
update accounts set requiresOQ = 1
where id in (select distinct conid from contractor_audit ca
join pqfdata pd on ca.id = pd.auditid
where pd.questionid = 894
and pd.answer = 'Yes');
**/

insert  into `email_template`(`id`,`accountID`,`templateName`,`subject`,`body`,`createdBy`,`creationDate`,`updatedBy`,`updateDate`,`listType`,`allowsVelocity`,`html`,`recipient`) values (106,1100,'Credit Card Transaction Failure','Credit Card Transaction Failed','Due to a connection malfunction between our payment gateway and PICS, the payment information has been lost for ${permissions.name} (${permissions.userid}) on ${contractor.name} (contractor.id) for invoice:\r\nhttp://www.picsorganizer.com/InvoiceDetail.action?invoice.id=${invoice.id}\r\n\r\nWe cannot determine whether payment was processed successfully. Please check BrainTree to determine whether payment was processed successfully and notify ${permissions.name} at ${permissions.email} or ${permissions.phone}, or one of the following billing contact(s):\r\n#foreach( $user in $billingusers )\r\n${user.getUsername()}: ${user.email} or ${user.phone}\r\n#end\r\n\r\n-PICS IT',941,'2010-03-29 09:55:46',941,'2010-03-29 09:55:46','Contractor',1,0,NULL);