-- PICS-2926
update contractor_info c
join contractor_audit ca on ca.conID = c.id and ca.auditTypeID = 1
join pqfdata d on d.auditID = ca.id and d.questionID = 2444 and d.dateVerified is not null
set c.safetyRiskVerified = d.dateVerified;

update contractor_info c
join (select ca.conID, min(d.dateVerified) dateVerified from contractor_audit ca
join pqfdata d on d.auditID = ca.id and d.questionID in (7678, 7679) and d.dateVerified is not null
where ca.auditTypeID = 1
group by ca.conID) d2 on d2.conID = c.id
set c.productRiskVerified = d2.dateVerified;
-- END

-- PICS-787
update email_template t set t.subject = 'You have Open Tasks at PICS Auditing that require your Action', t.body = '<SubscriptionHeader>
PICS would like to inform you that there are Open Tasks on your account that require your attention. Please review the items below and once you have had a chance to review them, you can either log into your account to view them within your Open Tasks window or click on the links below which will direct you to the specific Open Task you choose. Please keep in mind that if you have any Open Tasks, you will receive this email at the beginning of each month as a reminder. Although this is a list of Open Tasks as of the beginning of the month, please keep in mind that additional Open Tasks might be created throughout the month. Please check your Open Tasks as often as possible.
<br/><br/>
<table style="border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;">
 <thead>
  <tr style="vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;">
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Current Open Tasks</td>
  </tr>
 </thead>
 <tbody>
#foreach( $task in $tasks )
  <tr style="margin:0px">
   <td style="border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;">${task}</td>
  </tr>
#end
 </tbody>
</table>

<TimeStampDisclaimer>
<SubscriptionFooter>' 
where t.id = 168;

-- subscribing all contractors to OpenTasks, only run this once a Contractor Email Subscription Cron has been set up 
/* insert into email_subscription 
	(id, 
	userID, 
	subscription, 
	timePeriod, 
	lastSent, 
	permission, 
	createdBy, 
	updatedBy, 
	creationDate, 
	updateDate
	)
select null, u.id, 'OpenTasks', 'Monthly', null, null, 1, 1, now(), now() from accounts a
join contractor_info c on c.id = a.id
join users u on c.id = u.accountID
where a.status in ('Active','Pending'); */
--
-- END

-- PICS-3021
-- delete all invisible caos
delete from contractor_audit_operator where id in (select id from (
select cao.id from contractor_audit ca
join accounts a on ca.conID = a.id and a.type = 'Contractor' and a.status = 'Active'
join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 0
where ca.auditTypeID in (2)
) aa);

-- insert new pics global cao with data from later on workflow
insert into contractor_audit_operator (auditID, opID, createdBy, updatedBy, creationDate, updateDate, status, visible, percentVerified, percentComplete, statusChangedDate)
select distinct ca.id, 4, 37951, 37951, now(), now(), cao.status, 1, max(cao.percentVerified), max(cao.percentComplete), now()
from contractor_audit ca
join accounts a on ca.conID = a.id and a.type = 'Contractor' and a.status = 'Active'
join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 1
where ca.auditTypeID in (2)
group by ca.id;

-- delete all other caos
delete from contractor_audit_operator where id in (select id from (
select cao.id from contractor_audit ca
join accounts a on ca.conID = a.id and a.type = 'Contractor' and a.status = 'Active'
join contractor_audit_operator cao on ca.id = cao.auditID and cao.opID != 4 and cao.visible = 1
where ca.auditTypeID in (2)
) aa);

-- Script to reset the cron for all those contractors who have a new CAO. 
update contractor_info ci
join accounts a on ci.id = a.id
join contractor_audit ca on ci.id = ca.conID and ca.auditTypeID = 2
join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 1 and cao.opID = 4 and cao.creationDate > curdate()
set ci.needsRecalculation = ci.needsRecalculation + 2, ci.lastRecalculation = null
where a.type = 'Contractor' and a.status = 'Active'
-- END