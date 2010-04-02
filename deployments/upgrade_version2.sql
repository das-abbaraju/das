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

-- Changing account name velocity tag
update email_template set body = 'Hello <DisplayName>,
<MyName> has issued you a login for the ${user.account.name} account on PICS.
Please log in using the following link to set your password.
${confirmLink}
Have a great week,
<PICSSignature>' where id = 5;

-- Create new Widget for operators
insert into `widget`(`widgetID`,`caption`,`widgetType`,`synchronous`,`url`,`requiredPermission`,`chartType`)
values ( NULL,'PICS Contacts','Html','0','OperatorPicsContactsAjax.action',NULL,NULL);

insert into `widget_user`(`id`,`widgetID`,`userID`,`expanded`,`column`,`sortOrder`,`customConfig`)
values ( NULL,'25','616','1','1','3',NULL);

-- Update email template
update `email_template` set `id`='83',`accountID`='1100',`templateName`='Operator Request for Registration',
`subject`='Operator Request for Registration',`body`='${newContractor.name}
Hello ${newContractor.contact},
${requestedBy} of ${newContractor.requestedBy.name} has requested that you create an account with PICS to complete the auditing process. Please click on the link below to register an account with PICS.
${requestLink}
Thank you,
<PICSSignature>',`createdBy`='951',`creationDate`='2010-01-14 16:36:12',`updatedBy`='951',
`updateDate`='2010-01-14 16:36:12',`listType`='Contractor',`allowsVelocity`='1',`html`='0',
`recipient`=NULL where `id`='83';

-- insert into email subscription the PICSReleaseNotes for primary contacts for operators

insert into email_subscription 
select null,u.id,'PICSSystemNotifications','Event',NOw(),null,1,1,Now(),Now() from 
accounts a 
join users u on a.contactID = u.id
where a.type in ('Operator','Corporate')
and a.status = 'Active' and u.isActive = 'Yes'
AND u.email not like '%picsauditing.com'
AND length(u.email) > 0 and u.email like '%@%.%'
group by u.email;