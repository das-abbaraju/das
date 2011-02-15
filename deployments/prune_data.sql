/*

prune note -- by date

prune generalcontractors
app_index -- ask David
rerun app_index_stats
prune accounts -- remove demos and pics
;

*/

-- Audits

delete d
from contractor_audit_operator_workflow d
JOIN contractor_audit_operator cao on cao.id = d.caoID
JOIN contractor_audit ca ON cao.auditID = ca.id
JOIN accounts a ON a.id = ca.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from contractor_audit_operator_permission d
JOIN contractor_audit_operator cao on cao.id = d.caoID
JOIN contractor_audit ca ON cao.auditID = ca.id
JOIN accounts a ON a.id = ca.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete cao
from contractor_audit_operator cao
JOIN contractor_audit ca ON cao.auditID = ca.id
JOIN accounts a ON a.id = ca.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from audit_cat_data d
JOIN contractor_audit ca on d.auditID = ca.id
JOIN accounts a ON a.id = ca.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from pqfdata d
JOIN contractor_audit ca on d.auditID = ca.id
JOIN accounts a ON a.id = ca.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from contractor_audit_file d
JOIN contractor_audit ca on d.auditID = ca.id
JOIN accounts a ON a.id = ca.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from contractor_audit d 
JOIN accounts a ON a.id = d.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

-- Tags

delete d
from flag_criteria_operator d
join operator_tag t on d.tagID = t.id
JOIN accounts a ON a.id = t.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from audit_category_rule d
join operator_tag t on d.tagID = t.id
JOIN accounts a ON a.id = t.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from audit_type_rule d
join operator_tag t on d.tagID = t.id
JOIN accounts a ON a.id = t.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from contractor_tag d
join operator_tag t on d.tagID = t.id
JOIN accounts a ON a.id = t.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from contractor_tag d
JOIN accounts a ON a.id = d.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from operator_tag d
JOIN accounts a ON a.id = d.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

-- Flags

truncate flag_archive;

delete d
from flag_criteria_operator d
JOIN accounts a ON a.id = d.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from flag_criteria_contractor d
JOIN accounts a ON a.id = d.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from flag_data d
JOIN accounts a ON a.id = d.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from flag_data d
JOIN accounts a ON a.id = d.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from flag_data_override d
JOIN accounts a ON a.id = d.opID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from flag_data_override d
JOIN accounts a ON a.id = d.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

-- Users

delete d
from users d
JOIN accounts a ON a.id = d.accountID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');


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

-- Accounts

delete d
from note d
JOIN accounts a ON a.id = d.accountID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

delete d
from contractor_registration_request d
JOIN accounts a ON a.id = d.conID AND a.status != 'Demo' AND a.type IN ('Contractor','Operator','Corporate');

-- Misc

truncate email_queue;

truncate app_error_log;

truncate contractor_cron_log;