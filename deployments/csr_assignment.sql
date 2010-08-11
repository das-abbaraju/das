-- Ashley Prather
update contractor_info c, accounts a set welcomeAuditor_id = 22223
where c.id = a.id
and a.state in ('AK','AZ','CO','HI','ID','MT','NM','NV','OR','UT','WA','WY') 
AND a.country = 'US';

-- Derrick Piper
update contractor_info c, accounts a set welcomeAuditor_id = 23542
where c.id = a.id
and (a.state in ('TX'));

-- Estevan Orozco
update contractor_info c, accounts a set welcomeAuditor_id = 940
where c.id = a.id
and (a.state in ('AS','CA','GU','MP','PR','UM','VI') OR a.country not in ('US','CA'));

-- Kaitlyn O'Malley
update contractor_info c, accounts a set welcomeAuditor_id = 11067
where c.id = a.id
and a.state in ('AR','IA','KS','LA','MO','ND','NE','OK','SD','WI')
AND a.country = 'US';

-- Chris Jimenez
update contractor_info c, accounts a set welcomeAuditor_id =  24798
where c.id = a.id
and a.state in ('AL','FL','GA','KY','OH','TN')
AND a.country = 'US';

-- Tiffany Roberson
update contractor_info c, accounts a set welcomeAuditor_id = 22222
where c.id = a.id
and a.state in ('IL','IN','MI','MN','MS')
AND a.country = 'US';

-- Valeree Claudio
update contractor_info c, accounts a set welcomeAuditor_id = 8397
where c.id = a.id
and a.state in ('CT','DC','DE','MA','MD','ME','NC','NH','NJ','NY','PA','RI','SC','VA','VT','WV')
AND a.country = 'US';

-- Gary Abenaim
update contractor_info c, accounts a set welcomeAuditor_id = 24143
where c.id = a.id
and (a.country = 'CA');

-- update auditor with CSR for these contractors for PQF and Annual Update
update contractor_audit ca, contractor_info c set ca.auditorid =
c.welcomeAuditor_id
where ca.conid = c.id
and auditTypeid in 
(select id from audit_type where (classtype in ('PQF') or id
in (9,11)))
and auditStatus in ('Pending','Submitted','Incomplete');

-- update auditor with CSR for these contractors for Policies
update contractor_audit ca, contractor_info c, contractor_audit_operator cao set ca.auditorid = c.welcomeAuditor_id
where ca.conid = c.id
and audittypeid in
(select id from audit_type where (classtype in ('Policy')))
and auditStatus != 'Expired'
and ca.id = cao.auditid
and cao.status in ('Pending','Submitted');

-- Assigning Tiffany as BP IISN case management auditor
update contractor_audit ca set ca.auditorid = 22222, ca.closingAuditorID = 22222
where auditTypeid in 
(select id from audit_type where id = 96)
and auditStatus in ('Pending','Submitted','Incomplete');

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

-- rebuild stats for AppIndex
TRUNCATE TABLE app_index_stats;
INSERT INTO app_index_stats SELECT indexType, NULL, count(distinct foreignKey) FROM app_index GROUP BY indexType;
INSERT INTO app_index_stats SELECT NULL, value, count(*) FROM app_index GROUP BY value;
INSERT INTO app_index_stats SELECT indexType, value, count(*) FROM app_index GROUP BY indexType, value;
ANALYZE TABLE app_index, app_index_stats;