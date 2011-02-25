select t.TABLE_NAME, t.DATA_LENGTH, t.INDEX_LENGTH, t.TABLE_ROWS
from information_schema.TABLES t where t.TABLE_SCHEMA = 'pics_pruned' and t.TABLE_TYPE = 'BASE TABLE'
order by t.DATA_LENGTH + t.INDEX_LENGTH desc
limit 100;

/*

prune generalcontractors
app_index -- ask David
rerun app_index_stats
;

*/

-- Audits

TRUNCATE TABLE email_queue;

TRUNCATE TABLE flag_archive;

TRUNCATE TABLE app_error_log;

TRUNCATE TABLE contractor_cron_log;

TRUNCATE TABLE email_attachment;

TRUNCATE TABLE loginlog;

truncate ncms_desktop;
truncate ncms_contractors;

CREATE TABLE accounts_to_prune AS
SELECT id FROM accounts
WHERE status != 'Demo' AND type IN ('Contractor','Operator','Corporate');

delete d
FROM generalcontractors d
JOIN accounts_to_prune a ON a.id = d.subID;

delete d
FROM generalcontractors d
JOIN accounts_to_prune a ON a.id = d.genID;

delete d
FROM facilities d
JOIN accounts_to_prune a ON a.id = d.opID;

delete d
FROM facilities d
JOIN accounts_to_prune a ON a.id = d.corporateID;

delete d
from contractor_audit_operator_workflow d
JOIN contractor_audit_operator cao on cao.id = d.caoID AND cao.visible = 0;

delete d
from contractor_audit_operator_permission d
JOIN contractor_audit_operator cao on cao.id = d.caoID AND cao.visible = 0;

delete FROM contractor_audit_operator WHERE visible = 0;

delete d
from contractor_audit_operator_workflow d
JOIN contractor_audit_operator cao on cao.id = d.caoID
JOIN contractor_audit ca ON cao.auditID = ca.id
JOIN accounts_to_prune a ON a.id = ca.conID;

delete d
from contractor_audit_operator_permission d
JOIN contractor_audit_operator cao on cao.id = d.caoID
JOIN contractor_audit ca ON cao.auditID = ca.id
JOIN accounts_to_prune a ON a.id = ca.conID;

delete cao
from contractor_audit_operator cao
JOIN contractor_audit ca ON cao.auditID = ca.id
JOIN accounts_to_prune a ON a.id = ca.conID;

delete d
from audit_cat_data d
JOIN contractor_audit ca on ca.id = d.auditID AND ca.expiresDate < Now()
Where d.applies = 0;

delete d
from audit_cat_data d
JOIN contractor_audit ca on d.auditID = ca.id
JOIN accounts_to_prune a ON a.id = ca.conID;

delete d
from pqfdata d
JOIN contractor_audit ca on d.auditID = ca.id
JOIN accounts_to_prune a ON a.id = ca.conID;

delete d
from PQFDATA_EMPLOYEES d
JOIN accounts_to_prune a on a.id = d.conID;

delete d
from contractor_audit_file d
JOIN contractor_audit ca on d.auditID = ca.id
JOIN accounts_to_prune a ON a.id = ca.conID;

delete d
from contractor_audit d 
JOIN accounts_to_prune a ON a.id = d.conID;

-- Tags

delete d
from flag_criteria_operator d
join operator_tag t on d.tagID = t.id
JOIN accounts_to_prune a ON a.id = t.opID;

delete d
from audit_category_rule d
join operator_tag t on d.tagID = t.id
JOIN accounts_to_prune a ON a.id = t.opID;

delete d
from audit_type_rule d
join operator_tag t on d.tagID = t.id
JOIN accounts_to_prune a ON a.id = t.opID;

delete d
from contractor_tag d
join operator_tag t on d.tagID = t.id
JOIN accounts_to_prune a ON a.id = t.opID;

delete d
from contractor_tag d
JOIN accounts_to_prune a ON a.id = d.conID;

delete d
from operator_tag d
JOIN accounts_to_prune a ON a.id = d.opID;

-- Flags

truncate flag_archive;
truncate flag_criteria_operator;
truncate flag_criteria_contractor;
truncate flag_data;

delete d
from flag_data_override d
JOIN accounts_to_prune a ON a.id = d.opID OR a.id = d.conID;

-- Users

delete d
from users d
JOIN accounts_to_prune a ON a.id = d.accountID;


-- Employees

delete d
from employee_site d
join employee e on d.employeeID = e.id
JOIN accounts a ON a.id = e.accountID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from employee_role d
join employee e on r.employeeID = e.id
JOIN accounts a ON a.id = e.accountID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from employee_qualification q
join employee e on q.employeeID = e.id
JOIN accounts a ON a.id = e.accountID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from employee_competency d
join employee e on d.employeeID = e.id
JOIN accounts a ON a.id = e.accountID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from employee_assessment_authorization d
join employee e on d.employeeID = e.id
JOIN accounts a ON a.id = e.accountID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from job_competency d
join operator_competency c on d.competencyID = c.id
JOIN accounts a ON a.id = c.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from job_competency_stats d
join operator_competency c on d.competencyID = c.id
JOIN accounts a ON a.id = c.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from operator_competency d
JOIN accounts a ON a.id = d.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from job_site d
JOIN accounts a ON a.id = d.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from note d
join employee e on d.employeeID = e.id
JOIN accounts a ON a.id = e.accountID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from employee d
JOIN accounts a ON a.id = d.accountID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');


delete d
from invoice_item d
JOIN invoice i ON d.invoiceID = i.id
JOIN accounts_to_prune a on a.id = i.accountID;

delete d
from invoice d
JOIN accounts_to_prune a on a.id = d.accountID;


-- Accounts

delete d
from audit_type_rule d
JOIN accounts_to_prune a on a.id = d.opID;

delete from audit_type_rule
WHERE expirationDate < NOW();

delete d
from audit_category_rule d
JOIN accounts_to_prune a on a.id = d.opID;

delete from audit_category_rule
WHERE expirationDate < NOW();


delete d
from note d
JOIN accounts_to_prune a ON a.id = d.accountID;

delete d
from contractor_registration_request d
JOIN accounts_to_prune a ON a.id = d.conID;

-- Misc

delete d
FROM operators d
JOIN accounts_to_prune a ON a.id = d.id;

delete d
FROM contractor_info d
JOIN accounts_to_prune a ON a.id = d.id;

delete d
FROM accounts d
JOIN accounts_to_prune a ON a.id = d.id;

update operators d
JOIN accounts_to_prune a ON a.id = d.parentID
SET d.parentID = null;

update operators d
JOIN accounts_to_prune a ON a.id = d.inheritInsuranceCriteria
SET d.inheritInsuranceCriteria = null;

update operators d
JOIN accounts_to_prune a ON a.id = d.inheritFlagCriteria
SET d.inheritFlagCriteria = null;

update contractor_info d
JOIN accounts_to_prune a ON a.id = d.requestedByID
SET d.requestedByID = null;

update accounts a
LEFT JOIN users u ON a.contactID = u.id
SET a.contactID = null
WHERE u.id IS NULL;

delete from note
where creationDate < DATE_SUB(now(), INTERVAL 1 MONTH);

delete d
from app_index d
JOIN accounts_to_prune a on a.id = d.foreignKey AND d.indexType IN ('A','C','O','CO','AS');

delete d
from app_index d
LEFT JOIN users a on a.id = d.foreignKey AND d.indexType = 'U'
where a.id is null;

delete d
from useraccess d
LEFT JOIN users a on a.id = d.userID
where a.id is null;

delete d
from user_switch d
LEFT JOIN users a on a.id = d.userID
where a.id is null;

delete d
from app_index d
LEFT JOIN employee a on a.id = d.foreignKey AND d.indexType = 'U'
where a.id is null;


TRUNCATE TABLE app_index_stats;
INSERT INTO app_index_stats SELECT indexType, NULL, count(distinct foreignKey) FROM app_index GROUP BY indexType;
INSERT INTO app_index_stats SELECT NULL, value, count(*) FROM app_index GROUP BY value;
INSERT INTO app_index_stats SELECT indexType, value, count(*) FROM app_index GROUP BY indexType, value;
ANALYZE TABLE app_index, app_index_stats;

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

delete p
from invoice_payment p
LEFT JOIN invoice i on i.id = p.paymentID
where i.id is null;

delete p
from invoice_payment p
LEFT JOIN invoice i on i.id = p.refundID
where i.id is null;

delete p
from invoice_payment p
LEFT JOIN invoice i on i.id = p.invoiceID
where i.id is null;

delete d
from certificate d
JOIN accounts_to_prune a on a.id = d.conID;

delete p
from osha_audit p
LEFT JOIN contractor_audit i on i.id = p.auditID
where i.id is null;

DROP TABLE accounts_to_prune;
