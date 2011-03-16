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