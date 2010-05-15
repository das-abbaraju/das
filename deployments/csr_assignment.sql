-- Ashley Prather
update contractor_info c, accounts a set welcomeAuditor_id = 22223
where c.id = a.id
and welcomeAuditor_id is null
and a.state in ('AK','AZ','HI','ID','MT','NM','NV','OR','UT','WY') 
AND a.country = 'US';

-- Derrick Piper
update contractor_info c, accounts a set welcomeAuditor_id = 23542
where c.id = a.id
and welcomeAuditor_id is null
and (a.state in ('TX') OR a.country = 'CA');

-- Estevan Orozco
update contractor_info c, accounts a set welcomeAuditor_id = 940
where c.id = a.id
and welcomeAuditor_id is null
and (a.state in ('CA','GU','PR','WA') OR a.country not in ('US','CA'));

-- Kaitlyn O'Malley
update contractor_info c, accounts a set welcomeAuditor_id = 11067
where c.id = a.id
and welcomeAuditor_id is null
and a.state in ('AR','CO','IA','KS','LA','MO','ND','NE','OK','SD','WI')
AND a.country = 'US';

-- Neal Chawla
update contractor_info c, accounts a set welcomeAuditor_id = 23550
where c.id = a.id
and welcomeAuditor_id is null
and a.state in ('AL','FL','GA','KY','OH','TN')
AND a.country = 'US';

-- Tiffany Roberson
update contractor_info c, accounts a set welcomeAuditor_id = 22222
where c.id = a.id
and welcomeAuditor_id is null
and a.state in ('IL','IN','MI','MN','MS')
AND a.country = 'US';

-- Valeree Claudio
update contractor_info c, accounts a set welcomeAuditor_id = 8397
where c.id = a.id
and welcomeAuditor_id is null
and a.state in ('CT','DE','MA','MD','ME','NC','NH','NJ','NY','PA','RI','SC','VA','VT','WV')
AND a.country = 'US';


-- update auditor with CSR for these contractors
update contractor_audit ca, contractor_info c set ca.auditorid =
c.welcomeAuditor_id
where ca.conid = c.id
and ca.auditorID is null
and audittypeid in
(select id from audit_type where (classtype in ('PQF','Policy') or id
in (9,11)))
and auditStatus in ('Pending','Submitted','Incomplete');