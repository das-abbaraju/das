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

/* remove the question with id 0*/
delete from pqfquestions where id = 0;

update flag_criteria set questionid = null where questionid = 0;

update `email_template` 
set `id`='83',`accountID`='1100',`templateName`='Operator Request for Registration',
`subject`='${newContractor.requestedBy.name} has requested you join PICS',
`body`='${newContractor.name}\r\n\r\nHello, ${newContractor.contact}\r\n\r\n${requestedBy} of ${newContractor.requestedBy.name} has requested that you create an account with PICS to complete the prequalification process. Please click on the link below to register an account with PICS.\r\n${requestLink}\r\n\r\nThank you,\r\n<PICSSignature>',
`createdBy`='951',`creationDate`='2010-01-14 16:36:12',`updatedBy`='951',`updateDate`='2010-01-14 16:36:12',
`listType`='Contractor',`allowsVelocity`='1',`html`='0',`recipient`=NULL where `id`='83';

insert into `useraccess` 
(accessID, userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
values (NULL, 959, 'RequestNewContractor', 1, 1, 0, 1, now(), 1);