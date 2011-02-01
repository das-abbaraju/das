-- PICS-1797 - Use a better label for annual stats
update flag_criteria set label = REPLACE(label, right(label, 3), '') where label like '% ''%';

update accounts set industry = 'WoodProducts'
where name like 'Roseburg Forest Products%'
and type in ('Operator','Corporate');

-- MANTIS-1354
insert into email_template 
(accountID, templateName, subject, body, createdBy, creationDate, listType, allowsVelocity, html, recipient)
values (1100,"Insurance Policies rejected by PICS","Insurance Policies rejected by PICS","Hello <ContactName>,

The following insurance certificates have been approved or rejected by <MyName> at <MyCompanyName> for the following reasons:

#foreach ( $cao in $caoList )
#if ( $cao.status == \"Incomplete\" )
#foreach ( $caow in $cao.caoWorkflow )
#if ( $caow.status == \"Incomplete\" )
-- ${cao.audit.auditType.auditName} Insurance Certificates has been ${cao.status.button}ed for ${cao.operator.name}
#if ( $caow.notes.length() > 0 )because ${caow.notes}.

#end
#end
#end
#end
#end

Please correct these issues and re-upload your insurance certificate to your PICS account.
If you have any specific questions about any operator's insurance requirements, please review the Insurance Requirements documents in the Forms and Docs section. For questions, please contact ${permissions.name} at ${permissions.email}.

When you renew any policy, please make sure that you upload the new insurance certificate to keep the information up to date.

Have a great day,
PICS Customer Service",23157,now(),"Audit",1,0,"Admin");