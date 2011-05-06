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

-- rebuild stats for AppIndex
TRUNCATE TABLE app_index_stats;
INSERT INTO app_index_stats SELECT indexType, NULL, count(distinct foreignKey) FROM app_index GROUP BY indexType;
INSERT INTO app_index_stats SELECT NULL, value, count(*) FROM app_index GROUP BY value;
INSERT INTO app_index_stats SELECT indexType, value, count(*) FROM app_index GROUP BY indexType, value;
ANALYZE TABLE app_index, app_index_stats;

-- remove old data from the contractor_cron_log
DELETE FROM contractor_cron_log WHERE DATEDIFF(NOW(), startDate) > 7;

-- ref_trade.contractorCount
UPDATE ref_trade AS rt,
       (SELECT   rt.id AS id, count(ct.id) AS count
        FROM     ref_trade rt JOIN contractor_trade ct ON ct.tradeID = rt.id
        GROUP BY rt.id) AS counts
SET    rt.contractorCount = counts.count
WHERE  rt.id = counts.id;