-- PICS-1038/PICS-1055
-- insert into invoice_fee (id, fee, defaultAmount, visible, feeClass, qbFullName, createdBy, updatedBy, creationDate, updateDate)
--	values (12, 'PICS UAE Membership for PQF-Only', 500.00, 1, 'Membership', 'PQF-Only', 20952, 20952, now(), NOW()),
--	(13, 'PICS UAE Membership for 1 Operator', 2000.00, 1, 'Membership', '1 Operator', 20952, 20952, now(), NOW()),
--	(14, 'PICS UAE Membership for 2-4 Operators', 3000.00, 1, 'Membership', '2-4 Operators', 20952, 20952, now(), NOW()),
--	(15, 'PICS UAE Membership for 5-8 Operators', 4500.00, 1, 'Membership', '5-8 Operators', 20952, 20952, now(), NOW()),
--	(16, 'PICS UAE Membership for 9-12 Operators', 6000.00, 1, 'Membership', '9-12 Operators', 20952, 20952, now(), NOW()),
--	(17, 'PICS UAE Membership for 13-19 Operators', 8000.00, 1, 'Membership', '13-19 Operators', 20952, 20952, now(), NOW()),
--	(18, 'PICS UAE Membership for 20-49 Operators', 11000.00, 1, 'Membership', '20-49 Operators', 20952, 20952, now(), NOW()),
--	(19, 'PICS UAE Membership for 50+ Operators', 15000.00, 1, 'Membership', '50 Operators', 20952, 20952, now(), NOW());
-- END

--update flag_criteria_operator set criteriaID = 634 
--where criteriaID = 469 and opid not in (13656);
---- PICS-1575, PICS-1461
--alter table note add column `employeeID` int(11) NULL after `followupDate`;

-- PICS-1736
insert into `email_template`(`id`,`accountID`,`templateName`,`subject`,`body`,`createdBy`,`creationDate`,`updatedBy`,`updateDate`,`listType`,`allowsVelocity`,`html`,`recipient`) values ( NULL,'1100','Recent OQ Changes','Recent Operator Qualification Changes','<SubscriptionHeader>\r\nBelow are all the Operator Qualification changes since $pics_dateTool.format(\'MM/dd/yy\', $date).<br/><br/>\r\n#if(${criteriaList.size()} > 0)\r\n<h3 style=\"color: rgb(168, 77, 16)\">Job Task Criteria Changes (${criteriaList.size()})</h3>\r\n<table style=\"border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;\">\r\n <thead>\r\n  <tr style=\"vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;\">\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Type</td>\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Task</td>\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Criteria</td>\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Action</td>\r\n  </tr>\r\n </thead>\r\n <tbody>\r\n  #foreach( $criteria in $criteriaList )\r\n  <tr style=\"margin:0px\">\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">${criteria.get(\'taskType\')}</td>\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">${criteria.get(\'task\')}</td>\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">${criteria.get(\'criteria\')}</td>\r\n   #if(${criteria.get(\'daysFromExpiration\')} > 0)\r\n    <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">Added $pics_dateTool.format(\'MMM d\', ${criteria.get(\'effectiveDate\')})</td>\r\n   #else\r\n    <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">Removed $pics_dateTool.format(\'MMM d\', ${criteria.get(\'expirationDate\')})</td>\r\n   #end\r\n  </tr>\r\n  #end\r\n </tbody>\r\n</table> \r\n#end\r\n#if(${sitesList.size()} > 0)\r\n<h3 style=\"color: rgb(168, 77, 16)\">Job Task sites Changes (${sitesList.size()})</h3>\r\n<table style=\"border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;\">\r\n <thead>\r\n  <tr style=\"vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;\">\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Type</td>\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Task</td>\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Site</td>\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Action</td>\r\n  </tr>\r\n </thead>\r\n <tbody>\r\n  #foreach( $sites in $sitesList )\r\n  <tr style=\"margin:0px\">\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">${sites.get(\'taskType\')}</td>\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">${sites.get(\'task\')}</td>\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">${sites.get(\'opName\')}: ${sites.get(\'name\')}</td>\r\n   #if(${sites.get(\'daysFromExpiration\')} > 0)\r\n    <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">Added $pics_dateTool.format(\'MMM d\', ${sites.get(\'effectiveDate\')})</td>\r\n   #else\r\n    <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">Removed $pics_dateTool.format(\'MMM d\', ${sites.get(\'expirationDate\')})</td>\r\n   #end\r\n  </tr>\r\n  #end\r\n </tbody>\r\n</table> \r\n#end\r\n<TimeStampDisclaimer>\r\n<SubscriptionFooter>','23157',NOW(),NULL,NULL,'Contractor','1','1',NULL);

-- PICS-1734
update contractor_audit SET effectiveDate = NULL;

-- 70,246 rows
update contractor_audit as ca
  join (select ca.id, max(caow.creationDate) uDate
        from contractor_audit ca
          join contractor_audit_operator cao on cao.auditID = ca.id
          join contractor_audit_operator_workflow caow on caow.caoID = cao.id
        where caow.status IN ('Submitted','Resubmitted')
        group by ca.id) as r
    on ca.id = r.id
set ca.effectiveDate = r.uDate
WHERE effectiveDate IS null;

update contractor_audit as ca
  join (select ca.id, max(caow.creationDate) uDate
        from contractor_audit ca
          join contractor_audit_operator cao on cao.auditID = ca.id
          join contractor_audit_operator_workflow caow on caow.caoID = cao.id
        where caow.status IN ('Complete','Approved')
        group by ca.id) as r
    on ca.id = r.id
set ca.effectiveDate = r.uDate
WHERE effectiveDate IS null;

update contractor_audit
set effectiveDate = creationDate
WHERE effectiveDate IS null;

update contractor_audit SET effectiveDate = NULL WHERE auditTypeID = 1;


-- END