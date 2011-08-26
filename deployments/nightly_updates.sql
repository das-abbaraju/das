-- rebuild the stats for ContractorOperators
TRUNCATE TABLE stats_gco_count;

INSERT INTO stats_gco_count 
SELECT gc.genID opID, null, count(*) total FROM generalcontractors gc 
JOIN accounts a ON a.id = gc.genID AND a.type = 'Operator' 
GROUP BY gc.genID; 

INSERT INTO stats_gco_count 
SELECT g1.genID opID, g2.genID opID2, count(*) total 
FROM generalcontractors g1 
JOIN generalcontractors g2 on g1.subID = g2.subID and g1.id != g2.id 
JOIN accounts a1 ON a1.id = g1.genID AND a1.type = 'Operator' 
JOIN accounts a2 ON a2.id = g2.genID AND a2.type = 'Operator' 
GROUP BY g1.genID, g2.genID;

update contractor_audit_operator set status = 'Expired' 
where status != 'Expired' AND auditID IN (
	select ca.id from contractor_audit ca JOIN audit_type aType ON aType.id = ca.auditTypeID where ca.expiresDate < NOW() AND aType.renewable = 0
);

-- remove old data from the contractor_cron_log
DELETE FROM contractor_cron_log WHERE DATEDIFF(NOW(), startDate) > 7;

------------------------- TRADES -----------------------------
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

update ref_trade set parentID = null where parentID = 5;

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

DELETE from contractor_audit_operator where visible = 0 and status = 'Pending';

-- Opt Out Subscription Inserts
-- OQChanges
insert into email_subscription (id,userID,subscription,timePeriod,lastSent,permission,createdBy,updatedBy,creationDate,updateDate)
select distinct null,u.id,'OQChanges','Monthly',null,null,1,1,now(),now() from users u
join useraccess ua on ua.userID = u.id
join accounts a on u.accountID = a.id
left join email_subscription s on u.id = s.userID and s.subscription = 'OQChanges'
where s.id is null and u.isActive = 'Yes' and a.status = 'Active' and a.requiresOQ = 1 and a.type = 'Contractor' and ua.accessType in ('ContractorAdmin','ContractorSafety');