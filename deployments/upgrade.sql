-- PICS-2187
update email_template et
set et.body = '<SubscriptionHeader>
There are <b>${data.size()} contractors</b> with #if($caoStatus.toString() == ''Complete'')
Verified
#else
${caoStatus}
#end Insurance Certificates.<br/><br/>
#if($caoStatus.toString() == ''Complete'')
You can approve or reject them on the <a href="http://www.picsorganizer.com/ReportInsuranceApproval.action?filter.caoStatus=Verified">Policies Awaiting Decision</a> report. 
<br/>
#else
You can contact the contractor(s) below using the email addresses provided.
<br/>
#end
<table style="border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;">
 <thead>
  <tr style="vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;">
#if(${user.account.corporate})
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Operator Name</td>
#end
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Contractor</td>
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Policy</td>
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Expires</td>
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Certificate</td>
#if($caoStatus == ''Pending'')
  <td style="border: 1px solid #e0e0e0; padding: 4px;">Contractor Contact</td>
  <td style="border: 1px solid #e0e0e0; padding: 4px;">Email</td>
#end
  </tr>
 </thead>
 <tbody>
  #foreach( $d in $data )
  <tr style="margin:0px">
#if(${user.account.corporate})
   <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;"><a href="http://www.picsorganizer.com/FacilitiesEdit.action?id=${d.get(''opID'')}">${d.get(''opName'')}</a></td>
#end
   <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;"><a href="http://www.picsorganizer.com/ContractorView.action?id=${d.get(''conID'')}">${d.get(''conName'')}</a></td>
   <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;"><a href="http://www.picsorganizer.com/Audit.action?auditID=${d.get(''auditID'')}">$i18nCache.getText(${d.get(''atype.name'')},$user.locale)</a></td>
   <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;">$pics_dateTool.format(''MM/dd/yy'', ${d.get(''expiresDate'')})</td>
   <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;"><a href="http://www.picsorganizer.com/CertificateUpload.action?id=${d.get(''conID'')}&certID=${d.get(''certID'')}&button=download">Certificate</a></td>
#if($caoStatus == ''Pending'')
  <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;">${d.get(''primaryContactName'')}</td>
  <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;">${d.get(''primaryContactEmail'')}</td>
#end
  </tr>
  #end
 </tbody>
</table>
<TimeStampDisclaimer>
<SubscriptionFooter>'
where et.id = 61;

update email_template et
set et.body = 'Attn: <ContactName>,

This is an automatic reminder that the following Policies for <CompanyName> have or are about to expire.

#foreach($outer in $contractor.audits)
#if($outer.auditType.classType.toString() == ''Policy'')
#foreach($inner in $contractor.audits)
#if($inner.id != $outer.id && $inner.auditType == $outer.auditType && $inner.getExpiringPolicies())
#foreach($operator in $outer.operators)
#if($operator.status.toString() == ''Pending'' && $operator.visible)
$inner.auditType.name for $operator.operator.name Expires On $pics_dateTool.format(''yyyy-MM-dd'',$inner.expiresDate)
#end
#end
#end
#end
#end
#end

Please upload a new insurance certificate using the insurance requirements of the above.

If we do not receive this certificate prior to the expiration you may not be permitted to enter the facility.

As always we appreciate your cooperation and are here to answer any questions you may have. Please reply to <CSRName> at <CSREmail> with any questions.

Thank you,
<CSRName>
PICS
P.O. Box 51387
Irvine CA 92619-1387
tel: <CSRPhone>
fax: <CSRFax>
<CSREmail>
http://www.picsauditing.com'
where et.id = 10;

update email_template et
set et.body = 'Hello <ContactName>,

${cao.audit.contractorAccount.name}''s ${cao.audit.auditType.name} Insurance Certificate has been ${cao.status} by ${cao.operator.name}
#if( $cao.notesLength ) for the following reasons:
${cao.notes}.
#end

#if( $cao.status == "Rejected" )
Please correct these issues and re-upload your insurance certificate to your PICS account.
If you have any specific questions about ${cao.operator.name}''s insurance requirements, please contact ${permissions.username} at ${permissions.email}
#else
When you renew this policy, please make sure that you upload the new insurance certificate to keep the information up to date.
#end

Have a great day,
PICS Customer Service'
where et.id = 33;

update email_template et
set et.body = 'Hello <ContactName>,

${cao.audit.contractorAccount.name}''s ${cao.audit.auditType.name} Insurance Certificate has been changed to ${cao.status} for ${cao.operator.name} by <MyName> at <MyCompanyName>#if( $note.length() > 0 ) for the following reason(s):
${note}.
#end

#if( $cao.status.incomplete )
Please correct these issues and re-upload your insurance certificate to your PICS account.
If you have any specific questions about ${cao.operator.name}''s insurance requirements, please review the Insurance Requirements document in the Forms and Docs section. For more information, please contact <MyName> at <MyEmail>.
#else
When you renew this policy, please make sure that you upload the new insurance certificate to keep the information up to date.
#end

Have a great day,
PICS Customer Service'
where et.id = 52;

update email_template et
set et.body = 'Hello <ContactName>,

The following insurance certificates have been approved or rejected by <MyName> at <MyCompanyName> for the following reasons:

#foreach ( $cao in $caoList )
#if ( $cao.status == "Incomplete" )
#foreach ( $caow in $cao.caoWorkflow )
#if ( $caow.status == "Incomplete" )
-- ${cao.audit.auditType.name} Insurance Certificates has been ${cao.status.button}ed for ${cao.operator.name}
#if ( $caow.notes.length() > 0 )because ${caow.notes}.

#end
#end
#end
#end
#end

Please correct these issues and re-upload your insurance certificate to your PICS account.
If you have any specific questions about any operator''s insurance requirements, please review the Insurance Requirements documents in the Forms and Docs section. For questions, please contact ${permissions.name} at ${permissions.email}.

When you renew any policy, please make sure that you upload the new insurance certificate to keep the information up to date.

Have a great day,
PICS Customer Service'
where et.id = 132;
--

update `email_template`
set `body`='<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
</head>
<body>
<div style="width: 750px; background-color: #002240; padding: 0 15px 0 15px; margin: 0; color: #6699CC; font-weight: normal; font-family: sans-serif; font-size: 11px; border-bottom: 3px solid #4686bf; border-left: 1px solid #002240; border-right: 1px solid #002240;">
&nbsp;</div>
<div style="width: 750px; padding: 15px; margin: 0; background-color: white; line-height: 18px; font-family: Helvetica, Arial, sans-serif; font-size: 14px; border-left: 1px solid #002240; border-right: 1px solid #002240;">
Below is a list of Operators who have had at least 10 total flag changes as well as 5% of their total flag colors change recently. Please review each one for errors. If you find a flag error, please work with your manager or IT to resolve the problems ASAP. If the flags are due to a valid change, then approve each one and consider reaching out to the operator''s primary contact to discuss the impact of the changes.
#if(${changes.size()} > 0)
<h3 style="color: rgb(168, 77, 16)">Operator Flag Changes</h3>
<table style="border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;">
 <thead>
  <tr style="vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;">
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Operator</td>
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Changes</td>
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Percent</td>
  </tr>
 </thead>
 <tbody>
  #foreach( $change in $changes )
  <tr style="margin:0px">
   <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;"><a href="https://www.picsorganizer.com/ReportFlagChanges.action?filter.operator=${change.get(''id'')}">${change.get(''operator'')}</a></td>
   <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;">${change.get(''changes'')}</td>
   <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;">${change.get(''percent'')}%</td>
  </tr>
  #end
 </tbody>
</table>	
#end
<br />
<TimeStampDisclaimer>
</div>
<div style="width: 750px; background-color: #002240; padding: 15px; margin: 0; color: #6699CC; font-weight: normal; font-family: sans-serif; font-size: 11px; border-top: 3px solid #4686bf; border-left: 1px solid #002240; border-right: 1px solid #002240;">
Copyright &copy; 2009 PICS
<a href="http://www.picsauditing.com/" style="font-size: 11px; color: #6699CC; padding: 2px;">http://www.picsauditing.com</a> |
<a href="http://www.picsauditing.com/app/privacy_policy.jsp" style="font-size: 11px; color: #6699CC; padding: 2px;">Privacy Policy</a>
</div>
</body>
</html>'
where `id`=55;

update `email_template`
set `body`='<SubscriptionHeader>\nThere are <b>${data.size()} contractors</b> with #if($caoStatus.toString() == \'Complete\')\r\nVerified\r\n#else\r\n${caoStatus}\r\n#end Insurance Certificates.<br/><br/>\n#if($caoStatus.toString() == \'Complete\')\nYou can approve or reject them on the <a href=\"http://www.picsorganizer.com/ReportInsuranceApproval.action?filter.caoStatus=Verified\">Policies Awaiting Decision</a> report. \n<br/>\n#else\nYou can contact the contractor(s) below using the email addresses provided.\n<br/>\n#end\n<table style=\"border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;\">\n <thead>\n  <tr style=\"vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;\">\n#if(${user.account.corporate})\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Operator Name</td>\n#end\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Contractor</td>\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Policy</td>\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Expires</td>\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Certificate</td>\n#if($caoStatus == \'Pending\')\n  <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Contractor Contact</td>\n  <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Email</td>\n#end\n  </tr>\n </thead>\n <tbody>\n  #foreach( $d in $data )\n  <tr style=\"margin:0px\">\n#if(${user.account.corporate})\n   <td style=\"border: 1px solid #A84D10; padding: 4px; font-size: 13px;\"><a href=\"http://www.picsorganizer.com/FacilitiesEdit.action?id=${d.get(\'opID\')}\">${d.get(\'opName\')}</a></td>\n#end\n   <td style=\"border: 1px solid #A84D10; padding: 4px; font-size: 13px;\"><a href=\"http://www.picsorganizer.com/ContractorView.action?id=${d.get(\'conID\')}\">${d.get(\'conName\')}</a></td>\n   <td style=\"border: 1px solid #A84D10; padding: 4px; font-size: 13px;\"><a href=\"http://www.picsorganizer.com/Audit.action?auditID=${d.get(\'auditID\')}\">${d.get(\'auditName\')}</a></td>\n   <td style=\"border: 1px solid #A84D10; padding: 4px; font-size: 13px;\">$pics_dateTool.format(\'MM/dd/yy\', ${d.get(\'expiresDate\')})</td>\n   <td style=\"border: 1px solid #A84D10; padding: 4px; font-size: 13px;\"><a href=\"http://www.picsorganizer.com/CertificateUpload.action?id=${d.get(\'conID\')}&certID=${d.get(\'certID\')}&button=download\">Certificate</a></td>\n#if($caoStatus == \'Pending\')\n  <td style=\"border: 1px solid #A84D10; padding: 4px; font-size: 13px;\">${d.get(\'primaryContactName\')}</td>\n  <td style=\"border: 1px solid #A84D10; padding: 4px; font-size: 13px;\">${d.get(\'primaryContactEmail\')}</td>\n#end\n  </tr>\n  #end\n </tbody>\n</table>\n<TimeStampDisclaimer>\n<SubscriptionFooter>',`updatedBy`='23157',`updateDate`=NOW(), 
where `id`='61';
-- PICS-2089
update accounts a
set onsiteServices = true, offsiteServices = true, materialSupplier = true
where a.type = 'Operator'
and a.status = 'Active';
-- PICS-2032
insert into useraccess 
(userID, accessType, lastUpdate, grantedByID)
values (959, 'ContractorSimulator', now(), 23157);
-- PICS-462
insert into 
user_assignment(state,country,userID,assignmentType,auditTypeID,createdBy,creationDate) 
values 
('FL','US',11503,'Auditor',2,23157,NOW()),('FL','US',11503,'Auditor',3,23157,NOW()),
('GA','US',11503,'Auditor',2,23157,NOW()),('GA','US',11503,'Auditor',3,23157,NOW()),
('IN','US',11503,'Auditor',2,23157,NOW()),('IN','US',11503,'Auditor',3,23157,NOW()),
('NH','US',11503,'Auditor',2,23157,NOW()),('NH','US',11503,'Auditor',3,23157,NOW()),
('SC','US',11503,'Auditor',2,23157,NOW()),('SC','US',11503,'Auditor',3,23157,NOW()),
('TX','US',11503,'Auditor',2,23157,NOW()),('TX','US',11503,'Auditor',3,23157,NOW()),
('CA','US',935,'Auditor',2,23157,NOW()),('CA','US',935,'Auditor',3,23157,NOW()),
('IA','US',935,'Auditor',2,23157,NOW()),('IA','US',935,'Auditor',3,23157,NOW()),
('KS','US',935,'Auditor',2,23157,NOW()),('KS','US',935,'Auditor',3,23157,NOW()),
('MN','US',935,'Auditor',2,23157,NOW()),('MN','US',935,'Auditor',3,23157,NOW()),
('MS','US',935,'Auditor',2,23157,NOW()),('MS','US',935,'Auditor',3,23157,NOW()),
('MO','US',935,'Auditor',2,23157,NOW()),('MO','US',935,'Auditor',3,23157,NOW()),
('NE','US',935,'Auditor',2,23157,NOW()),('NE','US',935,'Auditor',3,23157,NOW()),
('ND','US',935,'Auditor',2,23157,NOW()),('ND','US',935,'Auditor',3,23157,NOW()),
('RI','US',935,'Auditor',2,23157,NOW()),('RI','US',935,'Auditor',3,23157,NOW()),
('SD','US',935,'Auditor',2,23157,NOW()),('SD','US',935,'Auditor',3,23157,NOW()),
('TN','US',935,'Auditor',2,23157,NOW()),('TN','US',935,'Auditor',3,23157,NOW()),
('WA','US',935,'Auditor',2,23157,NOW()),('WA','US',935,'Auditor',3,23157,NOW()),
('WI','US',935,'Auditor',2,23157,NOW()),('WI','US',935,'Auditor',3,23157,NOW()),
('WY','US',935,'Auditor',2,23157,NOW()),('WY','US',935,'Auditor',3,23157,NOW()),
('AZ','US',34067,'Auditor',2,23157,NOW()),('AZ','US',34067,'Auditor',3,23157,NOW()),
('AR','US',34067,'Auditor',2,23157,NOW()),('AR','US',34067,'Auditor',3,23157,NOW()),
('CO','US',34067,'Auditor',2,23157,NOW()),('CO','US',34067,'Auditor',3,23157,NOW()),
('ID','US',34067,'Auditor',2,23157,NOW()),('ID','US',34067,'Auditor',3,23157,NOW()),
('KY','US',34067,'Auditor',2,23157,NOW()),('KY','US',34067,'Auditor',3,23157,NOW()),
('LA','US',34067,'Auditor',2,23157,NOW()),('LA','US',34067,'Auditor',3,23157,NOW()),
('MI','US',34067,'Auditor',2,23157,NOW()),('MI','US',34067,'Auditor',3,23157,NOW()),
('NM','US',34067,'Auditor',2,23157,NOW()),('NM','US',34067,'Auditor',3,23157,NOW()),
('OK','US',34067,'Auditor',2,23157,NOW()),('OK','US',34067,'Auditor',3,23157,NOW()),
('UT','US',34067,'Auditor',2,23157,NOW()),('UT','US',34067,'Auditor',3,23157,NOW()),
('VA','US',34067,'Auditor',2,23157,NOW()),('VA','US',34067,'Auditor',3,23157,NOW()),
('WV','US',34067,'Auditor',2,23157,NOW()),('WV','US',34067,'Auditor',3,23157,NOW()),
('AK','US',1029,'Auditor',2,23157,NOW()),('AK','US',1029,'Auditor',3,23157,NOW()),
('AS','US',1029,'Auditor',2,23157,NOW()),('AS','US',1029,'Auditor',3,23157,NOW()),
('CT','US',1029,'Auditor',2,23157,NOW()),('CT','US',1029,'Auditor',3,23157,NOW()),
('DC','US',1029,'Auditor',2,23157,NOW()),('DC','US',1029,'Auditor',3,23157,NOW()),
('HI','US',1029,'Auditor',2,23157,NOW()),('HI','US',1029,'Auditor',3,23157,NOW()),
('MT','US',1029,'Auditor',2,23157,NOW()),('MT','US',1029,'Auditor',3,23157,NOW()),
('NV','US',1029,'Auditor',2,23157,NOW()),('NV','US',1029,'Auditor',3,23157,NOW()),
('NC','US',1029,'Auditor',2,23157,NOW()),('NC','US',1029,'Auditor',3,23157,NOW()),
('MP','US',1029,'Auditor',2,23157,NOW()),('MP','US',1029,'Auditor',3,23157,NOW()),
('OH','US',1029,'Auditor',2,23157,NOW()),('OH','US',1029,'Auditor',3,23157,NOW()),
('OR','US',1029,'Auditor',2,23157,NOW()),('OR','US',1029,'Auditor',3,23157,NOW()),
('UM','US',1029,'Auditor',2,23157,NOW()),('UM','US',1029,'Auditor',3,23157,NOW()),
('AL','US',34065,'Auditor',2,23157,NOW()),('AL','US',34065,'Auditor',3,23157,NOW()),
('DE','US',34065,'Auditor',2,23157,NOW()),('DE','US',34065,'Auditor',3,23157,NOW()),
('GU','US',34065,'Auditor',2,23157,NOW()),('GU','US',34065,'Auditor',3,23157,NOW()),
('IL','US',34065,'Auditor',2,23157,NOW()),('IL','US',34065,'Auditor',3,23157,NOW()),
('ME','US',34065,'Auditor',2,23157,NOW()),('ME','US',34065,'Auditor',3,23157,NOW()),
('MD','US',34065,'Auditor',2,23157,NOW()),('MD','US',34065,'Auditor',3,23157,NOW()),
('MA','US',34065,'Auditor',2,23157,NOW()),('MA','US',34065,'Auditor',3,23157,NOW()),
('NJ','US',34065,'Auditor',2,23157,NOW()),('NJ','US',34065,'Auditor',3,23157,NOW()),
('NY','US',34065,'Auditor',2,23157,NOW()),('NY','US',34065,'Auditor',3,23157,NOW()),
('PA','US',34065,'Auditor',2,23157,NOW()),('PA','US',34065,'Auditor',3,23157,NOW()),
('PR','US',34065,'Auditor',2,23157,NOW()),('PR','US',34065,'Auditor',3,23157,NOW()),
('VT','US',34065,'Auditor',2,23157,NOW()),('VT','US',34065,'Auditor',3,23157,NOW()),
('VI','US',34065,'Auditor',2,23157,NOW()),('VI','US',34065,'Auditor',3,23157,NOW());

insert into user_assignment (assignmentType, createdBy, creationDate, country, state, postal_start, postal_end, userID, auditTypeID)
values
('Auditor', 23157, NOW(), 'US','CA',90001,90084, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90001,90084, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90001,90084, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90086,90091, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90086,90091, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90086,90091, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90093,90096, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90093,90096, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90093,90096, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90099,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90099,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90099,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90101,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90101,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90101,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90103,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90103,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90103,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90189,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90189,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90189,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90201,90202, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90201,90202, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90201,90202, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90209,90213, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90209,90213, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90209,90213, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90220,90224, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90220,90224, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90220,90224, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90230,90233, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90230,90233, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90230,90233, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90239,90242, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90239,90242, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90239,90242, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90245,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90245,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90245,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90247,90251, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90247,90251, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90247,90251, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90254,90255, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90254,90255, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90254,90255, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90260,90263, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90260,90263, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90260,90263, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90265,90267, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90265,90267, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90265,90267, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90270,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90270,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90270,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90272,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90272,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90272,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90274,90275, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90274,90275, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90274,90275, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90277,90278, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90277,90278, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90277,90278, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90280,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90280,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90280,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90290,90296, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90290,90296, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90290,90296, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90301,90312, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90301,90312, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90301,90312, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90401,90411, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90401,90411, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90401,90411, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90501,90510, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90501,90510, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90501,90510, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90601,90610, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90601,90610, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90601,90610, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90620,90624, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90620,90624, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90620,90624, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90630,90633, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90630,90633, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90630,90633, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90637,90640, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90637,90640, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90637,90640, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90650,90652, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90650,90652, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90650,90652, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90660,90662, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90660,90662, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90660,90662, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90670,90671, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90670,90671, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90670,90671, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90680,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90680,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90680,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90701,90704, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90701,90704, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90701,90704, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90706,90707, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90706,90707, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90706,90707, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90710,90717, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90710,90717, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90710,90717, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90720,90721, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90720,90721, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90720,90721, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90723,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90723,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90723,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90731,90734, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90731,90734, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90731,90734, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90740,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90740,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90740,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90742,90749, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90742,90749, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90742,90749, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90755,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90755,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90755,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90801,90810, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90801,90810, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90801,90810, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90813,90815, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90813,90815, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90813,90815, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90822,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90822,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90822,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90831,90835, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90831,90835, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90831,90835, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90840,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90840,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90840,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90842,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90842,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90842,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90844,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90844,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90844,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90846,90848, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90846,90848, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90846,90848, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90853,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90853,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90853,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90895,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90895,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90895,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',90899,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',90899,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',90899,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91001,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91001,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91001,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91003,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91003,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91003,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91006,91012, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91006,91012, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91006,91012, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91016,91017, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91016,91017, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91016,91017, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91020,91021, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91020,91021, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91020,91021, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91023,91025, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91023,91025, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91023,91025, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91030,91031, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91030,91031, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91030,91031, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91040,91043, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91040,91043, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91040,91043, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91046,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91046,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91046,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91066,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91066,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91066,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91077,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91077,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91077,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91101,91110, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91101,91110, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91101,91110, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91114,91118, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91114,91118, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91114,91118, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91121,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91121,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91121,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91123,91126, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91123,91126, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91123,91126, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91129,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91129,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91129,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91182,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91182,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91182,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91184,91185, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91184,91185, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91184,91185, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91188,91189, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91188,91189, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91188,91189, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91199,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91199,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91199,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91201,91210, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91201,91210, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91201,91210, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91214,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91214,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91214,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91221,91222, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91221,91222, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91221,91222, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91224,91226, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91224,91226, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91224,91226, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91302,91309, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91302,91309, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91302,91309, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91311,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91311,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91311,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91313,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91313,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91313,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91316,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91316,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91316,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91324,91331, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91324,91331, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91324,91331, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91333,91335, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91333,91335, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91333,91335, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91337,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91337,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91337,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91340,91346, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91340,91346, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91340,91346, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91352,91353, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91352,91353, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91352,91353, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91356,91357, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91356,91357, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91356,91357, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91364,91365, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91364,91365, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91364,91365, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91367,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91367,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91367,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91371,91372, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91371,91372, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91371,91372, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91392,91396, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91392,91396, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91392,91396, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91401,91413, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91401,91413, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91401,91413, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91416,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91416,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91416,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91423,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91423,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91423,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91426,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91426,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91426,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91436,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91436,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91436,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91470,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91470,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91470,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91482,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91482,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91482,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91495,91496, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91495,91496, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91495,91496, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91499,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91499,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91499,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91501,91508, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91501,91508, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91501,91508, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91510,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91510,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91510,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91521,91523, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91521,91523, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91521,91523, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91526,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91526,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91526,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91601,91612, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91601,91612, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91601,91612, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91614,91618, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91614,91618, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91614,91618, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91701,91702, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91701,91702, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91701,91702, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91706,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91706,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91706,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91708,91711, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91708,91711, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91708,91711, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91714,91716, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91714,91716, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91714,91716, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91722,91724, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91722,91724, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91722,91724, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91729,91735, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91729,91735, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91729,91735, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91737,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91737,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91737,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91739,91741, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91739,91741, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91739,91741, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91743,91750, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91743,91750, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91743,91750, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91752,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91752,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91752,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91754,91756, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91754,91756, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91754,91756, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91758,91759, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91758,91759, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91758,91759, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91761,91773, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91761,91773, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91761,91773, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91775,91776, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91775,91776, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91775,91776, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91778,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91778,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91778,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91780,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91780,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91780,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91784,91786, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91784,91786, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91784,91786, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91788,91793, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91788,91793, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91788,91793, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91795,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91795,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91795,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91797,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91797,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91797,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91801,91804, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91801,91804, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91801,91804, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91896,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91896,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91896,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',91899,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',91899,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',91899,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92003,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92003,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92003,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92007,92011, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92007,92011, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92007,92011, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92013,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92013,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92013,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92018,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92018,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92018,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92023,92030, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92023,92030, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92023,92030, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92033,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92033,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92033,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92046,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92046,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92046,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92049,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92049,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92049,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92051,92052, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92051,92052, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92051,92052, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92054,92061, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92054,92061, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92054,92061, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92067,92069, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92067,92069, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92067,92069, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92075,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92075,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92075,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92078,92079, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92078,92079, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92078,92079, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92081,92085, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92081,92085, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92081,92085, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92088,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92088,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92088,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92091,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92091,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92091,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92096,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92096,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92096,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92220,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92220,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92220,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92223,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92223,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92223,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92313,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92313,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92313,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92316,92318, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92316,92318, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92316,92318, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92320,92322, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92320,92322, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92320,92322, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92324,92326, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92324,92326, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92324,92326, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92329,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92329,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92329,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92331,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92331,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92331,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92334,92337, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92334,92337, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92334,92337, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92339,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92339,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92339,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92341,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92341,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92341,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92344,92346, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92344,92346, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92344,92346, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92350,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92350,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92350,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92352,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92352,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92352,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92354,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92354,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92354,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92357,92359, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92357,92359, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92357,92359, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92369,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92369,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92369,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92371,92378, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92371,92378, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92371,92378, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92382,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92382,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92382,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92385,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92385,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92385,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92391,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92391,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92391,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92397,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92397,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92397,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92399,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92399,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92399,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92401,92408, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92401,92408, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92401,92408, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92410,92415, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92410,92415, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92410,92415, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92418,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92418,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92418,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92423,92424, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92423,92424, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92423,92424, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92427,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92427,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92427,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92501,92509, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92501,92509, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92501,92509, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92513,92519, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92513,92519, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92513,92519, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92521,92522, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92521,92522, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92521,92522, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92530,92532, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92530,92532, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92530,92532, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92543,92546, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92543,92546, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92543,92546, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92548,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92548,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92548,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92551,92557, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92551,92557, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92551,92557, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92562,92564, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92562,92564, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92562,92564, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92567,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92567,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92567,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92570,92572, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92570,92572, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92570,92572, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92581,92587, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92581,92587, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92581,92587, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92589,92593, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92589,92593, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92589,92593, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92595,92596, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92595,92596, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92595,92596, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92599,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92599,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92599,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92602,92607, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92602,92607, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92602,92607, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92609,92610, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92609,92610, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92609,92610, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92612,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92612,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92612,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92614,92620, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92614,92620, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92614,92620, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92623,92630, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92623,92630, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92623,92630, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92637,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92637,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92637,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92646,92663, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92646,92663, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92646,92663, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92672,92679, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92672,92679, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92672,92679, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92683,92685, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92683,92685, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92683,92685, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92688,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92688,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92688,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92690,92694, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92690,92694, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92690,92694, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92697,92698, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92697,92698, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92697,92698, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92701,92708, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92701,92708, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92701,92708, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92711,92712, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92711,92712, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92711,92712, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92725,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92725,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92725,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92728,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92728,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92728,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92735,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92735,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92735,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92780,92782, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92780,92782, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92780,92782, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92799,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92799,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92799,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92801,92809, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92801,92809, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92801,92809, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92811,92812, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92811,92812, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92811,92812, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92814,92817, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92814,92817, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92814,92817, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92821,92823, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92821,92823, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92821,92823, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92825,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92825,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92825,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92831,92838, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92831,92838, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92831,92838, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92840,92846, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92840,92846, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92840,92846, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92850,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92850,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92850,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92856,92857, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92856,92857, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92856,92857, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92859,92871, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92859,92871, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92859,92871, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92877,92883, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92877,92883, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92877,92883, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92885,92887, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92885,92887, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92885,92887, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',92899,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',92899,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',92899,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',93510,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',93510,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',93510,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',93543,93544, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',93543,93544, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',93543,93544, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',93553,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',93553,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',93553,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','CA',93563,NULL, 935, 3), ('Auditor', 23157, NOW(), 'US','CA',93563,NULL, 34067, 3), ('Auditor', 23157, NOW(), 'US','CA',93563,NULL, 1029, 3),
('Auditor', 23157, NOW(), 'US','TX',77001,77096, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77001,77096, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77098,77099, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77098,77099, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77201,77210, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77201,77210, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77212,77213, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77212,77213, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77215,77231, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77215,77231, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77233,77238, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77233,77238, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77240,77245, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77240,77245, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77248,77249, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77248,77249, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77251,77259, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77251,77259, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77261,77263, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77261,77263, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77265,77275, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77265,77275, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77277,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77277,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77279,77280, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77279,77280, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77282,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77282,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77284,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77284,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77287,77293, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77287,77293, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77297,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77297,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77299,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77299,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77301,77306, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77301,77306, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77315,77316, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77315,77316, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77318,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77318,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77325,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77325,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77327,77328, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77327,77328, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77331,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77331,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77333,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77333,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77336,77339, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77336,77339, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77345,77347, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77345,77347, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77353,77358, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77353,77358, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77362,77363, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77362,77363, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77365,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77365,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77368,77369, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77368,77369, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77371,77373, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77371,77373, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77375,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77375,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77377,77389, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77377,77389, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77391,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77391,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77393,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77393,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77396,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77396,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77401,77402, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77401,77402, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77406,77407, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77406,77407, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77410,77411, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77410,77411, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77413,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77413,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77417,77418, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77417,77418, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77420,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77420,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77422,77423, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77422,77423, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77426,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77426,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77429,77431, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77429,77431, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77433,77436, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77433,77436, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77441,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77441,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77443,77454, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77443,77454, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77459,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77459,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77461,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77461,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77463,77464, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77463,77464, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77466,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77466,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77468,77469, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77468,77469, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77471,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77471,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77473,77474, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77473,77474, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77476,77481, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77476,77481, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77484,77489, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77484,77489, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77491,77494, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77491,77494, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77496,77498, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77496,77498, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77501,77508, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77501,77508, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77510,77512, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77510,77512, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77514,77523, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77514,77523, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77530,77536, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77530,77536, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77538,77539, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77538,77539, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77541,77542, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77541,77542, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77545,77547, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77545,77547, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77549,77555, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77549,77555, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77560,77566, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77560,77566, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77568,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77568,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77571,77575, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77571,77575, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77577,77578, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77577,77578, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77580,77584, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77580,77584, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77586,77588, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77586,77588, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77590,77592, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77590,77592, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77597,77598, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77597,77598, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77830,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77830,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77868,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77868,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77873,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77873,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',77880,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',77880,NULL, 11503, 3),
('Auditor', 23157, NOW(), 'US','TX',78933,NULL, 34065, 3), ('Auditor', 23157, NOW(), 'US','TX',78933,NULL, 11503, 3);