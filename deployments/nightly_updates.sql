-- rebuild the stats for ContractorOperators
TRUNCATE TABLE stats_gco_count;

INSERT INTO stats_gco_count 
SELECT co.opID opID, null, count(*) total FROM contractor_operator co
JOIN accounts a ON a.id = co.opID AND a.type = 'Operator'
GROUP BY co.opID;

INSERT INTO stats_gco_count 
SELECT g1.opID opID, g2.opID opID2, count(*) total
FROM contractor_operator g1
JOIN contractor_operator g2 on g1.conID = g2.conID and g1.id != g2.id
JOIN accounts a1 ON a1.id = g1.opID AND a1.type = 'Operator'
JOIN accounts a2 ON a2.id = g2.opID AND a2.type = 'Operator'
GROUP BY g1.opID, g2.opID;

-- remove old data from the contractor_cron_log
DELETE FROM contractor_cron_log WHERE DATEDIFF(NOW(), startDate) > 7;

/************************** TRADES **************************/
-- ref_trade.contractorCount
UPDATE ref_trade SET contractorCount = 0;

UPDATE ref_trade AS rt, (
	SELECT tradeID, count(ct.id) AS total
	FROM contractor_trade ct GROUP BY tradeID
) AS counts
SET rt.contractorCount = counts.total
WHERE rt.id = counts.tradeID;

UPDATE ref_trade AS rt,
       (SELECT   rt1.id AS id, sum(rt2.contractorCount) AS total
        FROM     ref_trade rt1 JOIN ref_trade rt2 ON rt1.indexStart < rt2.indexStart AND rt1.indexEnd > rt2.indexEnd
        GROUP BY rt1.id) AS counts
SET    rt.contractorCount = counts.total
WHERE  rt.id = counts.id;

update ref_trade set childCount = 0, childCountTotal = 0;

update ref_trade p
join (select parentID, count(*) total FROM ref_trade GROUP BY parentID) c ON p.id = c.parentID
SET p.childCount = c.total;

-- run until now rows are updated (7 times)
update ref_trade p
join (select parentID, count(*) + SUM(childCountTotal) total FROM ref_trade GROUP BY parentID) c ON p.id = c.parentID
SET p.childCountTotal = c.total;
update ref_trade p
join (select parentID, count(*) + SUM(childCountTotal) total FROM ref_trade GROUP BY parentID) c ON p.id = c.parentID
SET p.childCountTotal = c.total;
update ref_trade p
join (select parentID, count(*) + SUM(childCountTotal) total FROM ref_trade GROUP BY parentID) c ON p.id = c.parentID
SET p.childCountTotal = c.total;
update ref_trade p
join (select parentID, count(*) + SUM(childCountTotal) total FROM ref_trade GROUP BY parentID) c ON p.id = c.parentID
SET p.childCountTotal = c.total;
update ref_trade p
join (select parentID, count(*) + SUM(childCountTotal) total FROM ref_trade GROUP BY parentID) c ON p.id = c.parentID
SET p.childCountTotal = c.total;
update ref_trade p
join (select parentID, count(*) + SUM(childCountTotal) total FROM ref_trade GROUP BY parentID) c ON p.id = c.parentID
SET p.childCountTotal = c.total;
update ref_trade p
join (select parentID, count(*) + SUM(childCountTotal) total FROM ref_trade GROUP BY parentID) c ON p.id = c.parentID
SET p.childCountTotal = c.total;

-- clean up invisible CAOs
DELETE from contractor_audit_operator where visible = 0 and status = 'Pending';

delete FROM contractor_audit_operator
WHERE visible = 0 AND auditID IN (
	SELECT id FROM contractor_audit WHERE expiresDate < NOW()
);

-- Opt Out Subscription Inserts
-- OQChanges
insert into email_subscription (id,userID,subscription,timePeriod,lastSent,permission,createdBy,updatedBy,creationDate,updateDate)
select distinct null,u.id,'OQChanges','Monthly',null,null,1,1,now(),now() from users u
join useraccess ua on ua.userID = u.id
join accounts a on u.accountID = a.id
left join email_subscription s on u.id = s.userID and s.subscription = 'OQChanges'
where s.id is null and u.isActive = 'Yes' and a.status = 'Active' and a.requiresOQ = 1 and a.type = 'Contractor' and ua.accessType in ('ContractorAdmin','ContractorSafety');
-- Open Tasks (new subscriptions will be sent out a month after the user is initally added)
insert into email_subscription (id,userID,subscription,timePeriod,lastSent,permission,createdBy,updatedBy,creationDate,updateDate)
select distinct null,u.id,'OpenTasks','Monthly',now(),null,1,1,now(),now() from users u
join accounts a on u.accountID = a.id
left join email_subscription s on u.id = s.userID and s.subscription = 'OpenTasks'
where s.id is null and u.isActive = 'Yes' and a.status = 'Active' and a.type = 'Contractor';