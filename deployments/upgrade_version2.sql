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