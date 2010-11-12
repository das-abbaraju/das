-- Ashley Prather
update contractor_info c, accounts a set welcomeAuditor_id = 22223
where c.id = a.id
and a.state in ('AZ','ID','MT','NM','NV','OR','UT','WA','WY') 
AND a.country = 'US';

-- Derrick Piper
update contractor_info c, accounts a set welcomeAuditor_id = 23542
where c.id = a.id
and (a.state in ('TX'));

-- Estevan Orozco
update contractor_info c, accounts a set welcomeAuditor_id = 940
where c.id = a.id
and (a.state in ('CA','GU','PR','UM') OR a.country not in ('US','CA'));

-- Kaitlyn O'Malley
-- update contractor_info c, accounts a set welcomeAuditor_id = 11067
-- where c.id = a.id
-- and a.state in ('AR','IA','KS','LA','MO','ND','NE','OK','SD','WI')
-- AND a.country = 'US';

-- Chris Jimenez
update contractor_info c, accounts a set welcomeAuditor_id =  24798
where c.id = a.id
and a.state in ('FL','GA','SC')
AND a.country = 'US';

-- Tiffany Roberson
update contractor_info c, accounts a set welcomeAuditor_id = 22222
where c.id = a.id
and a.state in ('IL','IN','WI')
AND a.country = 'US';

-- Valeree Claudio
update contractor_info c, accounts a set welcomeAuditor_id = 8397
where c.id = a.id
and a.state in ('CT','DE','ME','NH','NJ','NY','RI','VT')
AND a.country = 'US';

-- Mohamed Massaquoi
update contractor_info c, accounts a set welcomeAuditor_id = 11504
where c.id = a.id
and a.state in ('MA','MD','NC','PA','VA','WV')
AND a.country = 'US';

-- Joe Villenueva
update contractor_info c, accounts a set welcomeAuditor_id = 27274
where c.id = a.id
and a.state in ('OH','KY','TN','MS','AL')
AND a.country = 'US';

-- Charlie Lee
update contractor_info c, accounts a set welcomeAuditor_id = 26330
where c.id = a.id
and a.state in ('LA','AR','KS')
AND a.country = 'US';

-- Maryum Anwar
update contractor_info c, accounts a set welcomeAuditor_id = 27275
where c.id = a.id
and a.state in ('IA','MN','OK','MO','ND','NE','SD','MI')
AND a.country = 'US';

-- Kencarol James
update contractor_info c, accounts a set welcomeAuditor_id = 27276
where c.id = a.id
and a.state in ('AK','CO','HI')
AND a.country = 'US';

-- Gary Abenaim
update contractor_info c, accounts a set welcomeAuditor_id = 24143
where c.id = a.id
and (a.country = 'CA');

-- update auditor with CSR for these contractors for PQF and Annual Update
update contractor_audit ca, contractor_info c, contractor_audit_operator cao set ca.auditorid = c.welcomeAuditor_id
where ca.conid = c.id
and audittypeid in
(select id from audit_type where (classtype in ('Policy')))
and expiresDate > NOW()
and ca.id = cao.auditid
and cao.status in ('Pending','Submitted','Incomplete');

-- update auditor with CSR for these contractors for Policies/PQfs/Annual Update and Welcome Call 
update contractor_audit ca, contractor_info c, contractor_audit_operator cao set ca.auditorid = c.welcomeAuditor_id
where ca.conid = c.id
and audittypeid in
(select id from audit_type where (classtype in ('Policy') or classtype in ('PQF') or id in (9,11)))
and expiresDate > NOW()
and ca.id = cao.auditid
and cao.status in ('Pending','Submitted');

-- Assigning Tiffany as BP IISN case management auditor
update contractor_audit ca,  contractor_audit_operator cao set ca.auditorid = 22222, ca.closingAuditorID = 22222
where  ca.id = cao.auditid
and auditTypeid in (select id from audit_type where id = 96)
and cao.status in ('Pending','Submitted','Incomplete');

-- Overrides for Corporate Contractors
-- Joe Villenueva - Airgas
update contractor_info c set welcomeAuditor_id = 27274
where c.id in (5593,11417,11270,10281,7471,6086,7892);

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