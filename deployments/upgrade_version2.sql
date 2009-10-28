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

/**
 * Update the invoice Fee with Bid Only Account
 */
update invoice_fee set fee="Bid Only Account Fee", 
qbFullName = "Bid Only Account Fee",updateDate = Now() 
where
id = 100;

/**
* update the description from the contractor to Account
*/
update accounts a, contractor_info c set a.description = c.description
where a.id = c.id;

/**
 * Change email token for AuditScheduledDate
 */
update token
set velocityCode = "${pics_dateTool.format('EEE d MMM h:mm a z', $audit.scheduledDate, $user.locale, $user.timezoneObject)}"
where tokenID = 16;
