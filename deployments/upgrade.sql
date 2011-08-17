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
-- correct all statuses so they are all the same on all visible to invisible caos
UPDATE contractor_audit ca
join accounts a on ca.conID = a.id
join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 1
join contractor_audit_operator cao2 on ca.id = cao2.auditID and cao.id != cao2.id and cao.status != cao2.status
SET cao2.status = cao.status
where ca.auditTypeID = 2;

-- insert pics global where it does not exist
insert into contractor_audit_operator (auditID, opID, createdBy, updatedBy, creationDate, updateDate, status, visible, percentVerified, percentComplete, statusChangedDate)
select distinct ca.id, 4, 37951, 37951, now(), now(), case when cao.status is null then 'Pending' else cao.status end as status, 0, 
case when max(cao.percentVerified) is null then 0 else max(cao.percentVerified) end as percentVerified, 
case when max(cao.percentComplete) is null then 0 else max(cao.percentComplete) end as percentComplete, now()
from contractor_audit ca
left join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 1
where ca.auditTypeID = 2
and not exists (select * from contractor_audit_operator exist where exist.auditID = ca.id and exist.opID = 4)
group by ca.id;

-- create and insert into temp tables with results of counts for each caow
CREATE TABLE IF NOT EXISTS pics_temp.temp_caow_copy
(
id mediumint(20) AUTO_INCREMENT primary key,
auditID mediumint(20),
caoID mediumint(20),
opID mediumInt(20),
countCaow smallint(3)
);

insert into pics_temp.temp_caow_copy (auditID, caoID, opID, countCaow)
select ca.id, cao2.id as caoID, cao2.opID, count(caow.id)
from contractor_audit ca
join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 0 and cao.opID = 4 and ca.auditTypeID = 2
join contractor_audit_operator cao2 on ca.id = cao2.auditID and cao2.opID != 4
join contractor_audit_operator_workflow caow on cao2.id = caow.caoID
group by cao2.id
order by ca.id;

CREATE TABLE IF NOT EXISTS pics_temp.temp_caow_copy_max_value
(
id mediumint(20) AUTO_INCREMENT primary key,
auditID mediumint(20),
caoID mediumint(20),
opID mediumInt(20),
countCaow smallint(3)
);

insert into pics_temp.temp_caow_copy_max_value (auditID, caoID, opID, countCaow)
select auditID, min(caoID) as caoID, opID, countCaow from pics_temp.temp_caow_copy tcc
	where countCaow = (
		select max(countCaow) from pics_temp.temp_caow_copy tcc2 where tcc.auditID = tcc2.auditID
	)
group by auditID;

-- for every invisible manual audit pics global, insert the caows for the matching caoID
insert into contractor_audit_operator_workflow (createdBy, updatedBy, creationDate, updateDate, caoID, status, previousStatus, notes)
select 37951, 37951, now(), now(), cao.id, caow.status, caow.previousStatus, caow.notes
from contractor_audit ca
join contractor_audit_operator cao on ca.id = cao.auditID and cao.visible = 0 and cao.opID = 4
join pics_temp.temp_caow_copy_max_value tcc on ca.id = tcc.auditID
join contractor_audit_operator_workflow caow on tcc.caoID = caow.caoID
where ca.auditTypeID = 2;

-- for every invisible manual audit pics global, update the caops for the matching caoID
update contractor_audit_operator_permission caop
join contractor_audit ca on ca.auditTypeID = 2
join contractor_audit_operator cao on ca.id = cao.auditID and cao.opID = 4
join contractor_audit_operator cao2 on ca.id = cao2.auditID and cao2.id = caop.caoID and cao2.opID != 4
set caop.caoID = cao.id,
caop.previousCaoID = caop.caoID;

-- update all invisible pics globals to visible and all other caos to invisible
update contractor_audit_operator cao
join contractor_audit ca on ca.id = cao.auditID and cao.visible = 0 and cao.opID = 4 and ca.auditTypeID = 2
set visible = 1;

update contractor_audit_operator cao
join contractor_audit ca on ca.id = cao.auditID and cao.visible = 1 and cao.opID != 4 and ca.auditTypeID = 2
set visible = 0;

-- Lucas
-- Find all the deleted audit categories with orphaned child data in audit category data
select distinct acd.categoryID from audit_cat_data acd
left join audit_category ac on acd.categoryID = ac.id
where ac.id is null;
-- Delete them, found 122156 on alpha
DELETE
from audit_cat_data
where categoryID in (28,422,423,424,1196,1197,1796,1797,1824,1828,1835);
-- END

INSERT INTO useraccess (userID, accessType, grantFlag, lastUpdate, grantedByID)
select 33885, 'Debug', 1, NOW(), 941;
