-- PICS-2137
update contractor_audit ca
set ca.closingAuditorID = 34065
where ca.id in (6359,6696,7264,7294,7303,7617,9816,10103,10124,10136,10306,10502,11267,12359,12454,12814,12898,12910,13097,13166,13437,32833,32878,33091,45326,45852,48190,48191,51477,51534,57789,57798,57800,57811,58378,59013,60915,60989,63453,63484,63571,63739,63839,63840,66420,66624,67010,67709,69058,73895,84795,85398,85484,86726,88985,90374,91223,91399,92073,92175,93015,93181,93461,93496,93497,93498,93550,93566,93570,93585,93685,93714,93824,93895,93906,94348,94502,94523,94646,94737,95114,95176,95813,96462,96975,97003,97067,97243,97408,97535,97574,97598,97601,97639,97944,98223,98276,98314,98771,98793,98795,98914,100742,100753,101250,101252,101254,101758,101917,102268,102969,103147,103314,103316,103923,103981,104421,105617,105685,105898,106009,106387,106426,106468,106551,107146,108093,108176,108180,108264,108451,108523,108863,109044,109076,109085,109524,111678,112556,113039,113163,113214,114009,114013,114087,114219,116321,116396,116510,116686,116725,116744,116848,116852,120922,127614,127795,128067,128162,128334,128739,129216,129594,129971,130393,130938,131292,131988,132132,132182,132286,132566,132833,132971,134381,134475,134582,134604,136297,136750,136945,137257,138231,138377,138401,138758,138803,139077,139180,139259,139327,140153,142527,142917,144575,145585,148431,148526,149279,149324,151472,151681,151807,152216,152262,152302,152497,152654,153181,153257,153293,154099,154451,154583,154689,155605,158276,159200,160533,160838,160944,161090,161202,163489,163557,164550,164649,164881,165261,165276,165527,165669,166163,166164,166190,166266,166294,166432,169493,170552,171046,171314,171320,172102,172164,172304,173239,173767,175196,175756,175792,176635,177164,178441,179190,207401,218049,222131,223480,228493,274222,299865,301004,301891,304401,305949,305998,306599,306945,306998,307122,307854,311111,311149,359774,359800,360370,364597,365364,365751,367140,368457,368483,379380,382757,383070,383994,384160,384718,9052,11465,11989,12140,43622,55126,61293,64533,81614,81676,81855,90100,95675,96974,96995,109872,113662,116629,135199,136679,138201,166165,169904,178677,183978,186333,203694,266789,279167);
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
-- PICS-2072
update `email_template`
set `subject`='<RequestedByOperator> has requested you join PICS',
`body`='<html>\r\n<head>\r\n<title><RequestedByOperator> has requested you join PICS</title> \r\n</head>\r\n<body>\r\n<table style=\"border-bottom: 1px solid rgb(123, 160, 205);\" width=\"900px\" cellpadding=\"5\" cellspacing=\"0\">\r\n<tr>\r\n<td style=\"border: 1px solid rgb(93, 155, 206); background-color: rgb(93, 155, 206); color: rgb(255, 255, 255); width: 900px; text-align: left; padding-left:1em\" colspan=\"2\"> \r\n<b><RequestedByOperator> has requested you join PICS</b>\r\n</td> \r\n</tr> \r\n<tr> \r\n<td style=\"border-left: 1px solid rgb(93, 155, 206); border-right: 1px solid rgb(93, 155, 206); width: 900px; text-align: left; vertical-align: top; padding-left: 1em; padding-right: 1em;\" colspan=\"2\">\r\n<p> \r\n<img height=\"90\" width=\"205\" alt=\"logo-pics\" src=\"http://www.picsauditing.com/wp-content/themes/PICS/style/images/logo-pics.jpg\"> \r\n<br/> \r\nHello <ContractorContactName>\r\n<br/>\r\n<br/> \r\n<RequestedByUser> at <RequestedByOperator> has requested for you to participate in their contractor/sub-contractor pre-qualification program through PICS (www.picsauditing.com).\r\n<br/>\r\n<br/> \r\nAttached, please find a letter from <RequestedByUser> with details on their prequalification program. Get started by registering your company in PICS Organizer by using the link below:\r\n<RegistrationLink>\r\n<br/>\r\n<br/> \r\nYour PICS Customer Service Representative is<br />\r\n<CSRName><br />\r\n<CSREmail>, <CSRPhone>\r\n<br/>\r\n<br/>\r\nIf you need assistance with the registration process or if you have questions regarding this email, please contact <CSRName> via email or at the phone number listed above.\r\n<br/>\r\n<br/>\r\nThank you and have a great day!<br/>\r\nPICS Customer Service Team<br/>\r\n1-800-506-7427 x 1  (Toll free in the USA)\r\n</p>\r\n</td>\r\n</tr>\r\n</table>\r\n</body>\r\n</html>',
`updatedBy`='23157',`updateDate`=NOW(),`html`='1'
where `id`='83';
-- PICS-2032
insert into useraccess 
(userID, accessType, lastUpdate, grantedByID)
values (959, 'ContractorSimulator', now(), 23157);