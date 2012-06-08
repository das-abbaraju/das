-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-5971 fix flag data override
update flag_data_override
set year='2011'
where id=5587;

-- PICS-4756 Remove orphaned questions
-- NOTE: This should be done before the config upgrade
-- Step 1 of 3
DELETE pd from pqfdata pd
join audit_question aq on aq.id=pd.questionID
Left join  audit_category ac on aq.categoryID=ac.id
where ac.id is null;

-- PICS-5416 update insurance policies to be day after answer of expiration date, bump contractors
-- update auto 
update contractor_audit ca 
join contractor_info ci on ci.id=ca.conID 
join pqfdata pd on pd.auditID = ca.id 
set ca.expiresDate=DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY), 
ci.needsRecalculation=ci.needsRecalculation + 1, 
ci.lastRecalculation=NULL 
where ca.auditTypeID = 15 
and ca.expiresDate > NOW() 
and pd.questionID=2111 
and STR_TO_DATE(pd.answer, '%m/%d/%Y') != '0000-00-00' 
AND Date_Format(ca.expiresDate, '%Y-%m-%d')>DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY); 

-- update general 
update contractor_audit ca 
join contractor_info ci on ci.id=ca.conID 
join pqfdata pd on pd.auditID = ca.id 
set ca.expiresDate=DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY), 
ci.needsRecalculation=ci.needsRecalculation + 1, 
ci.lastRecalculation=NULL 
where ca.auditTypeID= 13 
and ca.expiresDate > NOW() 
and pd.questionID=2082 
and STR_TO_DATE(pd.answer, '%m/%d/%Y') != '0000-00-00' 
AND Date_Format(ca.expiresDate, '%Y-%m-%d')>DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY); 

-- update excess 
update contractor_audit ca 
join contractor_info ci on ci.id=ca.conID 
join pqfdata pd on pd.auditID = ca.id 
set ca.expiresDate=DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY), 
ci.needsRecalculation=ci.needsRecalculation + 1, 
ci.lastRecalculation=NULL 
where ca.auditTypeID = 16 
and ca.expiresDate > NOW() 
and pd.questionID=2117 
and STR_TO_DATE(pd.answer, '%m/%d/%Y') != '0000-00-00' 
AND Date_Format(ca.expiresDate, '%Y-%m-%d')>DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY); 

-- update workers comp 
update contractor_audit ca 
join contractor_info ci on ci.id=ca.conID 
join pqfdata pd on pd.auditID = ca.id 
set ca.expiresDate=DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY), 
ci.needsRecalculation=ci.needsRecalculation + 1, 
ci.lastRecalculation=NULL 
where ca.auditTypeID = 14 
and ca.expiresDate > NOW() 
and pd.questionID=2105 
and STR_TO_DATE(pd.answer, '%m/%d/%Y') != '0000-00-00' 
AND Date_Format(ca.expiresDate, '%Y-%m-%d')>DATE_ADD(STR_TO_DATE(pd.answer, '%m/%d/%Y'), INTERVAL 1 DAY);

-- PICS-4647 Operator Flag Changes email template update for new totals
update email_template t set t.body = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
</head>
<body>
<div style="width: 750px; background-color: #002240; padding: 0 15px 0 15px; margin: 0; color: #6699CC; font-weight: normal; font-family: sans-serif; font-size: 11px; border-bottom: 3px solid #4686bf; border-left: 1px solid #002240; border-right: 1px solid #002240;">
&nbsp;</div>
<div style="width: 750px; padding: 15px; margin: 0; background-color: white; line-height: 18px; font-family: Helvetica, Arial, sans-serif; font-size: 14px; border-left: 1px solid #002240; border-right: 1px solid #002240;">
Below is a list of Client Sites who have had at least 10 total flag changes as well as 5% of their total flag colors change recently. Please review each one for errors. If you find a flag error, please work with your manager or IT to resolve the problems ASAP. If the flags are due to a valid change, then approve each one and consider reaching out to the client site\'s primary contact to discuss the impact of the changes.
#if(${changes.size()} > 0)
<h3 style="color: rgb(168, 77, 16)">Client Site Flag Changes</h3>
<div>
<strong>Total Flag Differences: </strong>${totalFlagChanges}<strong>  |  Total Client Sites Affected: </strong>${changes.size()}
</div>
<table style="border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;">
 <thead>
  <tr style="vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;">
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Client Site</td>
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Changes</td>
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Percent</td>
  </tr>
 </thead>
 <tbody>
  #foreach( $change in $changes )
  <tr style="margin:0px">
   <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;"><a href="https://www.picsorganizer.com/ReportFlagChanges.action?filter.operator=${change.get(\'id\')}">${change.get(\'operator\')}</a></td>
   <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;">${change.get(\'changes\')}</td>
   <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;">${change.get(\'percent\')}%</td>
  </tr>
  #end
 </tbody>
</table>	
#end
<br />
<TimeStampDisclaimer>
</div>
<div style="width: 750px; background-color: #002240; padding: 15px; margin: 0; color: #6699CC; font-weight: normal; font-family: sans-serif; font-size: 11px; border-top: 3px solid #4686bf; border-left: 1px solid #002240; border-right: 1px solid #002240;">
Copyright &copy; 2012 PICS
<a href="http://www.picsauditing.com/" style="font-size: 11px; color: #6699CC; padding: 2px;">http://www.picsauditing.com</a> |
<a href="http://www.picsauditing.com/app/privacy_policy.jsp" style="font-size: 11px; color: #6699CC; padding: 2px;">Privacy Policy</a>
</div>
</body>
</html>' where t.id = 55;
