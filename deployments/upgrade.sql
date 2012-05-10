-- -----------------------------------------------------------------------------------------------
-- THIS FILE IS FOR CHANGES TO NON-CONFIG TABLES THAT CANNOT BE APPLIED UNTIL RELEASE TIME
-- EXAMPLES:
-- -- data conversion
-- REFER TO config_tables.txt FOR A FULL LIST OF CONFIG TABLES
-- SEE upgradeConfig.sql FOR CONFIG CHANGES
-- -----------------------------------------------------------------------------------------------

-- PICS-4647 Operator Flag Changes email template update for new totals
update email_template t set t.body = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
</head>
<body>
<div style="width: 750px; background-color: #002240; padding: 0 15px 0 15px; margin: 0; color: #6699CC; font-weight: normal; font-family: sans-serif; font-size: 11px; border-bottom: 3px solid #4686bf; border-left: 1px solid #002240; border-right: 1px solid #002240;">
&nbsp;</div>
<div style="width: 750px; padding: 15px; margin: 0; background-color: white; line-height: 18px; font-family: Helvetica, Arial, sans-serif; font-size: 14px; border-left: 1px solid #002240; border-right: 1px solid #002240;">
Below is a list of Operators who have had at least 10 total flag changes as well as 5% of their total flag colors change recently. Please review each one for errors. If you find a flag error, please work with your manager or IT to resolve the problems ASAP. If the flags are due to a valid change, then approve each one and consider reaching out to the operator\'s primary contact to discuss the impact of the changes.
#if(${changes.size()} > 0)
<h3 style="color: rgb(168, 77, 16)">Operator Flag Changes</h3>
<div>
<strong>Total Flag Differences: </strong>${totalFlagChanges}<strong>  |  Total Operators Affected: </strong>${changes.size()}
</div>
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
--

-- PICS-5567 COHS Stattistics table
update pqfdata pd 
join contractor_audit ca on ca.id = pd.auditID 
join pqfdata pd2 on pd2.auditID = pd.auditID 
LEFT join pqfdata pd3 on pd3.auditID = pd.auditID 
set pd3.answer='0.00' 
where pd.questionID=8840 and pd.answer='No' 
and pd2.questionID=2066 and pd2.answer='Yes' 
and (pd3.questionID=11117 OR pd3.questionID=11118);

-- PICS-5758
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'AL');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'AM');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'AT');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'AZ');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'BA');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'BE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'BG');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'BY');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'CH');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'CY');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'CZ');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'DK');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'EE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'EL');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'ES');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'FI');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'GE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'HR');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'HU');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'IE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'IS');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'LI');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'LT');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'LU');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'LV');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'MD');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'ME');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'MK');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'MT');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'NL');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'NO');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'PL');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'PT');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'RO');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'RS');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'SE');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'SI');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'SK');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'TR');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'UA');
insert into user_assignment (userID, createdBy, updatedBy, creationDate, updateDate, assignmentType, country) values (26330, 941, 941, '2012-05-09 12:00', '2012-05-09 12:00', 'CSR', 'XK');